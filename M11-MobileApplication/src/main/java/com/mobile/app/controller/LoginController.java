package com.mobile.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.app.request.LoginRequest;
import com.mobile.app.response.EmployeeResponse;
import com.mobile.app.response.FboResponse;
import com.mobile.app.response.LoginResponse;
import com.mobile.app.service.LoginService;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/login")
public class LoginController
{

	@Autowired
	private LoginService loginService;

	@PostMapping("/verifyEmp")
	public EmployeeResponse empLogin(@RequestBody final LoginRequest loginRequest)
	{

		return loginService.empVerifyService(loginRequest);
	}

	@PostMapping("/verifyFbo")
	public FboResponse fboLogin(@RequestBody final LoginRequest loginRequest)
	{

		return loginService.fboVerifyService(loginRequest);
	}

	@PostMapping("/verify")
	public LoginResponse verifyLogin(@RequestBody final LoginRequest loginRequest)
	{
		return loginService.verifyLoginDetails(loginRequest);
	}
}
