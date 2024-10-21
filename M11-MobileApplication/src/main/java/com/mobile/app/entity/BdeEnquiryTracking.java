package com.mobile.app.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RUCOBDEEnquirytracking")
public class BdeEnquiryTracking
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "track_id")
	private Integer trackId;

	@Column(name = "fbo_name")
	private String fboName;

	@Column(name = "bde")
	private Integer bde;

	@Column(name = "last_visit_date")
	private Date lastVisitDate;

	@Column(name = "next_visit_date")
	private Date nextVisitDate;

	@Column(name = "discussion_points")
	private String discussionPoints;

	@CreationTimestamp
	@Column(name = "creation_time")
	private Date creationTime;

	public Integer getTrackId()
	{
		return trackId;
	}

	public void setTrackId(Integer trackId)
	{
		this.trackId = trackId;
	}

	public String getFboName()
	{
		return fboName;
	}

	public void setFboName(String fboName)
	{
		this.fboName = fboName;
	}

	public Integer getBde()
	{
		return bde;
	}

	public void setBde(Integer bde)
	{
		this.bde = bde;
	}

	public Date getLastVisitDate()
	{
		return lastVisitDate;
	}

	public void setLastVisitDate(Date lastVisitDate)
	{
		this.lastVisitDate = lastVisitDate;
	}

	public Date getNextVisitDate()
	{
		return nextVisitDate;
	}

	public void setNextVisitDate(Date nextVisitDate)
	{
		this.nextVisitDate = nextVisitDate;
	}

	public String getDiscussionPoints()
	{
		return discussionPoints;
	}

	public void setDiscussionPoints(String discussionPoints)
	{
		this.discussionPoints = discussionPoints;
	}

	public Date getCreationTime()
	{
		return creationTime;
	}

	public void setCreationTime(Date creationTime)
	{
		this.creationTime = creationTime;
	}

}
