package com.mobile.app.response;

public class VehicleResponse
{

	private Integer vehicleId;

	private String vehicleNumber;

	private String vehicleType;

	private String fuelType;

	private String region;

	private boolean available;

	private Long totalKM;

	private Double gasEmission;

	public Integer getVehicleId()
	{
		return vehicleId;
	}

	public void setVehicleId(Integer vehicleId)
	{
		this.vehicleId = vehicleId;
	}

	public String getVehicleNumber()
	{
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber)
	{
		this.vehicleNumber = vehicleNumber;
	}

	public String getVehicleType()
	{
		return vehicleType;
	}

	public void setVehicleType(String vehicleType)
	{
		this.vehicleType = vehicleType;
	}

	public String getFuelType()
	{
		return fuelType;
	}

	public void setFuelType(String fuelType)
	{
		this.fuelType = fuelType;
	}

	public String getRegion()
	{
		return region;
	}

	public void setRegion(String region)
	{
		this.region = region;
	}

	public boolean isAvailable()
	{
		return available;
	}

	public void setAvailable(boolean available)
	{
		this.available = available;
	}

	public Long getTotalKM()
	{
		return totalKM;
	}

	public void setTotalKM(Long totalKM)
	{
		this.totalKM = totalKM;
	}

	public Double getGasEmission()
	{
		return gasEmission;
	}

	public void setGasEmission(Double gasEmission)
	{
		this.gasEmission = gasEmission;
	}

}
