package com.mobile.app.request;

public class VehicleReadingData
{

	private String vehicleNumber;

	private String typeOfReading;

	private Long initialReading;

	private Long finalReading;

	private String message;

	public String getVehicleNumber()
	{
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber)
	{
		this.vehicleNumber = vehicleNumber;
	}

	public String getTypeOfReading()
	{
		return typeOfReading;
	}

	public void setTypeOfReading(String typeOfReading)
	{
		this.typeOfReading = typeOfReading;
	}

	public Long getInitialReading()
	{
		return initialReading;
	}

	public void setInitialReading(Long initialReading)
	{
		this.initialReading = initialReading;
	}

	public Long getFinalReading()
	{
		return finalReading;
	}

	public void setFinalReading(Long finalReading)
	{
		this.finalReading = finalReading;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

}
