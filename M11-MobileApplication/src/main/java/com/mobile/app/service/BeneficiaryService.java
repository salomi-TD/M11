package com.mobile.app.service;

import com.cashfree.lib.exceptions.ResourceAlreadyExistsException;
import com.cashfree.lib.exceptions.ResourceDoesntExistException;
import com.cashfree.lib.payout.clients.Beneficiary;
import com.cashfree.lib.payout.clients.Payouts;
import com.cashfree.lib.payout.domains.BeneficiaryDetails;
import com.cashfree.lib.payout.domains.response.GetBeneficiaryResponse;
import com.mobile.app.response.BeneficiaryData;

import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class BeneficiaryService
{

	private static final Logger LOG = Logger.getLogger(BeneficiaryService.class.getName());

	public BeneficiaryDetails addBeneficiary(final BeneficiaryData beneficiaryData, final Payouts payouts)
	{
		final BeneficiaryDetails beneficiaryDetails = new BeneficiaryDetails().setBeneId(beneficiaryData.getCode())
						.setName(beneficiaryData.getName()).setEmail(beneficiaryData.getEmail())
						.setPhone(beneficiaryData.getPhone()).setBankAccount(beneficiaryData.getBankAccount())
						.setIfsc(beneficiaryData.getIfsc()).setVpa(beneficiaryData.getUpi())
						.setAddress1(beneficiaryData.getAddress()).setCity(beneficiaryData.getCity())
						.setState(beneficiaryData.getState()).setPincode(beneficiaryData.getPincode());

		try
		{
			final Beneficiary beneficiary = new Beneficiary(payouts);
			beneficiary.addBeneficiary(beneficiaryDetails);
			LOG.info("Beneficiary added");
		}
		catch (final ResourceAlreadyExistsException x)
		{
			LOG.warning(x.getMessage());
		}
		return beneficiaryDetails;
	}

	public BeneficiaryDetails getBeneficiary(final BeneficiaryData beneficiaryData, final Payouts payouts)
	{
		final Beneficiary beneficiary = new Beneficiary(payouts);
		try
		{
			LOG.info("Trying to fetch beneficiary based on beneId");
			beneficiary.getBeneficiaryDetails(beneficiaryData.getCode());
			return convertBeneficiaryDetails(beneficiary.getBeneficiaryDetails(beneficiaryData.getCode()));
		}
		catch (final ResourceDoesntExistException x)
		{
			LOG.warning(x.getMessage());
			LOG.info("Trying to fetch beneficiary based on account details");
			try
			{
				final String beneId = beneficiary.getBeneficiaryId(beneficiaryData.getBankAccount(), beneficiaryData.getIfsc());
				LOG.info(beneficiary.getBeneficiaryDetails(beneId).toString());
				return convertBeneficiaryDetails(beneficiary.getBeneficiaryDetails(beneId));
			}
			catch (final ResourceDoesntExistException ex)
			{
				LOG.warning(ex.getMessage());
			}
		}
		return null;
	}

	public void removeBeneficiary(final String beneId, final Payouts payouts)
	{
		final Beneficiary beneficiary = new Beneficiary(payouts);
		beneficiary.removeBeneficiary(beneId);
	}

	public BeneficiaryDetails convertBeneficiaryDetails(final GetBeneficiaryResponse.Payload payload)
	{
		return new BeneficiaryDetails().setBeneId(payload.getBeneId()).setName(payload.getName()).setEmail(payload.getEmail())
						.setPhone(payload.getPhone()).setBankAccount(payload.getBankAccount()).setIfsc(payload.getIfsc())
						.setVpa(payload.getVpa()).setAddress1(payload.getAddress1()).setCity(payload.getCity())
						.setState(payload.getState());
	}

}