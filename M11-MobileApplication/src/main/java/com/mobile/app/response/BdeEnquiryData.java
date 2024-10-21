package com.mobile.app.response;

import com.mobile.app.entity.BdeEnquiryTracking;
import com.mobile.app.entity.BdeMonthlyTarget;

public class BdeEnquiryData
{

	private BdeEnquiryTracking bdeEnquiryTracking;

	private BdeMonthlyTarget bdeMonthlyTarget;

	public BdeEnquiryTracking getBdeEnquiryTracking()
	{
		return bdeEnquiryTracking;
	}

	public void setBdeEnquiryTracking(BdeEnquiryTracking bdeEnquiryTracking)
	{
		this.bdeEnquiryTracking = bdeEnquiryTracking;
	}

	public BdeMonthlyTarget getBdeMonthlyTarget()
	{
		return bdeMonthlyTarget;
	}

	public void setBdeMonthlyTarget(BdeMonthlyTarget bdeMonthlyTarget)
	{
		this.bdeMonthlyTarget = bdeMonthlyTarget;
	}

}
