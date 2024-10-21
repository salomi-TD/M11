package com.mobile.app.response;

import java.util.List;

import com.mobile.app.entity.BdeEnquiryTracking;
import com.mobile.app.entity.BdeMonthlyTarget;

public class BdeReport
{

	private EmployeeResponse bdeData;

	private List<FboResponse> productiveList;

	private List<FboResponse> unproductiveList;

	private List<FboResponse> onHoldList;

	private List<FboResponse> pendingList;

	private List<BdeEnquiryTracking> unregisteredFboList;

	private List<BdeMonthlyTarget> bdeMonthlyTargetList;

	public EmployeeResponse getBdeData()
	{
		return bdeData;
	}

	public void setBdeData(EmployeeResponse bdeData)
	{
		this.bdeData = bdeData;
	}

	public List<FboResponse> getProductiveList()
	{
		return productiveList;
	}

	public void setProductiveList(List<FboResponse> productiveList)
	{
		this.productiveList = productiveList;
	}

	public List<FboResponse> getUnproductiveList()
	{
		return unproductiveList;
	}

	public void setUnproductiveList(List<FboResponse> unproductiveList)
	{
		this.unproductiveList = unproductiveList;
	}

	public List<FboResponse> getOnHoldList()
	{
		return onHoldList;
	}

	public void setOnHoldList(List<FboResponse> onHoldList)
	{
		this.onHoldList = onHoldList;
	}

	public List<FboResponse> getPendingList()
	{
		return pendingList;
	}

	public void setPendingList(List<FboResponse> pendingList)
	{
		this.pendingList = pendingList;
	}

	public List<BdeEnquiryTracking> getUnregisteredFboList()
	{
		return unregisteredFboList;
	}

	public void setUnregisteredFboList(List<BdeEnquiryTracking> unregisteredFboList)
	{
		this.unregisteredFboList = unregisteredFboList;
	}

	public List<BdeMonthlyTarget> getBdeMonthlyTargetList()
	{
		return bdeMonthlyTargetList;
	}

	public void setBdeMonthlyTargetList(List<BdeMonthlyTarget> bdeMonthlyTargetList)
	{
		this.bdeMonthlyTargetList = bdeMonthlyTargetList;
	}

}
