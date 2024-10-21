package com.mobile.app.service;

import com.cashfree.lib.constants.Constants;

import com.cashfree.lib.payout.clients.Payouts;
import com.cashfree.lib.payout.clients.Transfers;
import com.cashfree.lib.payout.domains.BeneficiaryDetails;
import com.cashfree.lib.payout.domains.request.RequestTransferRequest;
import com.cashfree.lib.payout.domains.response.GetTransferResponse;
import com.mobile.app.request.PaymentForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@Service
public class CashfreePaymentService
{

	private static final Logger LOG = Logger.getLogger(CashfreePaymentService.class.getName());

	@Autowired
	private Environment environment;

	@Autowired
	public BeneficiaryService beneficiaryService;

	public GetTransferResponse requestTransfer(final PaymentForm paymentForm)
	{
		final Payouts payouts = Payouts.getInstance(Constants.Environment.TEST, environment.getRequiredProperty("client.ID"),
						environment.getRequiredProperty("client.Secret"));
		BeneficiaryDetails beneficiaryDetails = null;
		LOG.info("" + payouts.init());
		LOG.info("payouts initialized");
		if (!payouts.verifyToken())
		{
			LOG.info("Token is invalid");
			return (GetTransferResponse) new GetTransferResponse().setMessage("Token is invalid");
		}
		LOG.info("Token is valid");
		try
		{
			beneficiaryDetails = beneficiaryService.getBeneficiary(paymentForm.getBeneficiaryData(), payouts);
			if (beneficiaryDetails == null)
			{
				beneficiaryDetails = beneficiaryService.addBeneficiary(paymentForm.getBeneficiaryData(), payouts);
			}

			LOG.info("initiating Transfer Request");
			final Transfers transfers = new Transfers(payouts);
			final String transferId = "Trs" + ThreadLocalRandom.current().nextInt(0, 1000000);

			final RequestTransferRequest request = new RequestTransferRequest().setBeneId(beneficiaryDetails.getBeneId())
							.setAmount(new BigDecimal(paymentForm.getAmount())).setTransferId(transferId)
							.setRemarks(paymentForm.getRemarks());

			if (Objects.nonNull(paymentForm.getTransferMode()))
			{
				request.setTransferMode(paymentForm.getTransferMode().toString());
			}
			LOG.info("" + transfers.requestTransfer(request));
			LOG.info("" + transfers.getTransferStatus(null, transferId));
			return transfers.getTransferStatus(null, transferId);
		}
		catch (final Exception ex)
		{
			LOG.warning(ex.getMessage());
			return (GetTransferResponse) new GetTransferResponse().setMessage(ex.getMessage());
		}

	}

	public GetTransferResponse getTransactionStatus(final String trasactionId)
	{
		try
		{
			final Payouts payouts = Payouts.getInstance(Constants.Environment.TEST, environment.getRequiredProperty("client.ID"),
							environment.getRequiredProperty("client.Secret"));
			LOG.info("" + payouts.init());
			LOG.info("payouts initialized");
			final boolean isTokenValid = payouts.verifyToken();
			if (!isTokenValid)
			{
				LOG.info("Token is invalid");
				return (GetTransferResponse) new GetTransferResponse().setMessage("Token is invalid");
			}
			LOG.info("Token is valid");
			final Transfers transfers = new Transfers(payouts);
			LOG.info("" + transfers.getTransferStatus(trasactionId, null));
			return transfers.getTransferStatus(trasactionId, null);
		}
		catch (final Exception ex)
		{
			LOG.warning(ex.getMessage());
			return (GetTransferResponse) new GetTransferResponse().setMessage(ex.getMessage());
		}
	}
}