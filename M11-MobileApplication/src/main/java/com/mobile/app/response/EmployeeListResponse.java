package com.mobile.app.response;

import java.util.List;

public class EmployeeListResponse
{

	private List<EmployeeResponse> employees;

	public List<EmployeeResponse> getEmployees()
	{
		return employees;
	}

	public void setEmployees(List<EmployeeResponse> employees)
	{
		this.employees = employees;
	}
}
