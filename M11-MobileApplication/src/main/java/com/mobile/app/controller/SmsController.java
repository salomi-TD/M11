package com.mobile.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import com.mobile.app.entity.FoodBusinessOperator;
import com.mobile.app.service.SmsService;
@CrossOrigin(origins = "http://localhost:8081")
@Controller
public class SmsController
{

	@Autowired
	private SmsService smsService;

	public SmsController(final SmsService smsService)
	{
		this.smsService = smsService;
	}

	@GetMapping("/sendSMS")
	public ResponseEntity<String> sendSMS()
	{
		final FoodBusinessOperator fbo = new FoodBusinessOperator();
		fbo.setContactNo("+91733707556");
		smsService.sendSmsMessages(fbo, "Hello,You are successfully Registered");

		return new ResponseEntity<>("Messages sent successfully", HttpStatus.OK);
	}
}
