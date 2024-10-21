package com.mobile.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RUCOGasemissionindicator")
public class GasEmissionIndicator
{

	@Id
	@Column(name = "gas_id")
	private String gasId;

	@Column(name = "vehicle")
	private Integer vehicle;

	@Column(name = "emission_ratio")
	private Double emissionRatio;

	public String getGasId()
	{
		return gasId;
	}

	public void setGasId(String gasId)
	{
		this.gasId = gasId;
	}

	public Integer getVehicle()
	{
		return vehicle;
	}

	public void setVehicle(Integer vehicle)
	{
		this.vehicle = vehicle;
	}

	public Double getEmissionRatio()
	{
		return emissionRatio;
	}

	public void setEmissionRatio(Double emissionRatio)
	{
		this.emissionRatio = emissionRatio;
	}

}
