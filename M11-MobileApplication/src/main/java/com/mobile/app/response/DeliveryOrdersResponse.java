package com.mobile.app.response;

public class DeliveryOrdersResponse
{

	private Integer enrollmentId;

	private String name;

	private String restaurantName;

	private String contactNo;

	private String sourceAddress;

	private String destinationAddress;

	private Integer trackId;

	private Integer poId;

	private String startTime;

	private String endTime;

	private Double distanceTravelledInKm;

	private Double gasEmissionOccurred;

	private String orderStatus;

	private boolean ceAccepted;

	private boolean weightCalculated;

	private boolean ceAcceptanceWorkflow;

	public Integer getEnrollmentId()
	{
		return enrollmentId;
	}

	public void setEnrollmentId(Integer enrollmentId)
	{
		this.enrollmentId = enrollmentId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getRestaurantName()
	{
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName)
	{
		this.restaurantName = restaurantName;
	}

	public String getContactNo()
	{
		return contactNo;
	}

	public void setContactNo(String contactNo)
	{
		this.contactNo = contactNo;
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

	public Integer getTrackId()
	{
		return trackId;
	}

	public void setTrackId(Integer trackId)
	{
		this.trackId = trackId;
	}

	public Integer getPoId()
	{
		return poId;
	}

	public void setPoId(Integer poId)
	{
		this.poId = poId;
	}

	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	public String getEndTime()
	{
		return endTime;
	}

	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}

	public Double getDistanceTravelledInKm()
	{
		return distanceTravelledInKm;
	}

	public void setDistanceTravelledInKm(Double distanceTravelledInKm)
	{
		this.distanceTravelledInKm = distanceTravelledInKm;
	}

	public Double getGasEmissionOccurred()
	{
		return gasEmissionOccurred;
	}

	public void setGasEmissionOccurred(Double gasEmissionOccurred)
	{
		this.gasEmissionOccurred = gasEmissionOccurred;
	}

	public String getOrderStatus()
	{
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus)
	{
		this.orderStatus = orderStatus;
	}

	public boolean isCeAccepted()
	{
		return ceAccepted;
	}

	public void setCeAccepted(boolean ceAccepted)
	{
		this.ceAccepted = ceAccepted;
	}

	public boolean isWeightCalculated()
	{
		return weightCalculated;
	}

	public void setWeightCalculated(boolean weightCalculated)
	{
		this.weightCalculated = weightCalculated;
	}

	public boolean isCeAcceptanceWorkflow()
	{
		return ceAcceptanceWorkflow;
	}

	public void setCeAcceptanceWorkflow(boolean ceAcceptanceWorkflow)
	{
		this.ceAcceptanceWorkflow = ceAcceptanceWorkflow;
	}

}
