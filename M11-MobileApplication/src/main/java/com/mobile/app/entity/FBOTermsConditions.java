package com.mobile.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RUCOFBOTermsconditions")
public class FBOTermsConditions
{

	@Id
	@Column(name = "fbo")
	private Integer fbo;

	@Column(name = "commercial_terms")
	private String commercialTerms;

	@Column(name = "payment_cost_per_kg")
	private Double paymentCostPerKg;

	@Column(name = "payment_currency")
	private String paymentCurrency;

	public Integer getFbo()
	{
		return fbo;
	}

	public void setFbo(Integer fbo)
	{
		this.fbo = fbo;
	}

	public String getCommercialTerms()
	{
		return commercialTerms;
	}

	public void setCommercialTerms(String commercialTerms)
	{
		this.commercialTerms = commercialTerms;
	}

	public Double getPaymentCostPerKg()
	{
		return paymentCostPerKg;
	}

	public void setPaymentCostPerKg(Double paymentCostPerKg)
	{
		this.paymentCostPerKg = paymentCostPerKg;
	}

	public String getPaymentCurrency()
	{
		return paymentCurrency;
	}

	public void setPaymentCurrency(String paymentCurrency)
	{
		this.paymentCurrency = paymentCurrency;
	}

}
