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
@Table(name = "RUCOFBO")
public class FoodBusinessOperator
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "enrollment_id")
	private Integer enrollmentId;

	@Column(name = "name")
	private String name;

	@Column(name = "restaurant_name")
	private String restaurantName;

	@Column(name = "contact_no")
	private String contactNo;

	@Column(name = "email")
	private String email;

	@Column(name = "address")
	private String address;

	@Column(name = "gstno")
	private String gstNo;

	@Column(name = "fssaino")
	private String fssaiNo;

	@Column(name = "google_location_tracker")
	private String googleLocationTracker;

	@Column(name = "rate_agreed_per_KG")
	private Double rateAgreedPerKG;

	@Column(name = "region")
	private String region;

	@Column(name = "active")
	private boolean active;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "assigned_ruco")
	private Integer assignedRUCO;

	@Column(name = "assigned_uco")
	private Integer assignedUCO;

	@Column(name = "approval_date")
	private Date approvalDate;

	@Column(name = "frequency")
	private Integer frequency;

	@Column(name = "photos")
	private String photos;

	@Column(name = "password")
	private String password;

	@Column(name = "account_number")
	private String accountNumber;

	@Column(name = "ifsc_code")
	private String ifscCode;

	@Column(name = "upi_id")
	private String upiId;

	@Column(name = "password_set_token")
	private String passwordSetToken;

	@CreationTimestamp
	@Column(name = "creation_time")
	private Date creationTime;

	public Integer getEnrollmentId()
	{
		return enrollmentId;
	}

	public void setEnrollmentId(Integer enrollmentId)
	{
		this.enrollmentId = enrollmentId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getRestaurantName()
	{
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName)
	{
		this.restaurantName = restaurantName;
	}

	public String getContactNo()
	{
		return contactNo;
	}

	public void setContactNo(String contactNo)
	{
		this.contactNo = contactNo;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getGstNo()
	{
		return gstNo;
	}

	public void setGstNo(String gstNo)
	{
		this.gstNo = gstNo;
	}

	public String getFssaiNo()
	{
		return fssaiNo;
	}

	public void setFssaiNo(String fssaiNo)
	{
		this.fssaiNo = fssaiNo;
	}

	public String getGoogleLocationTracker()
	{
		return googleLocationTracker;
	}

	public void setGoogleLocationTracker(String googleLocationTracker)
	{
		this.googleLocationTracker = googleLocationTracker;
	}

	public Double getRateAgreedPerKG()
	{
		return rateAgreedPerKG;
	}

	public void setRateAgreedPerKG(Double rateAgreedPerKG)
	{
		this.rateAgreedPerKG = rateAgreedPerKG;
	}

	public String getRegion()
	{
		return region;
	}

	public void setRegion(String region)
	{
		this.region = region;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public Integer getCreatedBy()
	{
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy)
	{
		this.createdBy = createdBy;
	}

	public Integer getAssignedRUCO()
	{
		return assignedRUCO;
	}

	public void setAssignedRUCO(Integer assignedRUCO)
	{
		this.assignedRUCO = assignedRUCO;
	}

	public Integer getAssignedUCO()
	{
		return assignedUCO;
	}

	public void setAssignedUCO(Integer assignedUCO)
	{
		this.assignedUCO = assignedUCO;
	}

	public Date getApprovalDate()
	{
		return approvalDate;
	}

	public void setApprovalDate(Date approvalDate)
	{
		this.approvalDate = approvalDate;
	}

	public Integer getFrequency()
	{
		return frequency;
	}

	public void setFrequency(Integer frequency)
	{
		this.frequency = frequency;
	}

	public String getPhotos()
	{
		return photos;
	}

	public void setPhotos(String photos)
	{
		this.photos = photos;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public String getIfscCode()
	{
		return ifscCode;
	}

	public void setIfscCode(String ifscCode)
	{
		this.ifscCode = ifscCode;
	}

	public String getUpiId()
	{
		return upiId;
	}

	public void setUpiId(String upiId)
	{
		this.upiId = upiId;
	}

	public String getPasswordSetToken()
	{
		return passwordSetToken;
	}

	public void setPasswordSetToken(String passwordSetToken)
	{
		this.passwordSetToken = passwordSetToken;
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
