package com.mobile.app.response;

import java.util.Date;

public class PaymentHistoryResponse
{

	private String fboName;

	private String transactionId;

	private Double totalPrice;

	private Date date;

	public String getFboName()
	{
		return fboName;
	}

	public void setFboName(String fboName)
	{
		this.fboName = fboName;
	}

	public String getTransactionId()
	{
		return transactionId;
	}

	public void setTransactionId(String transactionId)
	{
		this.transactionId = transactionId;
	}

	public Double getTotalPrice()
	{
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice)
	{
		this.totalPrice = totalPrice;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

}
