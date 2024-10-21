package com.mobile.app.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.app.entity.FBOApprovalWorkflow;
import com.mobile.app.entity.WhatsAppWorkflow;
import com.mobile.app.enums.ApprovalStatus;
import com.mobile.app.enums.ApprovalType;
import com.mobile.app.repository.WhatsAppWorkflowRepo;
import com.mobile.app.repository.WorkflowRepo;
import com.mobile.app.request.WorkflowApprovalRequest;

@Service
public class WhatsAppService
{

	private static final Logger LOG = LoggerFactory.getLogger(WhatsAppService.class);

	private static final String IND_CALLING_CODE = "91";

	@Value("${whatsapp.phone.number.id}")
	private String m11PhoneNumberId;

	@Value("${whatsapp.url}")
	private String whatsAppUrl;

	@Value("${whatsapp.access.token}")
	private String accessToken;

	@Value("${whatsapp.template.name}")
	private String templateName;

	@Autowired
	private WorkflowRepo workflowRepo;

	@Autowired
	private WhatsAppWorkflowRepo whatsAppWorkflowRepo;

	@Autowired
	private WorkflowService workflowService;

	public void sendWhatsAppMessage(final Integer workflowId, final String fboName, final String fboContactNo,
					final Double weight, final Double price)
	{
		try
		{
			final FBOApprovalWorkflow workflowData = workflowRepo.findById(workflowId).orElse(null);
			if (Objects.nonNull(workflowData))
			{
				final Integer poId = workflowData.getPurchaseOrder();
				final WhatsAppWorkflow whatsAppWorkflow = saveWhatsAppWorkflow(workflowId, poId, fboContactNo, weight, price);
				sendWhatsAppTemplateMessage(whatsAppWorkflow, fboName, "template", templateName);
			}
			else
			{
				LOG.info("Workflow Data Not Available");
			}
		}
		catch (final Exception e)
		{
			LOG.error("sendWhatsAppMessages() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
	}

	private WhatsAppWorkflow saveWhatsAppWorkflow(final Integer workflowId, final Integer poId, final String fboContactNo,
					final Double weight, final Double price)
	{
		final WhatsAppWorkflow whatsAppWorkflow = new WhatsAppWorkflow();
		whatsAppWorkflow.setWorkflowId(workflowId);
		whatsAppWorkflow.setPoId(poId);
		whatsAppWorkflow.setFboContact(fboContactNo);
		whatsAppWorkflow.setWeight(weight);
		whatsAppWorkflow.setPrice(price);
		return whatsAppWorkflowRepo.save(whatsAppWorkflow);
	}

	public void handleIncomingMessages(final String payload)
	{
		try
		{
			LOG.info("handleIncomingMessages() - Message received from the user: " + payload);
			final JsonNode jsonNode = new ObjectMapper().readTree(payload);

			if (jsonNode.has("object") && jsonNode.get("object").asText().equals("whatsapp_business_account"))
			{
				final JsonNode entry = jsonNode.get("entry").get(0);
				if (entry != null && entry.has("changes") && entry.get("changes").size() > 0)
				{
					final JsonNode changes = entry.get("changes").get(0);
					if (changes != null && changes.has("value"))
					{
						final JsonNode value = changes.get("value");
						if (value.has("messages") && !value.get("messages").isEmpty())
						{
							final JsonNode messages = value.get("messages").get(0);
							final String fboContactNo = messages.get("from").asText().substring(2);

							final WhatsAppWorkflow whatsAppWorkflow = whatsAppWorkflowRepo
											.findByFboContactAndRecentCreationTime(fboContactNo);
							if (Objects.nonNull(whatsAppWorkflow))
							{
								handleWhatsAppWorkflow(whatsAppWorkflow, messages);
							}
						}
					}
				}
			}
		}
		catch (final IOException e)
		{
			LOG.error("handleIncomingMessages() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
	}

	private void handleWhatsAppWorkflow(final WhatsAppWorkflow whatsAppWorkflow, final JsonNode messages)
	{
		final Integer workflowId = whatsAppWorkflow.getWorkflowId();
		final FBOApprovalWorkflow workflowData = workflowRepo.findById(workflowId).orElse(null);
		if (Objects.nonNull(workflowData)
						&& ApprovalStatus.APPROVED.toString().equalsIgnoreCase(workflowData.getApprovalStatus().toString()))
		{
			return;
		}

		String requestBody = "";
		final String msgReplyBody = getMessageBody(messages);
		LOG.info("handleWhatsAppWorkflow() - Message received from the user: " + msgReplyBody);
		if ("Approve".equalsIgnoreCase(msgReplyBody))
		{
			requestBody = createRequestBody(whatsAppWorkflow, null, "text", null, "Thank you for your Approval.");
			processApprovalRequest(workflowId);
		}
		else
		{
			requestBody = createRequestBody(whatsAppWorkflow, null, "text", null, "Please click on *Approve* button above.");
		}

		try
		{
			final HttpRequest httpRequest = createPostRequest(requestBody);
			buildHttpClientAndSendRequest(httpRequest);
		}
		catch (final URISyntaxException e)
		{
			LOG.error("handleWhatsAppWorkflow() - URISyntaxException occurred. Message: [{}]", e.getMessage(), e);
		}
	}

	private String getMessageBody(final JsonNode messages)
	{
		if ("button".equalsIgnoreCase(messages.get("type").asText()))
		{
			return messages.get("button").get("text").asText();
		}
		else
		{
			return messages.get("text").get("body").asText();
		}
	}

	private void processApprovalRequest(final Integer workflowId)
	{
		final WorkflowApprovalRequest approvalRequest = new WorkflowApprovalRequest();
		approvalRequest.setWorkFlowId(workflowId);
		approvalRequest.setApprovalType(ApprovalType.PAYMENT.toString());
		approvalRequest.setApprovalStatus(ApprovalStatus.APPROVED.toString());
		approvalRequest.setComments("Approved via WhatsApp");
		workflowService.fboPaymentApprovalWorkflow(approvalRequest);
	}

	private void sendWhatsAppTemplateMessage(final WhatsAppWorkflow whatsAppWorkflow, final String fboName, final String type,
					final String templateName)
	{
		try
		{
			final String requestBody = createRequestBody(whatsAppWorkflow, fboName, type, templateName, null);
			LOG.info("sendWhatsAppTemplateMessage() - Message request body: " + requestBody);
			final HttpRequest httpRequest = createPostRequest(requestBody);
			buildHttpClientAndSendRequest(httpRequest);
		}
		catch (final URISyntaxException e)
		{
			LOG.error("sendWhatsAppTemplateMessage() - URISyntaxException occurred. Message: [{}]", e.getMessage(), e);
		}
	}

	private String createRequestBody(final WhatsAppWorkflow whatsAppWorkflow, final String fboName, final String type,
					final String templateName, final String customBody)
	{
		final StringBuilder requestBuilder = new StringBuilder("{");
		requestBuilder.append("\"messaging_product\": \"whatsapp\", ");
		if ("text".equalsIgnoreCase(type))
		{
			requestBuilder.append("\"recipient_type\": \"individual\", ");
			requestBuilder.append("\"to\": \"").append(IND_CALLING_CODE).append(whatsAppWorkflow.getFboContact()).append("\", ");
			requestBuilder.append("\"text\": { \"body\": \"").append(customBody).append("\" }");
		}
		else
		{
			requestBuilder.append("\"to\": \"").append(IND_CALLING_CODE).append(whatsAppWorkflow.getFboContact()).append("\", ");
			requestBuilder.append("\"type\": \"template\", ");
			requestBuilder.append("\"template\": { \"name\": \"").append(templateName).append("\", ");
			requestBuilder.append("\"language\": { \"code\": \"en_US\" }, ");
			requestBuilder.append("\"components\": [ { \"type\": \"body\", ");
			requestBuilder.append("\"parameters\": [ { \"type\": \"text\", \"text\": \"").append(fboName).append("\" }, ");
			requestBuilder.append("{ \"type\": \"text\", \"text\": \"").append(whatsAppWorkflow.getPoId()).append("\" }, ");
			requestBuilder.append("{ \"type\": \"text\", \"text\": \"").append(whatsAppWorkflow.getWeight()).append("\" }, ");
			requestBuilder.append("{ \"type\": \"text\", \"text\": \"").append(whatsAppWorkflow.getPrice())
							.append("\" } ] } ] }");
		}
		requestBuilder.append("}");
		return requestBuilder.toString();
	}

	private HttpRequest createPostRequest(final String requestBody) throws URISyntaxException
	{
		try
		{
			return HttpRequest.newBuilder().uri(new URI(whatsAppUrl)).header("Content-Type", "application/json")
							.header("Authorization", "Bearer " + accessToken)
							.POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
		}
		catch (final URISyntaxException e)
		{
			throw new IllegalArgumentException("Invalid WhatsApp URL", e);
		}
	}

	private void buildHttpClientAndSendRequest(final HttpRequest httpRequest)
	{
		final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
		try
		{
			final HttpResponse<String> response = httpClient.send(httpRequest, BodyHandlers.ofString());
			LOG.info(response.body());
		}
		catch (final IOException | InterruptedException e)
		{
			LOG.error("buildHttpClientAndSendRequest() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
	}

}
