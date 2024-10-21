package com.mobile.app.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mobile.app.entity.VehicleDetails;
import com.mobile.app.entity.VehicleReading;
import com.mobile.app.repository.VehicleReadingRepo;
import com.mobile.app.repository.VehicleRepo;
import com.mobile.app.request.VehicleReadingData;
import com.mobile.app.response.VehicleResponse;
import com.mobile.app.util.MElevenUtil;

import io.micrometer.common.util.StringUtils;

@Service
public class VehicleService
{

	private static final Logger LOG = LoggerFactory.getLogger(VehicleService.class);

	private static final String VEHICLE_NUMBER = "Vehicle Number: ";

	@Autowired
	private OrderService orderService;

	@Autowired
	private VehicleRepo vehicleRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private VehicleReadingRepo vehicleReadingsRepo;

	public String addVehicleDetails(final VehicleResponse vehicleData)
	{
		try
		{
			if (Objects.isNull(vehicleRepo.findByVehicleNumber(vehicleData.getVehicleNumber())))
			{
				final VehicleDetails vehicle = new VehicleDetails();
				vehicle.setVehicleNumber(vehicleData.getVehicleNumber());
				vehicle.setVehicleType(vehicleData.getVehicleType());
				vehicle.setFuelType(vehicleData.getFuelType());
				vehicle.setRegion(vehicleData.getRegion());
				vehicle.setAvailable(true);
				vehicleRepo.save(vehicle);
				return "Successfully added the Vehicle with Vehicle Number: " + vehicle.getVehicleNumber();
			}
		}
		catch (final Exception e)
		{
			LOG.error("addVehicleDetails() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return VEHICLE_NUMBER + vehicleData.getVehicleNumber().trim() + " already exists.";
	}

	public void updateVehicleDetails(final VehicleResponse updatedVehicleData)
	{
		try
		{
			final VehicleDetails vehicle = vehicleRepo.findById(updatedVehicleData.getVehicleId()).orElse(null);

			if (Objects.nonNull(vehicle))
			{
				vehicle.setVehicleNumber(updatedVehicleData.getVehicleNumber());
				vehicle.setVehicleType(updatedVehicleData.getVehicleType());
				vehicle.setFuelType(updatedVehicleData.getFuelType());
				vehicle.setRegion(updatedVehicleData.getRegion());
				vehicle.setAvailable(updatedVehicleData.isAvailable());
				vehicleRepo.save(vehicle);
			}
		}
		catch (final Exception e)
		{
			LOG.error("updateVehicleDetails() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
	}

	public String updateMultipleVehicleDetails(final List<VehicleResponse> vehicleList)
	{
		try
		{
			if (!CollectionUtils.isEmpty(vehicleList))
			{
				for (final VehicleResponse updatedVehicleData : vehicleList)
				{
					updateVehicleDetails(updatedVehicleData);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("updateMultipleVehicleDetails() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return "Vehicle data updated successfully";
	}

	public void removeVehicle(final Integer vehicleId)
	{
		try
		{
			final VehicleDetails vehicle = vehicleRepo.findById(vehicleId).orElse(null);

			if (Objects.nonNull(vehicle))
			{
				vehicleRepo.deleteById(vehicleId);
			}
		}
		catch (final Exception e)
		{
			LOG.error("removeVehicle() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
	}

	public String removeMultipleVehicles(final List<Integer> vehicleIds)
	{
		try
		{
			if (!CollectionUtils.isEmpty(vehicleIds))
			{
				for (final Integer vehicleId : vehicleIds)
				{
					removeVehicle(vehicleId);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("removeMultipleVehicles() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return "Vehicle data removed successfully";
	}

	public VehicleResponse getVehicleDetailsById(final Integer vehicleId)
	{
		final VehicleResponse vehicleResponse = new VehicleResponse();
		try
		{
			final VehicleDetails vehicle = vehicleRepo.findById(vehicleId).orElse(null);
			if (Objects.nonNull(vehicle))
			{
				return mapper.map(vehicle, VehicleResponse.class);
			}
		}
		catch (final Exception e)
		{
			LOG.error("getVehicleDetailsById() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}

		return vehicleResponse;
	}

	public List<VehicleResponse> getVehicleListByRegion(final String regionId)
	{
		final List<VehicleResponse> vehicleListResponse = new ArrayList<>();
		try
		{
			final List<VehicleDetails> vehicleList = vehicleRepo.findByRegion(regionId);
			if (!CollectionUtils.isEmpty(vehicleList))
			{
				for (final VehicleDetails vehicleDetails : vehicleList)
				{
					vehicleListResponse.add(mapper.map(vehicleDetails, VehicleResponse.class));
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getVehicleListByRegion() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return vehicleListResponse;

	}

	public VehicleReadingData addVehicleReadings(final VehicleReadingData vehicleReadingData)
	{
		try
		{
			final VehicleReading existingVehicleReading = vehicleReadingsRepo
							.findByVehicleNumberAndCurrentDate(vehicleReadingData.getVehicleNumber());
			if (Objects.isNull(existingVehicleReading) && StringUtils.isBlank(vehicleReadingData.getTypeOfReading()))
			{
				return vehicleReadingData;
			}
			else if (Objects.isNull(existingVehicleReading)
							&& "initialReading".equalsIgnoreCase(vehicleReadingData.getTypeOfReading()))
			{
				final VehicleReading vehicleReading = new VehicleReading();
				vehicleReading.setVehicleNumber(vehicleReadingData.getVehicleNumber());
				vehicleReading.setInitialReading(vehicleReadingData.getInitialReading());
				vehicleReading.setDate(new Date(System.currentTimeMillis()));
				vehicleReadingsRepo.save(vehicleReading);
				vehicleReadingData.setMessage("initial reading record submitted successfully");
			}
			else if (Objects.nonNull(existingVehicleReading) && StringUtils.isBlank(vehicleReadingData.getTypeOfReading())
							&& existingVehicleReading.getFinalReading() == null)
			{
				vehicleReadingData.setInitialReading(existingVehicleReading.getInitialReading());
				vehicleReadingData.setMessage("setFinalReading");
			}
			else if (Objects.nonNull(existingVehicleReading)
							&& "finalReading".equalsIgnoreCase(vehicleReadingData.getTypeOfReading())
							&& existingVehicleReading.getFinalReading() == null)
			{
				existingVehicleReading.setFinalReading(vehicleReadingData.getFinalReading());
				final Long totalKm = calcualteKm(existingVehicleReading.getInitialReading(),
								vehicleReadingData.getFinalReading());
				existingVehicleReading.setTotalKM(totalKm);
				final VehicleDetails vehicle = vehicleRepo.findByVehicleNumber(vehicleReadingData.getVehicleNumber());
				Double d = (double) totalKm;
				existingVehicleReading.setGasEmission(orderService.calculateGasEmission(d, vehicle.getFuelType()));
				vehicleReadingsRepo.save(existingVehicleReading);
				vehicleReadingData.setInitialReading(existingVehicleReading.getInitialReading());
				vehicleReadingData.setMessage("final  readinig recod submitted successefully and the Distance travled is "
								+ existingVehicleReading.getTotalKM());

			}
			else if (existingVehicleReading.getInitialReading() != null && existingVehicleReading.getFinalReading() != null)
			{
				vehicleReadingData.setInitialReading(existingVehicleReading.getInitialReading());
				vehicleReadingData.setFinalReading(existingVehicleReading.getFinalReading());
				vehicleReadingData.setMessage("final reading already submitted");
			}
		}
		catch (final Exception e)
		{
			LOG.error("addVehicleReadings() - Exception occurred. Message: [{}]", e.getMessage(), e);
			vehicleReadingData.setMessage(MElevenUtil.SOMETHING_WENT_WRONG);
		}
		return vehicleReadingData;
	}

	private long calcualteKm(final Long intialReadings, final Long finalReadings)
	{
		return finalReadings - intialReadings;

	}

	public List<VehicleReading> getYesterdayVehicleReadings()
	{
		return vehicleReadingsRepo.findByYesterdayDate();
	}

	public List<VehicleReading> getAllVehicleReadings()
	{
		return vehicleReadingsRepo.findAll();
	}

	public List<VehicleDetails> getVehicleList()
	{
		return vehicleRepo.findAll();
	}

}
