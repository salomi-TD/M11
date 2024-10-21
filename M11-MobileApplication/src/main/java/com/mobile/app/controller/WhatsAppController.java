package com.mobile.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.app.service.WhatsAppService;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/whatsapp")
public class WhatsAppController
{

	private static final Logger LOG = LoggerFactory.getLogger(WhatsAppController.class);

	@Value("${whatsapp.verify.token}")
	private String verifyToken;

	@Autowired
	private WhatsAppService whatsAppService;

	@GetMapping("/webhook")
	public ResponseEntity<String> validateWebhook(@RequestParam("hub.mode") final String mode,
					@RequestParam("hub.challenge") final String challenge, @RequestParam("hub.verify_token") final String token)
	{
		if (mode.equals("subscribe") && token.equals(verifyToken))
		{
			return new ResponseEntity<>(challenge, HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<>("Verification token or mode mismatch", HttpStatus.FORBIDDEN);
		}
	}

	@PostMapping("/webhook")
	public void handleIncomingMessages(@RequestBody final String payload)
	{
		try
		{
			whatsAppService.handleIncomingMessages(payload);
		}
		catch (final Exception e)
		{
			LOG.error("handleIncomingMessage() - Exception occurred.  Message:[{}]", e.getMessage(), e);
		}
	}

}
