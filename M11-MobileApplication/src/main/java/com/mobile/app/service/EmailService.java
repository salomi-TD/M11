package com.mobile.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.mobile.app.entity.FoodBusinessOperator;
import com.mobile.app.entity.WeighmentData;
import com.mobile.app.util.MElevenUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService
{

	private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private TemplateEngine templateEngine;

	@Value(value = "${company.from.mail}")
	private String MAIL_FROM;

	@Value(value = "${company.contact.mail}")
	private String CONTACT_MAIL;

	@Value(value = "${company.name}")
	private String COMP_NAME;

	@Value(value = "${register.email.template}")
	private String REG_EMAIL_TEMP;

	@Value(value = "${payment.email.subject}")
	private String PAY_EMAIL_SUB;

	@Value(value = "${register.email.subject}")
	private String REG_EMAIL_SUB;

	public String sendEmail(final FoodBusinessOperator fbo, final String template, final WeighmentData order,
					final String passwordSetToken) throws MessagingException
	{
		try
		{
			final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			final Context thymeleafContext = new Context();
			final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");

			helper.setFrom(MAIL_FROM);
			helper.setTo(fbo.getEmail());

			if (template.equals(REG_EMAIL_TEMP))
			{
				helper.setSubject(REG_EMAIL_SUB);
				thymeleafContext.setVariable("passwordSetToken", passwordSetToken);
			}
			else
			{
				helper.setSubject(PAY_EMAIL_SUB);
				thymeleafContext.setVariable("referenceId", order.getTransactionId());
				thymeleafContext.setVariable("purchaseOrderId", order.getPurchaseOrderId());
				thymeleafContext.setVariable("weight", MElevenUtil.getDoubleValue(order.getWeightCalculatedInKG()));
				thymeleafContext.setVariable("amount", MElevenUtil.getDoubleValue(order.getTotalPrice()));
				thymeleafContext.setVariable("orderStatus", order.getOrderStatus());
			}
			thymeleafContext.setVariable("recipientName", fbo.getName());
			thymeleafContext.setVariable("companyName", COMP_NAME);
			thymeleafContext.setVariable("contactEmail", CONTACT_MAIL);
			thymeleafContext.setVariable("enrollmentId", fbo.getEnrollmentId());

			final String emailContent = templateEngine.process(template, thymeleafContext);
			helper.setText(emailContent, true);

			javaMailSender.send(mimeMessage);

			return "Email sent successfully";
		}
		catch (final MessagingException e)
		{
			LOG.error("Error sending email to " + fbo.getEmail(), e);
			return "Error sending email. Please try again later.";
		}

	}
}
