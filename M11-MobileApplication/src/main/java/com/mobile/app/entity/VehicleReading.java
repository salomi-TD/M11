package com.mobile.app.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehiclereadings")
public class VehicleReading
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reading_id")
	private Integer readingId;

	@Column(name = "vehicle_no")
	private String vehicleNumber;

	@Column(name = "initial_reading")
	private Long initialReading;

	@Column(name = "final_reading")
	private Long finalReading;

	@Column(name = "date")
	private Date date;

	@Column(name = "total_km")
	private Long totalKM;

	@Column(name = "gas_emission")
	private Double gasEmission;

	public Integer getReadingId()
	{
		return readingId;
	}

	public void setReadingId(Integer readingId)
	{
		this.readingId = readingId;
	}

	public String getVehicleNumber()
	{
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber)
	{
		this.vehicleNumber = vehicleNumber;
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

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
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
