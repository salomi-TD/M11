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
@Table(name = "rucofbofollowup")
public class FboFollowup
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "followup_id")
	private Integer followupId;

	@Column(name = "fbo_id")
	private Integer fboId;

	@Column(name = "ruco_id")
	private Integer rucoId;

	@Column(name = "actual_followup_date")
	private Date actualFollowupDate;

	@Column(name = "followedup_on_actual_date")
	private boolean followedupOnActualDate;

	@Column(name = "postpone_date")
	private Date postponeDate;

	@Column(name = "followedup_on_postpone_date")
	private boolean followedupOnPostponeDate;

	@Column(name = "ready_for_pickup_date")
	private Date readyForPickupDate;

	@Column(name = "followedup_on_ready_for_pickup_date")
	private boolean followedupOnReadyForPickupDate;

	@CreationTimestamp
	@Column(name = "creation_time")
	private Date creationTime;

	public Integer getFollowupId()
	{
		return followupId;
	}

	public void setFollowupId(Integer followupId)
	{
		this.followupId = followupId;
	}

	public Integer getFboId()
	{
		return fboId;
	}

	public void setFboId(Integer fboId)
	{
		this.fboId = fboId;
	}

	public Integer getRucoId()
	{
		return rucoId;
	}

	public void setRucoId(Integer rucoId)
	{
		this.rucoId = rucoId;
	}

	public Date getActualFollowupDate()
	{
		return actualFollowupDate;
	}

	public void setActualFollowupDate(Date actualFollowupDate)
	{
		this.actualFollowupDate = actualFollowupDate;
	}

	public boolean isFollowedupOnActualDate()
	{
		return followedupOnActualDate;
	}

	public void setFollowedupOnActualDate(boolean followedupOnActualDate)
	{
		this.followedupOnActualDate = followedupOnActualDate;
	}

	public Date getPostponeDate()
	{
		return postponeDate;
	}

	public void setPostponeDate(Date postponeDate)
	{
		this.postponeDate = postponeDate;
	}

	public boolean isFollowedupOnPostponeDate()
	{
		return followedupOnPostponeDate;
	}

	public void setFollowedupOnPostponeDate(boolean followedupOnPostponeDate)
	{
		this.followedupOnPostponeDate = followedupOnPostponeDate;
	}

	public Date getReadyForPickupDate()
	{
		return readyForPickupDate;
	}

	public void setReadyForPickupDate(Date readyForPickupDate)
	{
		this.readyForPickupDate = readyForPickupDate;
	}

	public boolean isFollowedupOnReadyForPickupDate()
	{
		return followedupOnReadyForPickupDate;
	}

	public void setFollowedupOnReadyForPickupDate(boolean followedupOnReadyForPickupDate)
	{
		this.followedupOnReadyForPickupDate = followedupOnReadyForPickupDate;
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
