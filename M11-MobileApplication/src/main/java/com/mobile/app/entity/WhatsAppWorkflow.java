package com.mobile.app.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rucowhatsappworkflow")
public class WhatsAppWorkflow
{

	@Id
	@Column(name = "workflow_id")
	private Integer workflowId;

	@Column(name = "fbo_contact")
	private String fboContact;

	@Column(name = "po_id")
	private Integer poId;

	@Column(name = "weight")
	private Double weight;

	@Column(name = "price")
	private Double price;

	@CreationTimestamp
	@Column(name = "creation_time")
	private Date creationTime;

	public Integer getWorkflowId()
	{
		return workflowId;
	}

	public void setWorkflowId(Integer workflowId)
	{
		this.workflowId = workflowId;
	}

	public String getFboContact()
	{
		return fboContact;
	}

	public void setFboContact(String fboContact)
	{
		this.fboContact = fboContact;
	}

	public Integer getPoId()
	{
		return poId;
	}

	public void setPoId(Integer poId)
	{
		this.poId = poId;
	}

	public Double getWeight()
	{
		return weight;
	}

	public void setWeight(Double weight)
	{
		this.weight = weight;
	}

	public Double getPrice()
	{
		return price;
	}

	public void setPrice(Double price)
	{
		this.price = price;
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
