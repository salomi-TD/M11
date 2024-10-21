package com.mobile.app.request;

public class WorkflowApprovalRequest
{

	private Integer workFlowId;

	private String approvalType;

	private String approvalStatus;

	private String comments;

	public Integer getWorkFlowId()
	{
		return workFlowId;
	}

	public void setWorkFlowId(Integer workFlowId)
	{
		this.workFlowId = workFlowId;
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

}
