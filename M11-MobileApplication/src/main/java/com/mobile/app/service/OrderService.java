package com.mobile.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mobile.app.entity.DeliveryTracking;
import com.mobile.app.entity.Employee;
import com.mobile.app.entity.FBOApprovalWorkflow;
import com.mobile.app.entity.FBOTermsConditions;
import com.mobile.app.entity.FboFollowup;
import com.mobile.app.entity.FoodBusinessOperator;
import com.mobile.app.entity.VehicleDetails;
import com.mobile.app.entity.WeighmentData;
import com.mobile.app.enums.ApprovalStatus;
import com.mobile.app.enums.ApprovalType;
import com.mobile.app.enums.OrderStatus;
import com.mobile.app.repository.DeliveryTrackingRepo;
import com.mobile.app.repository.EmployeeRepo;
import com.mobile.app.repository.FBOTermsConditionsRepo;
import com.mobile.app.repository.FboFollowupRepo;
import com.mobile.app.repository.FboRepo;
import com.mobile.app.repository.VehicleRepo;
import com.mobile.app.repository.WeighmentDataRepo;
import com.mobile.app.repository.WorkflowRepo;
import com.mobile.app.response.DeliveryOrdersResponse;
import com.mobile.app.response.EmployeeResponse;
import com.mobile.app.response.FboResponse;
import com.mobile.app.response.OrderDetailsResponse;
import com.mobile.app.response.PaymentHistoryResponse;
import com.mobile.app.response.TripHistoryResponse;
import com.mobile.app.response.VehicleResponse;
import com.mobile.app.response.WeighmentDataResponse;
import com.mobile.app.util.MElevenUtil;

@Service
public class OrderService
{

	private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);

	@Value("${emission.factor.petrol}")
	private String petrolEmissionFactor;

	@Value("${emission.factor.diesel}")
	private String dieselEmissionFactor;

	@Value("${emission.factor.cng}")
	private String cngEmissionFactor;

	@Value("${workflow.ce.acceptance}")
	private boolean isCeAcceptanceWorkflow;

	@Autowired
	private WeighmentDataRepo weighmentDataRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private FBOTermsConditionsRepo fboTermsRepo;

	@Autowired
	private WorkflowRepo workflowRepo;

	@Autowired
	private FboRepo fboRepo;

	@Autowired
	private FboFollowupRepo fboFollowupRepo;

	@Autowired
	private DeliveryTrackingRepo deliveryTrackingRepo;

	@Autowired
	private VehicleRepo vehicleRepo;

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private DeliveryTrackingService deliveryTrackingService;

	@Autowired
	private FileService fileService;

	@Autowired
	private WhatsAppService whatsAppService;

	public String createOrder(final WeighmentData data)
	{
		try
		{
			data.setOrderStatus(OrderStatus.PENDING.toString());
			final WeighmentData savedData = weighmentDataRepo.save(data);
			updateFollowupDate(savedData);
			createTracking(savedData.getPurchaseOrderId());

			return "Your Purchase Order Id is : " + savedData.getPurchaseOrderId();
		}
		catch (Exception e)
		{
			LOG.error("createOrder() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return MElevenUtil.SOMETHING_WENT_WRONG;
	}

	private void updateFollowupDate(final WeighmentData orderData)
	{
		try
		{
			final FboFollowup fboFollowup = fboFollowupRepo.findSingleByFboIdAndTodayFollowupDate(orderData.getAssignedFBO());
			if (Objects.nonNull(fboFollowup))
			{
				fboFollowup.setFboId(orderData.getAssignedFBO());
				fboFollowup.setRucoId(orderData.getCreatedBy());

				final String formattedCurrentDate = MElevenUtil.dateFormatddMMyyyy.format(new Date());

				if (fboFollowup.getActualFollowupDate() != null && MElevenUtil.dateFormatddMMyyyy
								.format(fboFollowup.getActualFollowupDate()).equals(formattedCurrentDate))
				{
					fboFollowup.setFollowedupOnActualDate(true);
				}
				else if (fboFollowup.getPostponeDate() != null && MElevenUtil.dateFormatddMMyyyy
								.format(fboFollowup.getPostponeDate()).equals(formattedCurrentDate))
				{
					fboFollowup.setFollowedupOnPostponeDate(true);
				}
				else if (fboFollowup.getReadyForPickupDate() != null && MElevenUtil.dateFormatddMMyyyy
								.format(fboFollowup.getReadyForPickupDate()).equals(formattedCurrentDate))
				{
					fboFollowup.setFollowedupOnReadyForPickupDate(true);
				}

				fboFollowupRepo.save(fboFollowup);
			}
		}
		catch (final Exception e)
		{
			LOG.error("updateFollowupDate() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
	}

	public String rescheduleFollowup(final Integer fboId, final String rescheduledDate)
	{
		try
		{
			final FboFollowup fboFollowup = fboFollowupRepo.findSingleByFboIdAndTodayFollowupDate(fboId);
			if (Objects.nonNull(fboFollowup))
			{
				final Date formattedRescheduledDate = MElevenUtil.dateFormatddMMyyyy.parse(rescheduledDate);

				if (fboFollowup.getPostponeDate() != null && MElevenUtil.dateFormatddMMyyyy.format(fboFollowup.getPostponeDate())
								.equals(MElevenUtil.dateFormatddMMyyyy.format(new Date())))
				{
					final FboFollowup rescheduleFollowup = new FboFollowup();
					rescheduleFollowup.setFboId(fboFollowup.getFboId());
					rescheduleFollowup.setRucoId(fboFollowup.getRucoId());
					rescheduleFollowup.setActualFollowupDate(fboFollowup.getActualFollowupDate());
					rescheduleFollowup.setFollowedupOnActualDate(fboFollowup.isFollowedupOnActualDate());
					rescheduleFollowup.setPostponeDate(formattedRescheduledDate);
					rescheduleFollowup.setFollowedupOnPostponeDate(false);
					rescheduleFollowup.setReadyForPickupDate(fboFollowup.getReadyForPickupDate());
					rescheduleFollowup.setFollowedupOnReadyForPickupDate(fboFollowup.isFollowedupOnReadyForPickupDate());
					fboFollowupRepo.save(rescheduleFollowup);
				}
				else
				{
					fboFollowup.setPostponeDate(formattedRescheduledDate);
					fboFollowup.setFollowedupOnActualDate(true);
					fboFollowupRepo.save(fboFollowup);
				}

				return "Rescheduled Successfully";
			}
		}
		catch (final Exception e)
		{
			LOG.error("rescheduleFollowup() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return "Rescheduling Failed";
	}

	public String prepareBill(final WeighmentData data)
	{
		try
		{
			final WeighmentData existingData = weighmentDataRepo.findById(data.getPurchaseOrderId()).orElse(null);

			if (Objects.nonNull(existingData))
			{
				existingData.setWeightCalculatedInKG(MElevenUtil.getDoubleValue(data.getWeightCalculatedInKG()));
				existingData.setPaymentStatus(ApprovalStatus.PENDING.toString());
				existingData.setPhoto(fileService.saveFileToDirectory("purchase_order_" + data.getPurchaseOrderId() + ".jpg",
								"weighmentImages", data.getPhoto()));
				calculatePrice(existingData);
				weighmentDataRepo.save(existingData);

				final VehicleDetails vehicleDetails = vehicleRepo.findByVehicleId(existingData.getVehicle());
				final DeliveryTracking deliveryTracking = deliveryTrackingRepo.findByPoId(existingData.getPurchaseOrderId());
				deliveryTracking.setDistanceTravelledInKm(
								deliveryTrackingService.getDistanceTravelled(data.getPurchaseOrderId()));
				if (Objects.nonNull(vehicleDetails) && Objects.nonNull(deliveryTracking))
				{
					deliveryTracking.setGasEmissionOccurred(
									calculateGasEmission(MElevenUtil.getDoubleValue(deliveryTracking.getDistanceTravelledInKm()),
													vehicleDetails.getFuelType()));
					deliveryTracking.setEndTime(new Date());
					deliveryTrackingRepo.save(deliveryTracking);
				}

				final FoodBusinessOperator fbo = fboRepo.findById(existingData.getAssignedFBO()).orElse(null);
				if (Objects.nonNull(fbo))
				{
					final FBOApprovalWorkflow fboApproval = new FBOApprovalWorkflow();
					fboApproval.setFbo(fbo.getEnrollmentId());
					fboApproval.setPurchaseOrder(existingData.getPurchaseOrderId());
					fboApproval.setApprovalType(ApprovalType.PAYMENT);
					fboApproval.setFboApproval(true);
					fboApproval.setApprovalStatus(ApprovalStatus.PENDING);

					workflowRepo.save(fboApproval);

					whatsAppService.sendWhatsAppMessage(fboApproval.getWorkFlowId(), fbo.getName(), fbo.getContactNo(),
									existingData.getWeightCalculatedInKG(), existingData.getTotalPrice());

					return "Bill has been prepared and sent for fbo approval";
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("prepareBill() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return "Purchase order is not found with id : " + data.getPurchaseOrderId();
	}

	private void calculatePrice(final WeighmentData updatedData)
	{
		final Double weightCalculated = MElevenUtil.getDoubleValue(updatedData.getWeightCalculatedInKG());
		try
		{
			final FBOTermsConditions fboTerms = fboTermsRepo.findById(updatedData.getAssignedFBO()).orElse(null);

			if (Objects.nonNull(fboTerms))
			{
				final Double paymentCostPerKg = MElevenUtil.getDoubleValue(fboTerms.getPaymentCostPerKg());
				final Double totalPrice = (weightCalculated * paymentCostPerKg);
				updatedData.setTotalPrice(totalPrice);
			}
		}
		catch (final Exception e)
		{
			LOG.error("calculatePrice() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
	}

	public WeighmentDataResponse getOrderDetails(final Integer orderId)
	{
		try
		{
			final WeighmentData orderDetails = weighmentDataRepo.findById(orderId).orElse(null);

			if (Objects.nonNull(orderDetails))
			{
				return mapper.map(orderDetails, WeighmentDataResponse.class);
			}
		}
		catch (final Exception e)
		{
			LOG.error("getOrderDetails() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return null;
	}

	public String sendPaymentRequest(final Integer orderId)
	{
		try
		{
			final WeighmentData orderDetails = weighmentDataRepo.findById(orderId).orElse(null);

			if (Objects.nonNull(orderDetails))
			{
				final FoodBusinessOperator fbo = fboRepo.findById(orderDetails.getAssignedFBO()).orElse(null);

				if (Objects.nonNull(fbo) && Objects.nonNull(fbo.getAssignedUCO()) && Objects.nonNull(fbo.getAssignedRUCO()))
				{
					requestForRUCO(fbo, orderId);
					requestForUCO(fbo, orderId);

					return "Request has been sent for approval";
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("sendPaymentRequest() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return MElevenUtil.SOMETHING_WENT_WRONG;
	}

	private void requestForRUCO(final FoodBusinessOperator fbo, final Integer orderId)
	{
		final FBOApprovalWorkflow paymentFlow = new FBOApprovalWorkflow();
		paymentFlow.setFbo(fbo.getEnrollmentId());
		paymentFlow.setApprover(fbo.getAssignedRUCO());
		paymentFlow.setPurchaseOrder(orderId);
		paymentFlow.setApprovalType(ApprovalType.PAYMENT);
		paymentFlow.setApprovalStatus(ApprovalStatus.PENDING);

		workflowRepo.save(paymentFlow);
	}

	private void requestForUCO(final FoodBusinessOperator fbo, final Integer orderId)
	{
		final FBOApprovalWorkflow paymentFlow = new FBOApprovalWorkflow();
		paymentFlow.setFbo(fbo.getEnrollmentId());
		paymentFlow.setApprover(fbo.getAssignedUCO());
		paymentFlow.setPurchaseOrder(orderId);
		paymentFlow.setApprovalType(ApprovalType.PAYMENT);
		paymentFlow.setApprovalStatus(ApprovalStatus.PENDING_AT_RUCO);

		workflowRepo.save(paymentFlow);
	}

	public List<WeighmentDataResponse> getOrdersByFbo(final Integer enrollmentId)
	{
		final List<WeighmentDataResponse> fboOrdersList = new ArrayList<>();
		try
		{
			final List<WeighmentData> listOfOrdersByFbo = weighmentDataRepo.findAllByAssignedFBO(enrollmentId);
			if (!CollectionUtils.isEmpty(listOfOrdersByFbo))
			{
				for (final WeighmentData weighmentData : listOfOrdersByFbo)
				{
					fboOrdersList.add(mapper.map(weighmentData, WeighmentDataResponse.class));
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getOrderByFbo() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return fboOrdersList;
	}

	public DeliveryOrdersResponse getOrdersAssignedToCeOrDriver(final Integer empId)
	{
		final DeliveryOrdersResponse assignedDeliveryOrder = new DeliveryOrdersResponse();
		try
		{
			final WeighmentData currentlyAssignedOrder = weighmentDataRepo.findByAssignedCEOrDriver(empId, empId);

			if (Objects.nonNull(currentlyAssignedOrder))
			{
				final FoodBusinessOperator fbo = fboRepo.findById(currentlyAssignedOrder.getAssignedFBO()).orElse(null);
				if (Objects.nonNull(fbo))
				{
					assignedDeliveryOrder.setEnrollmentId(mapper.map(fbo.getEnrollmentId(), Integer.class));
					assignedDeliveryOrder.setName(mapper.map(fbo.getName(), String.class));
					assignedDeliveryOrder.setRestaurantName(mapper.map(fbo.getRestaurantName(), String.class));
					assignedDeliveryOrder.setContactNo(mapper.map(fbo.getContactNo(), String.class));
					assignedDeliveryOrder.setSourceAddress(mapper.map(currentlyAssignedOrder.getSourceAddress(), String.class));
					assignedDeliveryOrder.setDestinationAddress(
									mapper.map(currentlyAssignedOrder.getDestinationAddress(), String.class));
					assignedDeliveryOrder.setPoId(mapper.map(currentlyAssignedOrder.getPurchaseOrderId(), Integer.class));
					assignedDeliveryOrder.setOrderStatus(mapper.map(currentlyAssignedOrder.getOrderStatus(), String.class));
				}
				final DeliveryTracking deliveryTracking = deliveryTrackingRepo
								.findByPoId(currentlyAssignedOrder.getPurchaseOrderId());
				if (Objects.nonNull(deliveryTracking))
				{
					assignedDeliveryOrder.setTrackId(mapper.map(deliveryTracking.getTrackId(), Integer.class));
					assignedDeliveryOrder.setCeAcceptanceWorkflow(isCeAcceptanceWorkflow);
					if (isCeAcceptanceWorkflow)
					{
						assignedDeliveryOrder.setCeAccepted(mapper.map(deliveryTracking.isCeAccepted(), Boolean.class));
					}
				}
				if (currentlyAssignedOrder.getWeightCalculatedInKG() != null
								&& currentlyAssignedOrder.getWeightCalculatedInKG() != 0.0)
				{
					assignedDeliveryOrder.setWeightCalculated(true);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getOrdersAssignedToCeOrDriver() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return assignedDeliveryOrder;
	}

	public String createTracking(final Integer poId)
	{
		try
		{
			if (Objects.nonNull(poId) && poId != 0)
			{
				final DeliveryTracking deliveryTracking = new DeliveryTracking();
				deliveryTracking.setPoId(poId);
				deliveryTracking.setStartTime(new Date());

				deliveryTrackingRepo.save(deliveryTracking);
				return "Tracking Created Successfully";
			}
		}
		catch (final Exception e)
		{
			LOG.error("createTracking() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return MElevenUtil.SOMETHING_WENT_WRONG;
	}

	public Double calculateGasEmission(final Double distanceTravelledInKm, final String fuelType)
	{
		Double gasEmissionOccurred = 0.0;
		if (Objects.nonNull(distanceTravelledInKm))
		{
			if ("petrol".equalsIgnoreCase(fuelType))
			{
				gasEmissionOccurred = distanceTravelledInKm * Double.valueOf(petrolEmissionFactor);
			}
			else if ("deisel".equalsIgnoreCase(fuelType))
			{
				gasEmissionOccurred = distanceTravelledInKm * Double.valueOf(dieselEmissionFactor);
			}
			else
			{
				gasEmissionOccurred = distanceTravelledInKm * Double.valueOf(cngEmissionFactor);
			}
		}
		return gasEmissionOccurred;
	}

	public String saveAcceptance(final Integer orderId)
	{
		try
		{
			final DeliveryTracking tracking = deliveryTrackingRepo.findByPoId(orderId);
			if (Objects.nonNull(tracking))
			{
				tracking.setCeAccepted(true);
				deliveryTrackingRepo.save(tracking);
				return "Order has been accepted";
			}
		}
		catch (final Exception e)
		{
			LOG.error("saveAcceptance() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return "Purchase Order Id is not found";
	}

	public OrderDetailsResponse getOrdersDetails(final Integer orderId)
	{
		OrderDetailsResponse orderDetailsResponse = new OrderDetailsResponse();

		try
		{
			final WeighmentData weighmentData = weighmentDataRepo.findById(orderId).orElse(null);
			if (Objects.nonNull(weighmentData))
			{
				orderDetailsResponse = mapper.map(weighmentData, OrderDetailsResponse.class);

				orderDetailsResponse.setAssignedFBO(
								mapper.map(fboRepo.findById(weighmentData.getAssignedFBO()).orElse(null), FboResponse.class));

				orderDetailsResponse.setVehicleResponse(
								mapper.map(vehicleRepo.findById(weighmentData.getVehicle()).orElse(null), VehicleResponse.class));
			}
		}
		catch (final Exception e)
		{
			LOG.error("getOrdersDetails() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return orderDetailsResponse;
	}

	public List<OrderDetailsResponse> getOrdersDetailsForRucoCurrentDate(final Integer empId)
	{
		final List<OrderDetailsResponse> currentlyAssignedOrderDetails = new ArrayList<>();
		try
		{
			final List<WeighmentData> currentOrdersList = weighmentDataRepo.findCurrentDayOrdersAssignedByRuco(empId);
			if (!CollectionUtils.isEmpty(currentOrdersList))
			{
				for (final WeighmentData weighmentData : currentOrdersList)
				{
					final OrderDetailsResponse orderDetailsResponse = mapper.map(weighmentData, OrderDetailsResponse.class);

					orderDetailsResponse.setVehicleResponse(mapper
									.map(vehicleRepo.findById(weighmentData.getVehicle()).orElse(null), VehicleResponse.class));

					orderDetailsResponse.setDriver(mapper.map(employeeRepo.findById(weighmentData.getDriver()).orElse(null),
									EmployeeResponse.class));
					currentlyAssignedOrderDetails.add(orderDetailsResponse);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getOrdersDetailsForRucoCurrentDate() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return currentlyAssignedOrderDetails;
	}

	public List<TripHistoryResponse> getTripHistory(final Integer empId)
	{
		final List<TripHistoryResponse> tripHistoryResponseList = new ArrayList<TripHistoryResponse>();
		final TripHistoryResponse tripHistoryResponse = new TripHistoryResponse();
		for (WeighmentData weighmentData : weighmentDataRepo.findByCreatedBy(empId))
		{
			tripHistoryResponse.setDate(weighmentData.getCreationTime());
			final FoodBusinessOperator fbo = fboRepo.findById(weighmentData.getAssignedFBO()).orElse(null);
			tripHistoryResponse.setRestaurantName(fbo.getRestaurantName());
			VehicleDetails vehicle = vehicleRepo.findById(weighmentData.getVehicle()).orElse(null);

			tripHistoryResponse.setVehicleNumber(vehicle.getVehicleNumber());
			tripHistoryResponse.setWeightCalculatedInKg(weighmentData.getWeightCalculatedInKG());
			tripHistoryResponseList.add(tripHistoryResponse);
		}
		return tripHistoryResponseList;

	}

	public List<PaymentHistoryResponse> getPaymentHistory(final Integer empId)
	{
		final List<PaymentHistoryResponse> paymentHistoryResponseList = new ArrayList<PaymentHistoryResponse>();
		final List<WeighmentData> weighmentDataList = weighmentDataRepo.findByCreatedByAndPaymentStatus(empId);
		if (!CollectionUtils.isEmpty(weighmentDataList))
		{
			final PaymentHistoryResponse paymentHistoryResponse = new PaymentHistoryResponse();
			for (final WeighmentData weighmentData : weighmentDataList)
			{

				final FoodBusinessOperator fbo = fboRepo.findById(weighmentData.getAssignedFBO()).orElse(null);
				if (Objects.nonNull(fbo))
				{
					paymentHistoryResponse.setDate(weighmentData.getCreationTime());
					paymentHistoryResponse.setFboName(fbo.getName());
					paymentHistoryResponse.setTotalPrice(weighmentData.getTotalPrice());
					paymentHistoryResponse.setTransactionId(weighmentData.getTransactionId());
					paymentHistoryResponseList.add(paymentHistoryResponse);
				}
			}
		}
		return paymentHistoryResponseList;
	}

	public List<PaymentHistoryResponse> getPaymentHistoryForUco(final Integer empId)
	{
		final List<PaymentHistoryResponse> paymentHistoryResponseList = new ArrayList<PaymentHistoryResponse>();
		final Employee ucoData = employeeRepo.findById(empId).orElse(null);
		if (Objects.nonNull(ucoData))
		{
			final List<Employee> rucoList = employeeRepo.findByRoleAndRegion("RUCO", ucoData.getRegion());
			if (Objects.nonNull(rucoList))
			{
				for (final Employee rucoData : rucoList)
				{
					paymentHistoryResponseList.addAll(getPaymentHistory(rucoData.getEmpId()));
				}
			}
		}
		return paymentHistoryResponseList;
	}

}
