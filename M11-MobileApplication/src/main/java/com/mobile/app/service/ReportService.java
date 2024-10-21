package com.mobile.app.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mobile.app.entity.BdeEnquiryTracking;
import com.mobile.app.entity.BdeMonthlyTarget;
import com.mobile.app.entity.DeliveryTracking;
import com.mobile.app.entity.Employee;
import com.mobile.app.entity.FboFollowup;
import com.mobile.app.entity.FoodBusinessOperator;
import com.mobile.app.entity.VehicleDetails;
import com.mobile.app.entity.VehicleReading;
import com.mobile.app.entity.WeighmentData;
import com.mobile.app.enums.ApprovalStatus;
import com.mobile.app.enums.ApprovalType;
import com.mobile.app.repository.BdeEnquiryTrackingRepo;
import com.mobile.app.repository.BdeMonthlyTargetRepo;
import com.mobile.app.repository.DeliveryTrackingRepo;
import com.mobile.app.repository.EmployeeRepo;
import com.mobile.app.repository.FboFollowupRepo;
import com.mobile.app.repository.FboRepo;
import com.mobile.app.repository.VehicleReadingRepo;
import com.mobile.app.repository.VehicleRepo;
import com.mobile.app.repository.WeighmentDataRepo;
import com.mobile.app.response.BdeReport;
import com.mobile.app.response.EmployeeResponse;
import com.mobile.app.response.FboListResponse;
import com.mobile.app.response.FboReportResponse;
import com.mobile.app.response.FboResponse;
import com.mobile.app.response.ReportSearchRequest;
import com.mobile.app.response.UcoReport;
import com.mobile.app.response.VehicleResponse;
import com.mobile.app.response.WeighmentDataResponse;
import com.mobile.app.util.MElevenUtil;

import io.micrometer.common.util.StringUtils;

@Service
public class ReportService
{

	private static final Logger LOG = LoggerFactory.getLogger(ReportService.class);

	@Autowired
	private VehicleReadingRepo vehicleReadingsRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private FboRepo fboRepo;

	@Autowired
	private WeighmentDataRepo weightmentDataRepo;

	@Autowired
	private FboFollowupRepo fboFollowupRepo;

	@Autowired
	private VehicleRepo vehicleRepo;

	@Autowired
	private DeliveryTrackingRepo deliveryTrackingRepo;

	@Autowired
	private BdeEnquiryTrackingRepo bdeEnquiryTrackingRepo;

	@Autowired
	private BdeMonthlyTargetRepo bdeTargetRepo;

	public FboReportResponse generateFboReport(final Integer enrollmentId)
	{
		final FboReportResponse reportResponse = new FboReportResponse();
		try
		{
			final FoodBusinessOperator fbo = fboRepo.findById(enrollmentId).orElse(null);
			if (Objects.nonNull(fbo))
			{
				reportResponse.setFboResponse(mapper.map(fbo, FboResponse.class));
			}

			final List<WeighmentData> weighmentDataList = weightmentDataRepo.findAllByAssignedFBOAndDateRange(enrollmentId,
							Date.from(LocalDate.now().minusMonths(3).atStartOfDay(ZoneId.systemDefault()).toInstant()),
							new Date());

			if (!CollectionUtils.isEmpty(weighmentDataList))
			{
				final List<WeighmentDataResponse> weighmentDataRespList = new ArrayList<>();
				for (final WeighmentData weighmentData : weighmentDataList)
				{
					weighmentDataRespList.add(mapper.map(weighmentData, WeighmentDataResponse.class));
				}
				reportResponse.setWeighmentDataList(weighmentDataRespList);
				reportResponse.setTotalWeightOfOil(weighmentDataRespList.stream()
								.mapToDouble(wDataResp -> MElevenUtil.getDoubleValue(wDataResp.getWeightCalculatedInKG())).sum());
				reportResponse.setPaidAmount(weighmentDataRespList.stream()
								.filter(wData -> ApprovalStatus.APPROVED.toString().equalsIgnoreCase(wData.getPaymentStatus()))
								.mapToDouble(wDataResp -> MElevenUtil.getDoubleValue(wDataResp.getTotalPrice())).sum());
				reportResponse.setPendingAmount(weighmentDataRespList.stream()
								.filter(wData -> wData.getPaymentStatus().contains(ApprovalStatus.PENDING.toString()))
								.mapToDouble(wDataResp -> MElevenUtil.getDoubleValue(wDataResp.getTotalPrice())).sum());
			}

		}
		catch (final Exception e)
		{
			LOG.error("generateFboReport() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return reportResponse;
	}

	public UcoReport generateUcoReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		final UcoReport ucoReport = new UcoReport();
		try
		{
			ucoReport.setTotalApprovedFbos(
							populateFboList(empId, ApprovalType.REGISTER, ApprovalStatus.APPROVED, searchRequest).size());
			ucoReport.setTotalPendingFbos(
							populateFboList(empId, ApprovalType.REGISTER, ApprovalStatus.PENDING, searchRequest).size());
			ucoReport.setTotalRejectedFbos(
							populateFboList(empId, ApprovalType.REGISTER, ApprovalStatus.REJECTED, searchRequest).size());
			ucoReport.setTotalOnHoldFbos(
							populateFboList(empId, ApprovalType.REGISTER, ApprovalStatus.ONHOLD, searchRequest).size());

			final List<List<WeighmentData>> listOfApprovedWeighmentDataList = populateWeighmentDataList(
							populateFboList(empId, ApprovalType.PAYMENT, ApprovalStatus.APPROVED, searchRequest), searchRequest);
			final Integer totalPaymentApprovedCount = calculateNoOfOrders(listOfApprovedWeighmentDataList,
							ApprovalStatus.APPROVED.toString());
			ucoReport.setTotalPaymentApprovedOrders(totalPaymentApprovedCount);

			final List<List<WeighmentData>> listOfPendingWeighmentDataList = populateWeighmentDataList(
							populateFboList(empId, ApprovalType.PAYMENT, ApprovalStatus.PENDING, searchRequest), searchRequest);
			final Integer totalPaymentPendingCount = calculateNoOfOrders(listOfPendingWeighmentDataList,
							ApprovalStatus.PENDING.toString());
			ucoReport.setTotalPaymentPendingOrders(totalPaymentPendingCount);

			final List<List<WeighmentData>> listOfPendingAtRucoWeighmentDataList = populateWeighmentDataList(
							populateFboList(empId, ApprovalType.PAYMENT, ApprovalStatus.PENDING_AT_RUCO, searchRequest),
							searchRequest);
			final Integer totalPaymentPendingAtRucoCount = calculateNoOfOrders(listOfPendingAtRucoWeighmentDataList,
							ApprovalStatus.PENDING_AT_RUCO.toString());
			ucoReport.setTotalPaymentPendingOrdersAtRuco(totalPaymentPendingAtRucoCount);

			ucoReport.setTotalOrders(totalPaymentApprovedCount + totalPaymentPendingCount + totalPaymentPendingAtRucoCount);

			// Total Weight of Oil
			final List<List<WeighmentData>> mergedWeighmentDataList = new ArrayList<>();
			mergedWeighmentDataList.addAll(listOfApprovedWeighmentDataList);
			mergedWeighmentDataList.addAll(listOfPendingWeighmentDataList);
			mergedWeighmentDataList.addAll(listOfPendingAtRucoWeighmentDataList);
			ucoReport.setTotalWeightOfOil(mergedWeighmentDataList.stream().flatMap(List::stream)
							.mapToDouble(wDataResp -> MElevenUtil.getDoubleValue(wDataResp.getWeightCalculatedInKG())).sum());

			// Total Pending Amount
			final List<List<WeighmentData>> mergedPendingWeighmentDataList = new ArrayList<>();
			mergedPendingWeighmentDataList.addAll(listOfPendingWeighmentDataList);
			mergedPendingWeighmentDataList.addAll(listOfPendingAtRucoWeighmentDataList);
			ucoReport.setPendingAmount(mergedPendingWeighmentDataList.stream().flatMap(List::stream)
							.mapToDouble(wDataResp -> MElevenUtil.getDoubleValue(wDataResp.getTotalPrice())).sum());

			// Total Paid Amount
			ucoReport.setPaidAmount(listOfApprovedWeighmentDataList.stream().flatMap(List::stream)
							.mapToDouble(wDataResp -> MElevenUtil.getDoubleValue(wDataResp.getTotalPrice())).sum());

			ucoReport.setGasEmissionByCng(calculateGasEmission(mergedWeighmentDataList, "CNG"));

			ucoReport.setGasEmissionByPetrol(calculateGasEmission(mergedWeighmentDataList, "Petrol"));

			ucoReport.setGasEmissionByDiesel(calculateGasEmission(mergedWeighmentDataList, "Diesel"));
		}
		catch (final Exception e)
		{
			LOG.error("generateUcoReport() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return ucoReport;
	}

	public BdeReport generateBdeReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		final BdeReport bdeReport = new BdeReport();
		try
		{
			final Employee bdeData = employeeRepo.findById(empId).orElse(null);
			if (Objects.nonNull(bdeData))
			{
				bdeReport.setBdeData(mapper.map(bdeData, EmployeeResponse.class));
			}
			final List<FboResponse> approvedFboList = populateFboList(empId, ApprovalType.REGISTER, ApprovalStatus.APPROVED,
							searchRequest);
			setProductivityForFbo(approvedFboList, bdeReport, "PRODUCTIVE");

			final List<FboResponse> pendingFboList = populateFboList(empId, ApprovalType.REGISTER, ApprovalStatus.PENDING,
							searchRequest);
			setProductivityForFbo(pendingFboList, bdeReport, ApprovalStatus.PENDING.toString());

			final List<FboResponse> rejectedFboList = populateFboList(empId, ApprovalType.REGISTER, ApprovalStatus.REJECTED,
							searchRequest);
			setProductivityForFbo(rejectedFboList, bdeReport, "UNPRODUCTIVE");

			final List<FboResponse> onHoldFboList = populateFboList(empId, ApprovalType.REGISTER, ApprovalStatus.ONHOLD,
							searchRequest);
			setProductivityForFbo(onHoldFboList, bdeReport, ApprovalStatus.ONHOLD.toString());

			populateUnregisteredFboList(empId, bdeReport, approvedFboList, pendingFboList, rejectedFboList, onHoldFboList,
							searchRequest);

			populateBdeMonthlyTargetList(empId, bdeReport, searchRequest);
		}
		catch (final Exception e)
		{
			LOG.error("generateBdeReport() - Exception occurred. Message:[{}]", e.getMessage(), e);
		}
		return bdeReport;
	}

	private void populateUnregisteredFboList(final Integer empId, final BdeReport bdeReport,
					final List<FboResponse> approvedFboList, final List<FboResponse> pendingFboList,
					final List<FboResponse> rejectedFboList, final List<FboResponse> onHoldFboList,
					final ReportSearchRequest searchRequest)
	{
		final List<FboResponse> fboList = new ArrayList<>();
		fboList.addAll(approvedFboList);
		fboList.addAll(pendingFboList);
		fboList.addAll(rejectedFboList);
		fboList.addAll(onHoldFboList);

		try
		{
			final Date endDate = new Date();
			List<BdeEnquiryTracking> bdeEnquiryTracking = new ArrayList<>();
			if (StringUtils.isNotBlank(searchRequest.getInterval()))
			{
				switch (searchRequest.getInterval())
				{
				case MElevenUtil.DAILY:
					bdeEnquiryTracking = bdeEnquiryTrackingRepo.findByBdeAndTimeInterval(empId,
									Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									endDate);
					break;

				case MElevenUtil.WEEKLY:
					bdeEnquiryTracking = bdeEnquiryTrackingRepo.findByBdeAndTimeInterval(empId,
									Date.from(LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									endDate);
					break;

				case MElevenUtil.MONTHLY:
					bdeEnquiryTracking = bdeEnquiryTrackingRepo.findByBdeAndTimeInterval(empId,
									Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									endDate);
					break;
				}
			}
			else
			{
				bdeEnquiryTracking = bdeEnquiryTrackingRepo.findByBdeAndTimeInterval(empId,
								MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getFromDate()),
								MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getToDate()));
			}
			bdeReport.setUnregisteredFboList(bdeEnquiryTracking.stream().filter(
							track -> fboList.stream().noneMatch(fbo -> fbo.getName().equalsIgnoreCase(track.getFboName())))
							.collect(Collectors.toList()));
		}
		catch (final Exception e)
		{
			LOG.error("populateUnregisteredFboList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
	}

	private void populateBdeMonthlyTargetList(final Integer empId, final BdeReport bdeReport,
					final ReportSearchRequest searchRequest)
	{
		try
		{
			final Date endDate = new Date();
			List<BdeMonthlyTarget> bdeMonthlyTarget = new ArrayList<>();
			if (StringUtils.isNotBlank(searchRequest.getInterval()))
			{
				switch (searchRequest.getInterval())
				{
				case MElevenUtil.DAILY:
					bdeMonthlyTarget = bdeTargetRepo.findByBdeAndTimeInterval(empId,
									Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									endDate);
					break;

				case MElevenUtil.WEEKLY:
					bdeMonthlyTarget = bdeTargetRepo.findByBdeAndTimeInterval(empId,
									Date.from(LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									endDate);
					break;

				case MElevenUtil.MONTHLY:
					bdeMonthlyTarget = bdeTargetRepo.findByBdeAndTimeInterval(empId,
									Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									endDate);
					break;
				}
			}
			else
			{
				bdeMonthlyTarget = bdeTargetRepo.findByBdeAndTimeInterval(empId,
								MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getFromDate()),
								MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getToDate()));
			}
			bdeReport.setBdeMonthlyTargetList(bdeMonthlyTarget);
		}
		catch (final Exception e)
		{
			LOG.error("populateUnregisteredFboList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
	}

	private List<FboResponse> populateFboList(final Integer empId, final ApprovalType approvalType,
					final ApprovalStatus approvalStatus, final ReportSearchRequest searchRequest)
	{
		final List<FboResponse> fboResponseList = new ArrayList<>();
		try
		{
			final Date endDate = new Date();
			List<FoodBusinessOperator> fboList = new ArrayList<>();
			if (StringUtils.isNotBlank(searchRequest.getInterval()))
			{
				switch (searchRequest.getInterval())
				{
				case MElevenUtil.DAILY:
					fboList = fboRepo.findByUcoOrBde(empId, approvalType, approvalStatus,
									Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									endDate);
					break;

				case MElevenUtil.WEEKLY:
					fboList = fboRepo.findByUcoOrBde(empId, approvalType, approvalStatus,
									Date.from(LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									endDate);
					break;

				case MElevenUtil.MONTHLY:
					fboList = fboRepo.findByUcoOrBde(empId, approvalType, approvalStatus,
									Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									endDate);
					break;
				}
			}
			else
			{
				fboList = fboRepo.findByUcoOrBde(empId, approvalType, approvalStatus,
								MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getFromDate()),
								MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getToDate()));
			}

			for (final FoodBusinessOperator fbo : fboList)
			{
				fboResponseList.add(mapper.map(fbo, FboResponse.class));
			}
		}
		catch (final Exception e)
		{
			LOG.error("populateFboList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return fboResponseList;
	}

	private void setProductivityForFbo(final List<FboResponse> fboFilteredList, final BdeReport bdeReport, final String action)
	{
		if (!CollectionUtils.isEmpty(fboFilteredList) && StringUtils.isNotBlank(action))
		{
			switch (action)
			{
			case "PRODUCTIVE":
				bdeReport.setProductiveList(fboFilteredList);
				break;
			case "PENDING":
				bdeReport.setPendingList(fboFilteredList);
				break;
			case "UNPRODUCTIVE":
				bdeReport.setUnproductiveList(fboFilteredList);
				break;
			case "ONHOLD":
				bdeReport.setOnHoldList(fboFilteredList);
				break;
			}
		}
	}

	private Integer calculateNoOfOrders(final List<List<WeighmentData>> listOfWeighmentDataList, final String status)
	{
		return (int) listOfWeighmentDataList.stream().flatMap(List::stream)
						.filter(data -> status.equalsIgnoreCase(data.getPaymentStatus())).count();
	}

	private List<List<WeighmentData>> populateWeighmentDataList(final List<FboResponse> fboList,
					final ReportSearchRequest searchRequest)
	{
		final List<List<WeighmentData>> listOfWeighmentDataList = new ArrayList<>();
		try
		{
			for (final FboResponse fbo : fboList)
			{
				if (StringUtils.isNotBlank(searchRequest.getInterval()))
				{
					switch (searchRequest.getInterval())
					{
					case MElevenUtil.DAILY:
						listOfWeighmentDataList.add(weightmentDataRepo.findAllByAssignedFBOAndDateRange(fbo.getEnrollmentId(),
										Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
										new Date()));
						break;

					case MElevenUtil.WEEKLY:
						listOfWeighmentDataList.add(weightmentDataRepo.findAllByAssignedFBOAndDateRange(fbo.getEnrollmentId(),
										Date.from(LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
										new Date()));
						break;

					case MElevenUtil.MONTHLY:
						listOfWeighmentDataList
										.add(weightmentDataRepo.findAllByAssignedFBOAndDateRange(fbo.getEnrollmentId(),
														Date.from(LocalDate.now().minusMonths(1)
																		.atStartOfDay(ZoneId.systemDefault()).toInstant()),
														new Date()));
						break;
					}
				}
				else
				{
					listOfWeighmentDataList.add(weightmentDataRepo.findAllByAssignedFBOAndDateRange(fbo.getEnrollmentId(),
									MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getFromDate()),
									MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getToDate())));
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("populateWeighmentDataList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return listOfWeighmentDataList;
	}

	private Double calculateGasEmission(final List<List<WeighmentData>> listOfWeighmentDataList, final String fuelType)
	{
		Double gasEmission = 0.0;
		for (final List<WeighmentData> listOfWData : listOfWeighmentDataList)
		{
			for (final WeighmentData wData : listOfWData)
			{
				final VehicleDetails vehicle = vehicleRepo.findById(wData.getVehicle()).orElse(null);
				if (Objects.nonNull(vehicle) && fuelType.equalsIgnoreCase(vehicle.getFuelType()))
				{
					final DeliveryTracking deliveryTracking = deliveryTrackingRepo.findByPoId(wData.getPurchaseOrderId());
					if (Objects.nonNull(deliveryTracking))
					{
						gasEmission += MElevenUtil.getDoubleValue(deliveryTracking.getGasEmissionOccurred());
					}
				}

			}
		}
		return gasEmission;
	}

	public FboListResponse getRucoDailyFollowupDetailsOfFbo(final Integer empId)
	{
		final FboListResponse fboListResponse = new FboListResponse();

		try
		{
			final List<FoodBusinessOperator> fboList = fboRepo.findByAssignedRUCOAndTodayFollowupDate(empId);

			if (!CollectionUtils.isEmpty(fboList))
			{
				final List<FboResponse> fboResponses = new ArrayList<>();
				for (final FoodBusinessOperator fbo : fboList)
				{
					final FboResponse fboResp = mapper.map(fbo, FboResponse.class);
					setFollowupAction(fboResp, fbo.getEnrollmentId());
					fboResponses.add(fboResp);
				}
				fboListResponse.setFboList(fboResponses);
			}
		}
		catch (final Exception e)
		{
			LOG.error("getRucoDailyFollowupDetailsOfFbo() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return fboListResponse;
	}

	private void setFollowupAction(final FboResponse fboResp, final Integer enrollmentId)
	{
		final FboFollowup fboFollowup = fboFollowupRepo.findSingleByFboIdAndTodayFollowupDate(enrollmentId);
		if (Objects.nonNull(fboFollowup))
		{
			final String formattedCurrentDate = MElevenUtil.dateFormatddMMyyyy.format(new Date());

			if (fboFollowup.getPostponeDate() != null
							&& MElevenUtil.dateFormatddMMyyyy.format(fboFollowup.getPostponeDate()).equals(formattedCurrentDate)
							&& Boolean.TRUE.equals(fboFollowup.isFollowedupOnActualDate()))
			{
				fboResp.setAction("Postponed");
			}
			else if (fboFollowup.getActualFollowupDate() != null && MElevenUtil.dateFormatddMMyyyy
							.format(fboFollowup.getActualFollowupDate()).equals(formattedCurrentDate))
			{
				fboResp.setAction("ActualFollowup");
			}
			else if (fboFollowup.getReadyForPickupDate() != null && MElevenUtil.dateFormatddMMyyyy
							.format(fboFollowup.getReadyForPickupDate()).equals(formattedCurrentDate))
			{
				fboResp.setAction("ReadyForPickup");
			}
		}

	}

	public List<WeighmentDataResponse> getReportAssignedToEmpByDateRange(final Integer id,
					final ReportSearchRequest searchRequest)
	{
		final List<WeighmentDataResponse> orderRespList = new ArrayList<>();
		try
		{
			List<WeighmentData> ordersList = new ArrayList<>();
			if (StringUtils.isNotBlank(searchRequest.getInterval()))
			{
				final Date endDate = new Date();
				switch (searchRequest.getInterval())
				{
				case MElevenUtil.DAILY:
					ordersList = weightmentDataRepo.findAllByEmpIdAndDateRange(id,
									Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									endDate);
					break;

				case MElevenUtil.WEEKLY:
					ordersList = weightmentDataRepo.findAllByEmpIdAndDateRange(id,
									Date.from(LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									endDate);
					break;

				case MElevenUtil.MONTHLY:
					ordersList = weightmentDataRepo.findAllByEmpIdAndDateRange(id,
									Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									endDate);
					break;
				}
			}
			else
			{
				ordersList = weightmentDataRepo.findAllByEmpIdAndDateRange(id,
								MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getFromDate()),
								MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getToDate()));
			}

			for (final WeighmentData order : ordersList)
			{
				orderRespList.add(mapper.map(order, WeighmentDataResponse.class));
			}
		}
		catch (final Exception e)
		{
			LOG.error("getReportAssignedToEmpByDateRange() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return orderRespList;
	}

	public UcoReport generateUcoFboReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		final UcoReport ucoReport = new UcoReport();
		try
		{
			ucoReport.setTotalApprovedFbos(
							populateFboList(empId, ApprovalType.REGISTER, ApprovalStatus.APPROVED, searchRequest).size());
			ucoReport.setTotalPendingFbos(
							populateFboList(empId, ApprovalType.REGISTER, ApprovalStatus.PENDING, searchRequest).size());
			ucoReport.setTotalRejectedFbos(
							populateFboList(empId, ApprovalType.REGISTER, ApprovalStatus.REJECTED, searchRequest).size());
			ucoReport.setTotalOnHoldFbos(
							populateFboList(empId, ApprovalType.REGISTER, ApprovalStatus.ONHOLD, searchRequest).size());
		}
		catch (final Exception e)
		{
			LOG.error("generateUcoFboReport() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return ucoReport;
	}

	public UcoReport generateUcoPaymentReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		final UcoReport ucoReport = new UcoReport();
		try
		{

			final List<List<WeighmentData>> listOfApprovedWeighmentDataList = populateWeighmentDataList(
							populateFboList(empId, ApprovalType.PAYMENT, ApprovalStatus.APPROVED, searchRequest), searchRequest);
			final Integer totalPaymentApprovedCount = calculateNoOfOrders(listOfApprovedWeighmentDataList,
							ApprovalStatus.APPROVED.toString());
			ucoReport.setTotalPaymentApprovedOrders(totalPaymentApprovedCount);

			final List<List<WeighmentData>> listOfPendingWeighmentDataList = populateWeighmentDataList(
							populateFboList(empId, ApprovalType.PAYMENT, ApprovalStatus.PENDING, searchRequest), searchRequest);
			final Integer totalPaymentPendingCount = calculateNoOfOrders(listOfPendingWeighmentDataList,
							ApprovalStatus.PENDING.toString());
			ucoReport.setTotalPaymentPendingOrders(totalPaymentPendingCount);

			final List<List<WeighmentData>> listOfPendingAtRucoWeighmentDataList = populateWeighmentDataList(
							populateFboList(empId, ApprovalType.PAYMENT, ApprovalStatus.PENDING_AT_RUCO, searchRequest),
							searchRequest);
			final Integer totalPaymentPendingAtRucoCount = calculateNoOfOrders(listOfPendingAtRucoWeighmentDataList,
							ApprovalStatus.PENDING_AT_RUCO.toString());
			ucoReport.setTotalPaymentPendingOrdersAtRuco(totalPaymentPendingAtRucoCount);

			ucoReport.setTotalOrders(totalPaymentApprovedCount + totalPaymentPendingCount + totalPaymentPendingAtRucoCount);

			// Total Weight of Oil
			final List<List<WeighmentData>> mergedWeighmentDataList = new ArrayList<>();
			mergedWeighmentDataList.addAll(listOfApprovedWeighmentDataList);
			mergedWeighmentDataList.addAll(listOfPendingWeighmentDataList);
			mergedWeighmentDataList.addAll(listOfPendingAtRucoWeighmentDataList);
			ucoReport.setTotalWeightOfOil(mergedWeighmentDataList.stream().flatMap(List::stream)
							.mapToDouble(wDataResp -> MElevenUtil.getDoubleValue(wDataResp.getWeightCalculatedInKG())).sum());

			// Total Pending Amount
			final List<List<WeighmentData>> mergedPendingWeighmentDataList = new ArrayList<>();
			mergedPendingWeighmentDataList.addAll(listOfPendingWeighmentDataList);
			mergedPendingWeighmentDataList.addAll(listOfPendingAtRucoWeighmentDataList);
			ucoReport.setPendingAmount(mergedPendingWeighmentDataList.stream().flatMap(List::stream)
							.mapToDouble(wDataResp -> MElevenUtil.getDoubleValue(wDataResp.getTotalPrice())).sum());

			// Total Paid Amount
			ucoReport.setPaidAmount(listOfApprovedWeighmentDataList.stream().flatMap(List::stream)
							.mapToDouble(wDataResp -> MElevenUtil.getDoubleValue(wDataResp.getTotalPrice())).sum());

		}
		catch (final Exception e)
		{
			LOG.error("generateUcoPaymentReport() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return ucoReport;
	}

	public UcoReport generateUcoEmissionReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		final UcoReport ucoReport = new UcoReport();
		final List<List<WeighmentData>> mergedWeighmentDataList = new ArrayList<>();
		try
		{
			ucoReport.setGasEmissionByCng(calculateGasEmission(mergedWeighmentDataList, "CNG"));

			ucoReport.setGasEmissionByPetrol(calculateGasEmission(mergedWeighmentDataList, "Petrol"));

			ucoReport.setGasEmissionByDiesel(calculateGasEmission(mergedWeighmentDataList, "Diesel"));
		}
		catch (final Exception e)
		{
			LOG.error("generateUcoEmissionReport() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return ucoReport;
	}

	public List<VehicleResponse> generateEmissionReport(final ReportSearchRequest searchRequest)
	{
		final List<VehicleResponse> vehicleResponseList = new ArrayList<VehicleResponse>();
		final List<VehicleDetails> VehicleList = vehicleRepo.findAll();
		if (!CollectionUtils.isEmpty(VehicleList))
		{
			for (final VehicleDetails vehicleDetails : VehicleList)
			{
				final VehicleResponse vehicleResponse = new VehicleResponse();
				List<VehicleReading> vehicleReadingList = populateEmissionDataList(searchRequest,
								vehicleDetails.getVehicleNumber());
				vehicleResponse.setVehicleNumber(vehicleDetails.getVehicleNumber());
				vehicleResponse.setFuelType(vehicleDetails.getFuelType());
				vehicleResponse.setTotalKM(calculateTotalKm(vehicleReadingList));
				vehicleResponse.setGasEmission(calculateTotalGasEmission(vehicleReadingList));
				vehicleResponseList.add(vehicleResponse);
			}
		}
		return vehicleResponseList;
	}

	private List<VehicleReading> populateEmissionDataList(final ReportSearchRequest searchRequest, final String VehicleNumber)
	{
		try
		{
			if (StringUtils.isNotBlank(searchRequest.getInterval()))
			{
				switch (searchRequest.getInterval())
				{
				case MElevenUtil.DAILY:
					return vehicleReadingsRepo.findByVehicleNumberAndDateRange(
									Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									new Date(), VehicleNumber);
				case MElevenUtil.WEEKLY:
					return vehicleReadingsRepo.findByVehicleNumberAndDateRange(
									Date.from(LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									new Date(), VehicleNumber);
				case MElevenUtil.MONTHLY:
					return vehicleReadingsRepo.findByVehicleNumberAndDateRange(
									Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									new Date(), VehicleNumber);
				}
			}
			else
			{
				return vehicleReadingsRepo.findByVehicleNumberAndDateRange(
								MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getFromDate()),
								MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getToDate()), VehicleNumber);
			}
		}
		catch (final Exception e)
		{
			LOG.error("populateEmissionDataList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return null;
	}

	private Long calculateTotalKm(final List<VehicleReading> vehicleReadingList)
	{
		Long totalkms = 00l;
		for (final VehicleReading vehicleReading : vehicleReadingList)
		{
			totalkms = totalkms + vehicleReading.getTotalKM();
		}
		return totalkms;
	}

	private Double calculateTotalGasEmission(final List<VehicleReading> vehicleReadingList)
	{
		Double totalGasEmission = 0.0;
		for (final VehicleReading vehicleReading : vehicleReadingList)
		{
			totalGasEmission = totalGasEmission + vehicleReading.getGasEmission();
		}
		return totalGasEmission;
	}

}
