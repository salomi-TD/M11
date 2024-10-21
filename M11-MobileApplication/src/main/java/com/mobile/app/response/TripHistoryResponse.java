package com.mobile.app.response;

import java.util.Date;

public class TripHistoryResponse
{

	private Date date;

	private String restaurantName;

	private Double weightCalculatedInKg;

	private String vehicleNumber;

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getRestaurantName()
	{
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName)
	{
		this.restaurantName = restaurantName;
	}

	public Double getWeightCalculatedInKg()
	{
		return weightCalculatedInKg;
	}

	public void setWeightCalculatedInKg(Double weightCalculatedInKg)
	{
		this.weightCalculatedInKg = weightCalculatedInKg;
	}

	public String getVehicleNumber()
	{
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber)
	{
		this.vehicleNumber = vehicleNumber;
	}

}
