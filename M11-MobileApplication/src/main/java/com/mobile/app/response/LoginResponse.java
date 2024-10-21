package com.mobile.app.response;

public class LoginResponse
{

	private EmployeeResponse employee;

	private FboResponse fbo;

	private String message;

	public EmployeeResponse getEmployee()
	{
		return employee;
	}

	public void setEmployee(EmployeeResponse employee)
	{
		this.employee = employee;
	}

	public FboResponse getFbo()
	{
		return fbo;
	}

	public void setFbo(FboResponse fbo)
	{
		this.fbo = fbo;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

}
