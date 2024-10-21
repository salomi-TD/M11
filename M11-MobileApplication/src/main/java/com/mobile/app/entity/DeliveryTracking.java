package com.mobile.app.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rucodeliverytracking")
public class DeliveryTracking
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "track_id")
	private Integer trackId;

	@Column(name = "po_id")
	private Integer poId;

	@Column(name = "start_time")
	private Date startTime;

	@Column(name = "end_time")
	private Date endTime;

	@Column(name = "distance_travelled_in_km")
	private Double distanceTravelledInKm;

	@Column(name = "gas_emission_occurred")
	private Double gasEmissionOccurred;

	@Column(name = "ce_accepted")
	private boolean ceAccepted;

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

	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}

	public Date getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Date endTime)
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

	public boolean isCeAccepted()
	{
		return ceAccepted;
	}

	public void setCeAccepted(boolean ceAccepted)
	{
		this.ceAccepted = ceAccepted;
	}

}
