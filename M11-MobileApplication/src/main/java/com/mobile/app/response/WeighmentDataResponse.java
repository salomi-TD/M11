package com.mobile.app.response;

import java.util.Date;

public class WeighmentDataResponse
{

	private Integer purchaseOrderId;

	private Integer createdBy;

	private Integer assignedCE;

	private Integer assignedFBO;

	private Date dateToVisit;

	private Integer driver;

	private Integer vehicle;

	private Integer cansProvided;

	private Double weightCalculatedInKG;

	private Double totalPrice;

	private String orderStatus;

	private String transactionId;

	private String paymentStatus;

	private String sourceAddress;

	private String destinationAddress;

	private String photo;

	private Date creationTime;

	public Integer getPurchaseOrderId()
	{
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Integer purchaseOrderId)
	{
		this.purchaseOrderId = purchaseOrderId;
	}

	public Integer getCreatedBy()
	{
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy)
	{
		this.createdBy = createdBy;
	}

	public Integer getAssignedCE()
	{
		return assignedCE;
	}

	public void setAssignedCE(Integer assignedCE)
	{
		this.assignedCE = assignedCE;
	}

	public Integer getAssignedFBO()
	{
		return assignedFBO;
	}

	public void setAssignedFBO(Integer assignedFBO)
	{
		this.assignedFBO = assignedFBO;
	}

	public Date getDateToVisit()
	{
		return dateToVisit;
	}

	public void setDateToVisit(Date dateToVisit)
	{
		this.dateToVisit = dateToVisit;
	}

	public Integer getDriver()
	{
		return driver;
	}

	public void setDriver(Integer driver)
	{
		this.driver = driver;
	}

	public Integer getVehicle()
	{
		return vehicle;
	}

	public void setVehicle(Integer vehicle)
	{
		this.vehicle = vehicle;
	}

	public Integer getCansProvided()
	{
		return cansProvided;
	}

	public void setCansProvided(Integer cansProvided)
	{
		this.cansProvided = cansProvided;
	}

	public Double getWeightCalculatedInKG()
	{
		return weightCalculatedInKG;
	}

	public void setWeightCalculatedInKG(Double weightCalculatedInKG)
	{
		this.weightCalculatedInKG = weightCalculatedInKG;
	}

	public Double getTotalPrice()
	{
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice)
	{
		this.totalPrice = totalPrice;
	}

	public String getOrderStatus()
	{
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus)
	{
		this.orderStatus = orderStatus;
	}

	public String getTransactionId()
	{
		return transactionId;
	}

	public void setTransactionId(String transactionId)
	{
		this.transactionId = transactionId;
	}

	public String getPaymentStatus()
	{
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus)
	{
		this.paymentStatus = paymentStatus;
	}

	public String getSourceAddress()
	{
		return sourceAddress;
	}

	public void setSourceAddress(String sourceAddress)
	{
		this.sourceAddress = sourceAddress;
	}

	public String getDestinationAddress()
	{
		return destinationAddress;
	}

	public void setDestinationAddress(String destinationAddress)
	{
		this.destinationAddress = destinationAddress;
	}

	public Date getCreationTime()
	{
		return creationTime;
	}

	public void setCreationTime(Date creationTime)
	{
		this.creationTime = creationTime;
	}

	public String getPhoto()
	{
		return photo;
	}

	public void setPhoto(String photo)
	{
		this.photo = photo;
	}

}
