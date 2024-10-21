package com.mobile.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cashfree.lib.payout.domains.response.GetTransferResponse;
import com.mobile.app.request.PaymentForm;
import com.mobile.app.service.CashfreePaymentService;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/payment")
public class PaymentController
{

	@Autowired
	public CashfreePaymentService cashfreePaymentService;

	@PostMapping("/transfer")
	public GetTransferResponse requestTransfer(@RequestBody final PaymentForm paymentForm)
	{

		return cashfreePaymentService.requestTransfer(paymentForm);
	}

	@GetMapping("/transferStatus")
	public GetTransferResponse getTransactionStatus(@RequestParam(value = "trasactionId") final String transactionId)
	{

		return cashfreePaymentService.getTransactionStatus(transactionId);
	}

}