package com.mobile.app.request;

public class FboAccountRequest
{

	private int fboId;

	private String accountNumber;

	private String ifscCode;

	private String upiId;

	public int getFboId()
	{
		return fboId;
	}

	public void setFboId(int fboId)
	{
		this.fboId = fboId;
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
}
