package com.mobile.app.response;

public class FboAccountResponse
{

	private String accountNumber;

	private String ifscCode;

	private String upiId;

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
}
