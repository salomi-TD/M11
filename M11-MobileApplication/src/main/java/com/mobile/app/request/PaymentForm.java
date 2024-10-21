package com.mobile.app.request;

import com.mobile.app.enums.PaymentModes;
import com.mobile.app.response.BeneficiaryData;

public class PaymentForm
{

	private BeneficiaryData beneficiaryData;

	private Double amount;

	private PaymentModes transferMode;

	private String remarks;

	private String paymentInstrumentId;

	public String getPaymentInstrumentId()
	{
		return paymentInstrumentId;
	}

	public void setPaymentInstrumentId(String paymentInstrumentId)
	{
		this.paymentInstrumentId = paymentInstrumentId;
	}

	public Double getAmount()
	{
		return amount;
	}

	public void setAmount(Double amount)
	{
		this.amount = amount;
	}

	public PaymentModes getTransferMode()
	{
		return transferMode;
	}

	public void setTransferMode(PaymentModes transferMode)
	{
		this.transferMode = transferMode;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	public BeneficiaryData getBeneficiaryData()
	{
		return beneficiaryData;
	}

	public void setBeneficiaryData(BeneficiaryData beneficiaryData)
	{
		this.beneficiaryData = beneficiaryData;
	}

}
