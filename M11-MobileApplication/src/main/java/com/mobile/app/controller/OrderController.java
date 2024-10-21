package com.mobile.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.app.entity.WeighmentData;
import com.mobile.app.response.DeliveryOrdersResponse;
import com.mobile.app.response.OrderDetailsResponse;
import com.mobile.app.response.PaymentHistoryResponse;
import com.mobile.app.response.TripHistoryResponse;
import com.mobile.app.response.WeighmentDataResponse;
import com.mobile.app.service.OrderService;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/orders")
public class OrderController
{

	@Autowired
	private OrderService orderService;

	@PostMapping("/create")
	public String createOrder(@RequestBody final WeighmentData data)
	{
		return orderService.createOrder(data);
	}

	@PostMapping("/reschedule")
	public String rescheduleFollowup(@RequestParam final Integer fboId, @RequestParam final String rescheduledDate)
	{
		return orderService.rescheduleFollowup(fboId, rescheduledDate);
	}

	@PostMapping("/preparebill")
	public String prepareBill(@RequestBody final WeighmentData data)
	{
		return orderService.prepareBill(data);
	}

	@GetMapping("/{orderId}")
	public WeighmentDataResponse getOrderDetails(@PathVariable("orderId") final Integer orderId)
	{
		return orderService.getOrderDetails(orderId);
	}

	@PostMapping("/paymentApproval/{orderId}")
	public String createPaymentApprovalRequest(@PathVariable("orderId") final Integer orderId)
	{
		return orderService.sendPaymentRequest(orderId);
	}

	@GetMapping("/fbo/{enrollmentId}")
	public List<WeighmentDataResponse> getOrdersByFbo(@PathVariable("enrollmentId") final Integer enrollmentId)
	{
		return orderService.getOrdersByFbo(enrollmentId);
	}

	@GetMapping("/assigned/{empId}")
	public DeliveryOrdersResponse getOrdersAssignedToCeOrDriver(@PathVariable("empId") final Integer empId)
	{
		return orderService.getOrdersAssignedToCeOrDriver(empId);
	}

	@PostMapping("/track/create/{poId}")
	public String createTracking(@PathVariable("poId") final Integer poId)
	{
		return orderService.createTracking(poId);
	}

	@PostMapping("/accept")
	public String saveAcceptance(@RequestParam("orderId") final Integer orderId)
	{
		return orderService.saveAcceptance(orderId);
	}

	@GetMapping("/employee/{orderId}")
	public OrderDetailsResponse getOrdersDetails(@PathVariable("orderId") final Integer orderId)
	{
		return orderService.getOrdersDetails(orderId);
	}

	@GetMapping("/ruco/{empId}")
	public List<OrderDetailsResponse> getOrdersDetailsForRucoCurrentDate(@PathVariable("empId") final Integer empId)
	{
		return orderService.getOrdersDetailsForRucoCurrentDate(empId);
	}

	@GetMapping("/trip-history/{empId}")
	public List<TripHistoryResponse> getTripHistory(@PathVariable("empId") final Integer empId)
	{
		return orderService.getTripHistory(empId);
	}

	@GetMapping("/payment-history/{empId}/{role}")
	public List<PaymentHistoryResponse> getPaymentHistory(@PathVariable("empId") final Integer empId,
					@PathVariable("role") final String role)
	{
		if ("UCO".equalsIgnoreCase(role))
		{
			return orderService.getPaymentHistoryForUco(empId);
		}

		return orderService.getPaymentHistory(empId);

	}

}