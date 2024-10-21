package com.mobile.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RUCOVehicledetails")
public class VehicleDetails
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vehicle_id")
	private Integer vehicleId;

	@Column(name = "vehicle_no")
	private String vehicleNumber;

	@Column(name = "vehicle_type")
	private String vehicleType;

	@Column(name = "fuel_type")
	private String fuelType;

	@Column(name = "region")
	private String region;

	@Column(name = "available")
	private boolean available;

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

}
