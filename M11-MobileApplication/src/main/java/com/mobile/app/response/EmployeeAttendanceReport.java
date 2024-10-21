package com.mobile.app.response;

public class EmployeeAttendanceReport
{

	private EmployeeResponse empData;

	private Integer totalNoOfDays;

	private Integer noOfDaysPresent;

	private Integer noOfDaysAbsent;

	private Boolean present;

	public EmployeeResponse getEmpData()
	{
		return empData;
	}

	public void setEmpData(EmployeeResponse empData)
	{
		this.empData = empData;
	}

	public Integer getTotalNoOfDays()
	{
		return totalNoOfDays;
	}

	public void setTotalNoOfDays(Integer totalNoOfDays)
	{
		this.totalNoOfDays = totalNoOfDays;
	}

	public Integer getNoOfDaysPresent()
	{
		return noOfDaysPresent;
	}

	public void setNoOfDaysPresent(Integer noOfDaysPresent)
	{
		this.noOfDaysPresent = noOfDaysPresent;
	}

	public Integer getNoOfDaysAbsent()
	{
		return noOfDaysAbsent;
	}

	public void setNoOfDaysAbsent(Integer noOfDaysAbsent)
	{
		this.noOfDaysAbsent = noOfDaysAbsent;
	}

	public Boolean getPresent()
	{
		return present;
	}

	public void setPresent(Boolean present)
	{
		this.present = present;
	}

}
