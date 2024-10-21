package com.mobile.app.service;

import java.util.ArrayList;
import com.mobile.app.response.DeliveryOrdersResponse;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cashfree.lib.payout.domains.response.GetTransferResponse;
import com.mobile.app.entity.Employee;
import com.mobile.app.entity.FBOApprovalWorkflow;
import com.mobile.app.entity.FoodBusinessOperator;
import com.mobile.app.entity.WeighmentData;
import com.mobile.app.enums.ApprovalStatus;
import com.mobile.app.enums.ApprovalType;
import com.mobile.app.enums.OrderStatus;
import com.mobile.app.enums.PaymentModes;
import com.mobile.app.repository.EmployeeRepo;
import com.mobile.app.repository.FboRepo;
import com.mobile.app.repository.VehicleRepo;
import com.mobile.app.repository.WeighmentDataRepo;
import com.mobile.app.repository.WorkflowRepo;
import com.mobile.app.request.PaymentForm;
import com.mobile.app.request.WorkflowApprovalRequest;
import com.mobile.app.response.BeneficiaryData;
import com.mobile.app.response.EmployeeResponse;
import com.mobile.app.response.FboApprovalWorkflowListResponse;
import com.mobile.app.response.FboApprovalWorkflowResponse;
import com.mobile.app.response.FboResponse;
import com.mobile.app.response.WeighmentDataResponse;
import com.mobile.app.util.MElevenUtil;

import io.micrometer.common.util.StringUtils;

import com.mobile.app.response.OrderDetailsResponse;
import com.mobile.app.response.VehicleResponse;
import com.mobile.app.repository.DeliveryTrackingRepo;

@Service
public class WorkflowService
{

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowService.class);

	@Autowired
	private WorkflowRepo workflowRepo;

	@Autowired
	private FboRepo fboRepo;

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private WeighmentDataRepo weighmentDataRepo;

	@Autowired
	private EmailService emailService;

	@Value(value = "${register.email.template}")
	private String REG_EMAIL_TEMP;

	@Value(value = "${payment.email.template}")
	private String PAY_EMAIL_TEMP;

	@Value("${sms.registration.message}")
	private String smsRegistrationMessage;

	@Value("${sms.payment.message}")
	private String smsPaymentMessage;

	@Autowired
	private SmsService smsService;

	@Autowired
	private CashfreePaymentService cashfreePaymentService;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private DeliveryTrackingRepo deliveryTrackingRepo;

	@Autowired
	private VehicleRepo vehicleRepo;

	public void createWorkflowApproval(final FoodBusinessOperator createdFbo, final ApprovalType approvalType)
	{
		final FBOApprovalWorkflow fboRegisterFlow = new FBOApprovalWorkflow();
		fboRegisterFlow.setFbo(createdFbo.getEnrollmentId());
		fboRegisterFlow.setApprover(createdFbo.getAssignedUCO());
		fboRegisterFlow.setApprovalType(approvalType);
		fboRegisterFlow.setApprovalStatus(ApprovalStatus.PENDING);

		try
		{
			workflowRepo.save(fboRegisterFlow);
		}
		catch (final Exception e)
		{
			LOG.error("createWorkflowApproval() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
	}

	public FboApprovalWorkflowListResponse getRegisterWorkflowList(final Integer empId)
	{
		try
		{
			final Employee employee = employeeRepo.findById(empId).orElse(null);

			if (Objects.nonNull(employee) && employee.getRole().equalsIgnoreCase("UCO"))
			{
				final List<FBOApprovalWorkflow> workflowList = findListByApprover(empId);

				final List<FBOApprovalWorkflow> registerTypeList = workflowList.stream()
								.filter(w -> w.getApprovalType().equals(ApprovalType.REGISTER)).collect(Collectors.toList());

				if (!CollectionUtils.isEmpty(registerTypeList))
				{
					return getFboApprovalList(registerTypeList);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getRegisterWorkflowList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return null;
	}

	public FboApprovalWorkflowListResponse getPaymentWorkflowList(final Integer empId)
	{
		final List<FBOApprovalWorkflow> workflowList = findListByApprover(empId);

		final List<FBOApprovalWorkflow> paymentTypeList = workflowList.stream()
						.filter(w -> w.getApprovalType().equals(ApprovalType.PAYMENT)).collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(paymentTypeList))
		{
			return getFboApprovalList(paymentTypeList);
		}
		return null;
	}

	private List<FBOApprovalWorkflow> findListByApprover(final Integer empId)
	{
		return workflowRepo.findAllByApprover(empId);
	}

	private FboApprovalWorkflowListResponse getFboApprovalList(final List<FBOApprovalWorkflow> workflowList)
	{
		final FboApprovalWorkflowListResponse workflowResponseList = new FboApprovalWorkflowListResponse();
		final List<FboApprovalWorkflowResponse> responseList = new ArrayList<FboApprovalWorkflowResponse>();

		for (final FBOApprovalWorkflow workflow : workflowList)
		{
			final FboApprovalWorkflowResponse workflowResponse = mapper.map(workflow, FboApprovalWorkflowResponse.class);
			final FoodBusinessOperator fbo = fboRepo.findById(workflow.getFbo()).orElse(null);

			if (Objects.nonNull(fbo))
			{
				final FboResponse fboResponse = mapper.map(fbo, FboResponse.class);
				workflowResponse.setFbo(fboResponse);
			}
			// to set order details(order id, amount, weight.....)
			if (Objects.nonNull(workflow.getPurchaseOrder()))
			{
				final WeighmentData orderData = weighmentDataRepo.findById(workflow.getPurchaseOrder()).orElse(null);
				if (Objects.nonNull(orderData))
				{
					final WeighmentDataResponse orderDetails = mapper.map(orderData, WeighmentDataResponse.class);

					workflowResponse.setOrderDetails(orderDetails);

					final OrderDetailsResponse detailsResponse = new OrderDetailsResponse();

					detailsResponse.setCeData(mapper.map(employeeRepo.findById(orderDetails.getAssignedCE()).orElse(null),
									EmployeeResponse.class));
					detailsResponse.setDriver(mapper.map(employeeRepo.findById(orderDetails.getDriver()).orElse(null),
									EmployeeResponse.class));

					detailsResponse.setVehicleResponse(mapper.map(vehicleRepo.findById(orderDetails.getVehicle()).orElse(null),
									VehicleResponse.class));

					detailsResponse.setDeliveryTrackingData(
									mapper.map(deliveryTrackingRepo.findByPoId(orderDetails.getPurchaseOrderId()),
													DeliveryOrdersResponse.class));

					workflowResponse.setOrderData(detailsResponse);
				}
			}
			responseList.add(workflowResponse);
		}
		workflowResponseList.setWorkflowList(responseList);
		return workflowResponseList;
	}

	public String approvalRequest(final WorkflowApprovalRequest request)
	{
		if (request.getApprovalType().contentEquals(ApprovalType.REGISTER.toString()))
		{
			return registrationApprovalRequest(request);
		}
		else
		{
			return paymentApprovalRequest(request);
		}
	}

	private String registrationApprovalRequest(final WorkflowApprovalRequest approvalRequest)
	{
		try
		{
			final FBOApprovalWorkflow workflow = workflowRepo.findById(approvalRequest.getWorkFlowId()).orElse(null);

			if (Objects.nonNull(workflow))
			{
				workflow.setApprovalStatus(ApprovalStatus.valueOf(approvalRequest.getApprovalStatus()));
				workflow.setComments(approvalRequest.getComments());

				if (approvalRequest.getApprovalStatus().equalsIgnoreCase(ApprovalStatus.APPROVED.toString()))
				{
					final FoodBusinessOperator fbo = fboRepo.findById(workflow.getFbo()).orElse(null);
					if (Objects.nonNull(fbo))
					{
						fbo.setActive(true);
						final String passwordSetToken = UUID.randomUUID().toString();
						fbo.setPasswordSetToken(passwordSetToken);

//						final String emailResult = emailService.sendEmail(fbo, REG_EMAIL_TEMP, null, passwordSetToken);
//						final String smsResult = smsService.sendSmsMessages(fbo, smsRegistrationMessage);
//
//						if ("Email sent successfully".equals(emailResult) && "SMS sent successfully".equals(smsResult))
//						{
//							fboRepo.save(fbo);
//							workflowRepo.save(workflow);
//							return "Email and SMS sent successfully to " + fbo.getEmail() + " and " + fbo.getContactNo()
//											+ ". Status has been updated successfully.";
//						}
//						else
//						{
//							return "Email or SMS sending failed. Please check and try again.";
//						}
					}
				}
				workflowRepo.save(workflow);
			}
		}
		catch (final Exception e)
		{
			LOG.error("registrationApprovalRequest() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return "Email and SMS sent successfully to";
	}

	private String paymentApprovalRequest(final WorkflowApprovalRequest approvalRequest)
	{
		try
		{
			final FBOApprovalWorkflow workflow = workflowRepo.findById(approvalRequest.getWorkFlowId()).orElse(null);

			if (Objects.nonNull(workflow))
			{
				final Employee approver = employeeRepo.findById(workflow.getApprover()).orElse(null);
				workflow.setApprovalStatus(ApprovalStatus.valueOf(approvalRequest.getApprovalStatus()));
				workflow.setComments(approvalRequest.getComments());
				workflowRepo.save(workflow);

				if (Objects.nonNull(approver) && approver.getRole().equalsIgnoreCase("UCO")
								&& approvalRequest.getApprovalStatus().equalsIgnoreCase(ApprovalStatus.APPROVED.toString())
								&& (workflow.getPurchaseOrder() != null))
				{
					final WeighmentData order = weighmentDataRepo.findById(workflow.getPurchaseOrder()).orElse(null);
					if (Objects.nonNull(order))
					{
						final FoodBusinessOperator fbo = fboRepo.findById(workflow.getFbo()).orElse(null);

						// Payment flow
						final GetTransferResponse paymentResponse = doPayment(fbo, order);

						if (StringUtils.isNotBlank(paymentResponse.getStatus())
										&& "Success".equalsIgnoreCase(paymentResponse.getStatus())
										&& Objects.nonNull(paymentResponse.getData()))
						{
							final String referenceId = paymentResponse.getData().getTransfer().getReferenceId();
							order.setTransactionId(referenceId);
							order.setOrderStatus(OrderStatus.COMPLETED.toString());
							order.setPaymentStatus(ApprovalStatus.APPROVED.toString());
							weighmentDataRepo.save(order);

							final String emailResult = emailService.sendEmail(fbo, PAY_EMAIL_TEMP, order, null);
							final String smsResult = smsService.sendSmsMessages(fbo, smsPaymentMessage);

							if ("Email sent successfully".equals(emailResult) && "SMS sent successfully".equals(smsResult))
							{
								return "Approval has been accepted and Payment transfered successfully, the Reference Id: "
												+ referenceId;
							}
							else
							{
								return "Email or SMS sending failed. Please check and try again.";
							}
						}
						else
						{
							return paymentResponse.getMessage();
						}
					}
				}

				if (Objects.nonNull(approver) && approver.getRole().equalsIgnoreCase("RUCO")
								&& approvalRequest.getApprovalStatus().equalsIgnoreCase(ApprovalStatus.APPROVED.toString()))
				{
					final List<FBOApprovalWorkflow> workflowList = workflowRepo.findByPurchaseOrder(workflow.getPurchaseOrder());

					if (!CollectionUtils.isEmpty(workflowList))
					{
						final List<FBOApprovalWorkflow> filteredList = workflowList.stream().filter(
										w -> !Objects.equals(w.getApprover(), workflow.getApprover()) && !w.isFboApproval())
										.collect(Collectors.toList());

						if (!CollectionUtils.isEmpty(filteredList))
						{
							final FBOApprovalWorkflow ucoWorkflow = filteredList.get(0);
							ucoWorkflow.setApprovalStatus(ApprovalStatus.PENDING);
							workflowRepo.save(ucoWorkflow);

							return "Status has been updated successfully and Approval sent to UCO";
						}
					}
				}
				return "Status has been updated successfully";
			}
		}
		catch (final Exception e)
		{
			LOG.error("paymentApprovalRequest() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return MElevenUtil.SOMETHING_WENT_WRONG;
	}

	private GetTransferResponse doPayment(final FoodBusinessOperator fbo, final WeighmentData order)
	{
		final String accountNo = cryptoService.decryptData(fbo.getAccountNumber());
		final PaymentForm paymentform = new PaymentForm();
		paymentform.setAmount(MElevenUtil.getDoubleValue(order.getTotalPrice()));
		paymentform.setTransferMode(PaymentModes.banktransfer);
		if (Objects.nonNull(fbo))
		{
			final BeneficiaryData beneficiaryData = new BeneficiaryData();
			beneficiaryData.setName(fbo.getName());
			beneficiaryData.setPhone(fbo.getContactNo());
			beneficiaryData.setIfsc(fbo.getIfscCode());
			beneficiaryData.setBankAccount(accountNo);
			beneficiaryData.setUpi(fbo.getUpiId());
			beneficiaryData.setCode(fbo.getName().replace(" ", "_") + fbo.getEnrollmentId());
			beneficiaryData.setEmail(fbo.getEmail());
			beneficiaryData.setAddress(fbo.getAddress());
			paymentform.setBeneficiaryData(beneficiaryData);
		}

		return cashfreePaymentService.requestTransfer(paymentform);
	}

	public FboApprovalWorkflowListResponse getFboPaymentApprovals(final Integer enrollmentId)
	{
		final FboApprovalWorkflowListResponse response = new FboApprovalWorkflowListResponse();
		try
		{
			final List<FBOApprovalWorkflow> approvalList = workflowRepo.findByFbo(enrollmentId);
			final List<FBOApprovalWorkflow> fboApprovalList = approvalList.stream().filter(w -> w.isFboApproval())
							.collect(Collectors.toList());

			final List<FboApprovalWorkflowResponse> approvalResponseList = new ArrayList<>();

			for (final FBOApprovalWorkflow workflow : fboApprovalList)
			{
				final WeighmentData order = weighmentDataRepo.findById(workflow.getPurchaseOrder()).orElse(null);

				if (Objects.nonNull(order))
				{
					final WeighmentDataResponse orderDetails = mapper.map(order, WeighmentDataResponse.class);
					final FboApprovalWorkflowResponse workflowResponse = mapper.map(workflow, FboApprovalWorkflowResponse.class);
					workflowResponse.setOrderDetails(orderDetails);

					final FoodBusinessOperator fbo = fboRepo.findById(enrollmentId).orElse(null);

					if (Objects.nonNull(fbo))
					{
						workflowResponse.setFbo(mapper.map(fbo, FboResponse.class));
					}
					else
					{
						LOG.warn("FBO not found for enrollmentId: {}", enrollmentId);
					}

					approvalResponseList.add(workflowResponse);
				}
				else
				{
					LOG.warn("WeighmentData not found for purchase order: {}", workflow.getPurchaseOrder());
				}
			}
			response.setWorkflowList(approvalResponseList);
		}
		catch (final Exception e)
		{
			LOG.error("getFboPaymentApprovals() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}

		return response;
	}

	/*
	 * FBO Approval flow
	 */
	public String fboPaymentApprovalWorkflow(final WorkflowApprovalRequest approvalRequest)
	{
		try
		{
			final FBOApprovalWorkflow fboApprovalWorkflow = workflowRepo.findById(approvalRequest.getWorkFlowId()).orElse(null);
			if (Objects.nonNull(fboApprovalWorkflow))
			{
				fboApprovalWorkflow.setApprovalStatus(ApprovalStatus.valueOf(approvalRequest.getApprovalStatus()));
				workflowRepo.save(fboApprovalWorkflow);

				// Create workflow models for UCO & RUCO
				if (approvalRequest.getApprovalStatus().equalsIgnoreCase(ApprovalStatus.APPROVED.toString()))
				{
					final WeighmentData order = weighmentDataRepo.findById(fboApprovalWorkflow.getPurchaseOrder()).orElse(null);
					if (Objects.nonNull(order))
					{
						order.setPaymentStatus(ApprovalStatus.PENDING_AT_RUCO.toString());
						weighmentDataRepo.save(order);
					}

					final FoodBusinessOperator fbo = fboRepo.findById(fboApprovalWorkflow.getFbo()).orElse(null);
					if (Objects.nonNull(fbo))
					{
						createRucoPaymentApprovalWorkflow(fboApprovalWorkflow, fbo);
						createUcoPaymentApprovalWorkflow(fboApprovalWorkflow, fbo);
					}
					return "Approval request has been sent successfully";
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("fboPaymentApprovalWorkflow() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return "Workflow data not found";
	}

	// RUCO Payment Approval Workflow
	private void createRucoPaymentApprovalWorkflow(final FBOApprovalWorkflow fboApprovalWorkflow, final FoodBusinessOperator fbo)
	{
		final FBOApprovalWorkflow rucoWorkflow = new FBOApprovalWorkflow();
		rucoWorkflow.setFbo(fboApprovalWorkflow.getFbo());
		rucoWorkflow.setPurchaseOrder(fboApprovalWorkflow.getPurchaseOrder());
		rucoWorkflow.setApprover(fbo.getAssignedRUCO());
		rucoWorkflow.setApprovalType(ApprovalType.PAYMENT);
		rucoWorkflow.setApprovalStatus(ApprovalStatus.PENDING);
		workflowRepo.save(rucoWorkflow);
	}

	// UCO Payment Approval Workflow
	private void createUcoPaymentApprovalWorkflow(final FBOApprovalWorkflow fboApprovalWorkflow, final FoodBusinessOperator fbo)
	{
		final FBOApprovalWorkflow ucoWorkflow = new FBOApprovalWorkflow();
		ucoWorkflow.setFbo(fboApprovalWorkflow.getFbo());
		ucoWorkflow.setPurchaseOrder(fboApprovalWorkflow.getPurchaseOrder());
		ucoWorkflow.setApprover(fbo.getAssignedUCO());
		ucoWorkflow.setApprovalType(ApprovalType.PAYMENT);
		ucoWorkflow.setApprovalStatus(ApprovalStatus.PENDING_AT_RUCO);
		workflowRepo.save(ucoWorkflow);
	}

}
