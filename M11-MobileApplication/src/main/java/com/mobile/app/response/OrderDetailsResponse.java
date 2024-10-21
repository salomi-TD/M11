package com.mobile.app.response;

public class OrderDetailsResponse
{

	private VehicleResponse vehicleResponse;

	private EmployeeResponse ucoData;

	private EmployeeResponse rucoData;

	private EmployeeResponse ceData;

	private EmployeeResponse driver;

	private FboResponse assignedFBO;

	private String sourceAddress;

	private String destinationAddress;

	private DeliveryOrdersResponse deliveryTrackingData;

	public VehicleResponse getVehicleResponse()
	{
		return vehicleResponse;
	}

	public void setVehicleResponse(VehicleResponse vehicleResponse)
	{
		this.vehicleResponse = vehicleResponse;
	}

	public EmployeeResponse getUcoData()
	{
		return ucoData;
	}

	public void setUcoData(EmployeeResponse ucoData)
	{
		this.ucoData = ucoData;
	}

	public EmployeeResponse getRucoData()
	{
		return rucoData;
	}

	public void setRucoData(EmployeeResponse rucoData)
	{
		this.rucoData = rucoData;
	}

	public EmployeeResponse getCeData()
	{
		return ceData;
	}

	public void setCeData(EmployeeResponse ceData)
	{
		this.ceData = ceData;
	}

	public EmployeeResponse getDriver()
	{
		return driver;
	}

	public void setDriver(EmployeeResponse driver)
	{
		this.driver = driver;
	}

	public FboResponse getAssignedFBO()
	{
		return assignedFBO;
	}

	public void setAssignedFBO(FboResponse assignedFBO)
	{
		this.assignedFBO = assignedFBO;
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

	public DeliveryOrdersResponse getDeliveryTrackingData()
	{
		return deliveryTrackingData;
	}

	public void setDeliveryTrackingData(DeliveryOrdersResponse deliveryTrackingData)
	{
		this.deliveryTrackingData = deliveryTrackingData;
	}

}
