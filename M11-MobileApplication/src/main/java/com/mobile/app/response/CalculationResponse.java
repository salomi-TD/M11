package com.mobile.app.response;

public class CalculationResponse
{

	private String duration;

	private Double totalOil;

	private Integer totalOrders;

	private Double totalPrice;

	public String getDuration()
	{
		return duration;
	}

	public void setDuration(String duration)
	{
		this.duration = duration;
	}

	public Double getTotalOil()
	{
		return totalOil;
	}

	public void setTotalOil(Double totalOil)
	{
		this.totalOil = totalOil;
	}

	public Integer getTotalOrders()
	{
		return totalOrders;
	}

	public void setTotalOrders(Integer totalOrders)
	{
		this.totalOrders = totalOrders;
	}

	public Double getTotalPrice()
	{
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice)
	{
		this.totalPrice = totalPrice;
	}

}
