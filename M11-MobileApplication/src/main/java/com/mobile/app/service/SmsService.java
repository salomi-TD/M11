package com.mobile.app.service;

import com.mobile.app.entity.FoodBusinessOperator;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService
{

	private static final Logger LOG = LoggerFactory.getLogger(SmsService.class);

	@Value("${twilio.account.sid}")
	private String accountSid;

	@Value("${twilio.auth.token}")
	private String authToken;

	@Value("${twilio.phone.number}")
	private String m11Contact;

	public String sendSmsMessages(final FoodBusinessOperator fbo, final String messageBody)
	{
		try
		{
			Twilio.init(accountSid, authToken);

			final Message message = Message
							.creator(new PhoneNumber("+91" + fbo.getContactNo()), new PhoneNumber(m11Contact), messageBody)
							.create();

			LOG.info("SMS Response :: " + message);

		}
		catch (final Exception e)
		{
			LOG.error("sendSmsMessages() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return "SMS sent successfully";
	}
}