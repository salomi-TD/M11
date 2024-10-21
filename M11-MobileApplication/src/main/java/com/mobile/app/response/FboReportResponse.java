package com.mobile.app.response;

import java.util.List;

public class FboReportResponse
{

	private FboResponse fboResponse;

	private List<WeighmentDataResponse> weighmentDataList;

	private Double totalWeightOfOil;

	private Double pendingAmount;

	private Double paidAmount;

	public FboResponse getFboResponse()
	{
		return fboResponse;
	}

	public void setFboResponse(FboResponse fboResponse)
	{
		this.fboResponse = fboResponse;
	}

	public List<WeighmentDataResponse> getWeighmentDataList()
	{
		return weighmentDataList;
	}

	public void setWeighmentDataList(List<WeighmentDataResponse> weighmentDataList)
	{
		this.weighmentDataList = weighmentDataList;
	}

	public Double getTotalWeightOfOil()
	{
		return totalWeightOfOil;
	}

	public void setTotalWeightOfOil(Double totalWeightOfOil)
	{
		this.totalWeightOfOil = totalWeightOfOil;
	}

	public Double getPendingAmount()
	{
		return pendingAmount;
	}

	public void setPendingAmount(Double pendingAmount)
	{
		this.pendingAmount = pendingAmount;
	}

	public Double getPaidAmount()
	{
		return paidAmount;
	}

	public void setPaidAmount(Double paidAmount)
	{
		this.paidAmount = paidAmount;
	}

}
