package com.mobile.app.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "rucoemployeeattendance")
public class EmployeeAttendance
{

	@Id
	@Column(name = "attendance_id")
	private Integer attendanceId;

	@Column(name = "emp_id")
	private Integer empId;

	@Column(name = "date")
	private Date date;

	@Column(name = "present")
	private Boolean present;

	@Transient
	private String message;

	public Integer getAttendanceId()
	{
		return attendanceId;
	}

	public void setAttendanceId(Integer attendanceId)
	{
		this.attendanceId = attendanceId;
	}

	public Integer getEmpId()
	{
		return empId;
	}

	public void setEmpId(Integer empId)
	{
		this.empId = empId;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public Boolean getPresent()
	{
		return present;
	}

	public void setPresent(Boolean present)
	{
		this.present = present;
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
