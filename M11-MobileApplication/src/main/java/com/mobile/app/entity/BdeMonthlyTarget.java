package com.mobile.app.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rucobdemonthlytarget")
public class BdeMonthlyTarget
{

	@Id
	@Column(name = "target_id")
	private String targetId;

	@Column(name = "bde")
	private Integer bde;

	@Column(name = "target_to_be_visited")
	private Integer targetToBeVisited;

	@Column(name = "actual_visited")
	private Integer actualVisited;

	@Column(name = "balance_to_visit")
	private Integer balanceToVisit;

	@CreationTimestamp
	@Column(name = "creation_time")
	private Date creationTime;

	public String getTargetId()
	{
		return targetId;
	}

	public void setTargetId(String targetId)
	{
		this.targetId = targetId;
	}

	public Integer getBde()
	{
		return bde;
	}

	public void setBde(Integer bde)
	{
		this.bde = bde;
	}

	public Integer getTargetToBeVisited()
	{
		return targetToBeVisited;
	}

	public void setTargetToBeVisited(Integer targetToBeVisited)
	{
		this.targetToBeVisited = targetToBeVisited;
	}

	public Integer getActualVisited()
	{
		return actualVisited;
	}

	public void setActualVisited(Integer actualVisited)
	{
		this.actualVisited = actualVisited;
	}

	public Integer getBalanceToVisit()
	{
		return balanceToVisit;
	}

	public void setBalanceToVisit(Integer balanceToVisit)
	{
		this.balanceToVisit = balanceToVisit;
	}

	public Date getCreationTime()
	{
		return creationTime;
	}

	public void setCreationTime(Date creationTime)
	{
		this.creationTime = creationTime;
	}

}
