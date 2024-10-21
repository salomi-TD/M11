package com.mobile.app.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.mobile.app.enums.ApprovalStatus;
import com.mobile.app.enums.ApprovalType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RUCOFBOApprovalworkflow")
public class FBOApprovalWorkflow
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "workflow_id")
	private Integer workFlowId;

	@Column(name = "fbo")
	private Integer fbo;

	@Column(name = "purchase_order")
	private Integer purchaseOrder;

	@Column(name = "approver")
	private Integer approver;

	@Enumerated(EnumType.STRING)
	@Column(name = "approval_type")
	private ApprovalType approvalType;

	@Enumerated(EnumType.STRING)
	@Column(name = "approval_status")
	private ApprovalStatus approvalStatus;

	@Column(name = "comments")
	private String comments;

	@Column(name = "fbo_approval")
	private boolean fboApproval;

	@CreationTimestamp
	@Column(name = "creation_time")
	private Date creationTime;

	public Integer getWorkFlowId()
	{
		return workFlowId;
	}

	public void setWorkFlowId(Integer workFlowId)
	{
		this.workFlowId = workFlowId;
	}

	public Integer getFbo()
	{
		return fbo;
	}

	public void setFbo(Integer fbo)
	{
		this.fbo = fbo;
	}

	public Integer getPurchaseOrder()
	{
		return purchaseOrder;
	}

	public void setPurchaseOrder(Integer purchaseOrder)
	{
		this.purchaseOrder = purchaseOrder;
	}

	public Integer getApprover()
	{
		return approver;
	}

	public void setApprover(Integer approver)
	{
		this.approver = approver;
	}

	public ApprovalType getApprovalType()
	{
		return approvalType;
	}

	public void setApprovalType(ApprovalType approvalType)
	{
		this.approvalType = approvalType;
	}

	public ApprovalStatus getApprovalStatus()
	{
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus)
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
