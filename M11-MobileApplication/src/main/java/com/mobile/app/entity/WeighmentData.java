package com.mobile.app.entity;

import java.util.Date;

import org.hibernate.annotations.CurrentTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RUCOWeighmentdata")
public class WeighmentData
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "po_id")
	private Integer purchaseOrderId;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "assigned_ce")
	private Integer assignedCE;

	@Column(name = "assigned_fbo")
	private Integer assignedFBO;

	@Column(name = "date_to_visit")
	private Date dateToVisit;

	@Column(name = "driver")
	private Integer driver;

	@Column(name = "vehicle")
	private Integer vehicle;

	@Column(name = "cans_provided")
	private Integer cansProvided;

	@Column(name = "weight_calculated_in_kg")
	private Double weightCalculatedInKG;

	@Column(name = "total_price")
	private Double totalPrice;

	@Column(name = "order_status")
	private String orderStatus;

	@Column(name = "source_address")
	private String sourceAddress;

	@Column(name = "destination_address")
	private String destinationAddress;

	@Column(name = "transaction_id")
	private String transactionId;

	@Column(name = "payment_status")
	private String paymentStatus;

	@Column(name = "photo")
	private String photo;

	@CurrentTimestamp
	@Column(name = "creation_time")
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