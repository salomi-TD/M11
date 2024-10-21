package com.mobile.app.response;

import java.util.Date;

public class FboApprovalWorkflowResponse
{

	private String workFlowId;

	private FboResponse fbo;

	private WeighmentDataResponse orderDetails;

	private OrderDetailsResponse orderData;

	private String purchaseOrder;

	private String approver;

	private String approvalType;

	private String approvalStatus;

	private String comments;

	private boolean fboApproval;

	private Date creationTime;

	public String getWorkFlowId()
	{
		return workFlowId;
	}

	public void setWorkFlowId(String workFlowId)
	{
		this.workFlowId = workFlowId;
	}

	public FboResponse getFbo()
	{
		return fbo;
	}

	public void setFbo(FboResponse fbo)
	{
		this.fbo = fbo;
	}

	public WeighmentDataResponse getOrderDetails()
	{
		return orderDetails;
	}

	public void setOrderDetails(WeighmentDataResponse orderDetails)
	{
		this.orderDetails = orderDetails;
	}

	public OrderDetailsResponse getOrderData()
	{
		return orderData;
	}

	public void setOrderData(OrderDetailsResponse orderData)
	{
		this.orderData = orderData;
	}

	public String getPurchaseOrder()
	{
		return purchaseOrder;
	}

	public void setPurchaseOrder(String purchaseOrder)
	{
		this.purchaseOrder = purchaseOrder;
	}

	public String getApprover()
	{
		return approver;
	}

	public void setApprover(String approver)
	{
		this.approver = approver;
	}

	public String getApprovalType()
	{
		return approvalType;
	}

	public void setApprovalType(String approvalType)
	{
		this.approvalType = approvalType;
	}

	public String getApprovalStatus()
	{
		return approvalStatus;
	}

	public void setApprovalStatus(String approvalStatus)
	{
		this.approvalStatus = approvalStatus;
	}

	public String getComments()
	{
		return comments;
	}

	public void setComments(String comments)
	{
		this.comments = comments;
	}

	public boolean isFboApproval()
	{
		return fboApproval;
	}

	public void setFboApproval(boolean fboApproval)
	{
		this.fboApproval = fboApproval;
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
