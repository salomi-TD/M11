package com.mobile.app.service;

import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobile.app.entity.Employee;
import com.mobile.app.entity.FoodBusinessOperator;
import com.mobile.app.repository.EmployeeRepo;
import com.mobile.app.repository.FboRepo;
import com.mobile.app.request.LoginRequest;
import com.mobile.app.response.EmployeeResponse;
import com.mobile.app.response.FboResponse;
import com.mobile.app.response.LoginResponse;

@Service
public class LoginService
{

	private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private FboRepo fboRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private CryptoService cryptoService;

	public EmployeeResponse empVerifyService(final LoginRequest loginRequest)
	{
		EmployeeResponse employeeResponse = new EmployeeResponse();
		try
		{
			final Employee employee = employeeRepo.findByContact(loginRequest.getUsername());
			if (Objects.isNull(employee))
			{
				employeeResponse.setMessage("Please Register");
				return employeeResponse;
			}
			final String empPass = cryptoService.decryptData(employee.getPassword());
			if (empPass.equals(loginRequest.getPassword()))
			{
				employeeResponse = mapper.map(employee, EmployeeResponse.class);
				employeeResponse.setRole(employee.getRole());
				employeeResponse.setMessage("Login Successful");
				return employeeResponse;
			}
			employeeResponse.setMessage("Password is Wrong");
		}
		catch (final Exception e)
		{
			LOG.error("empVerifyService() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return employeeResponse;
	}

	public FboResponse fboVerifyService(final LoginRequest loginRequest)
	{
		FboResponse fboResponse = new FboResponse();
		try
		{
			final FoodBusinessOperator fbo = fboRepo.findByContactNo(loginRequest.getUsername());
			if (Objects.isNull(fbo))
			{
				fboResponse.setMessage("Please Register");
				return fboResponse;
			}
			if (Objects.isNull(fbo.getPassword()))
			{
				fboResponse.setMessage("Your FBO is Not Approved yet.");
				return fboResponse;
			}

			final String pass = cryptoService.decryptData(fbo.getPassword());
			if (Objects.equals(pass, loginRequest.getPassword()))
			{
				fboResponse = mapper.map(fbo, FboResponse.class);
				fboResponse.setMessage("Login Successful");
				return fboResponse;
			}
			fboResponse.setMessage("Password is Wrong");
		}
		catch (final Exception e)
		{
			LOG.error("fboVerifyService() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return fboResponse;
	}

	public LoginResponse verifyLoginDetails(final LoginRequest loginRequest)
	{
		final LoginResponse loginResponse = new LoginResponse();
		try
		{
			final Employee employee = employeeRepo.findByContact(loginRequest.getUsername());
			FoodBusinessOperator fbo = null;

			if (Objects.nonNull(employee))
			{
				verifyEmployeeLogin(employee, loginRequest, loginResponse);
			}
			else
			{
				fbo = fboRepo.findByContactNo(loginRequest.getUsername());
				verifyFboLogin(fbo, loginRequest, loginResponse);
			}

			if (Objects.isNull(employee) && Objects.isNull(fbo))
			{
				loginResponse.setMessage("User is not registered");
			}
		}
		catch (final Exception e)
		{
			LOG.error("verifyLoginDetails() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return loginResponse;
	}

	private void verifyEmployeeLogin(final Employee employee, final LoginRequest loginRequest, final LoginResponse loginResponse)
	{
		final String empPass = cryptoService.decryptData(employee.getPassword());

		if (empPass.equals(loginRequest.getPassword()))
		{
			final EmployeeResponse empResponse = mapper.map(employee, EmployeeResponse.class);
			loginResponse.setMessage("Login Successful");
			loginResponse.setEmployee(empResponse);
		}
		else
		{
			loginResponse.setMessage("Invalid credentials of Employee, check your password");
		}
	}

	private void verifyFboLogin(final FoodBusinessOperator fbo, final LoginRequest loginRequest,
					final LoginResponse loginResponse)
	{
		if (Objects.nonNull(fbo))
		{
			if (!fbo.isActive())
			{
				loginResponse.setMessage("Your registration is not approved yet.");
			}
			else
			{
				final String fboPass = cryptoService.decryptData(fbo.getPassword());
				if (Objects.equals(fboPass, loginRequest.getPassword()))
				{
					loginResponse.setFbo(mapper.map(fbo, FboResponse.class));
					loginResponse.setMessage("Login Successful");
				}
				else
				{
					loginResponse.setMessage("Invalid credentials of FBO, check your password");
				}
			}
		}
	}

}
