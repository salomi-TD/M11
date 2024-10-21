package com.mobile.app.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.app.entity.VehicleDetails;
import com.mobile.app.entity.VehicleReading;
import com.mobile.app.request.VehicleReadingData;
import com.mobile.app.response.VehicleResponse;
import com.mobile.app.service.VehicleService;
import com.mobile.app.util.MElevenUtil;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/vehicle")
public class VehicleController
{

	private static final Logger LOG = LoggerFactory.getLogger(VehicleController.class);

	@Autowired
	private VehicleService vehicleService;

	@PostMapping("/add")
	public String addVehicleDetails(@RequestBody final VehicleResponse vehicleData)
	{
		return vehicleService.addVehicleDetails(vehicleData);
	}

	@PostMapping("/update")
	public String updateVehicleDetails(@RequestBody final VehicleResponse vehicleData)
	{
		try
		{
			vehicleService.updateVehicleDetails(vehicleData);
		}
		catch (final Exception e)
		{
			LOG.error("updateVehicleDetails() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return "Vehicle data updated successfully";
	}

	@PostMapping("/update-multiple")
	public String updateMultipleVehicleDetails(@RequestBody final List<VehicleResponse> vehicleList)
	{
		return vehicleService.updateMultipleVehicleDetails(vehicleList);
	}

	@DeleteMapping("/remove/{vehicleId}")
	public String removeVehicle(@PathVariable("vehicleId") final Integer vehicleId)
	{
		try
		{
			vehicleService.removeVehicle(vehicleId);
		}
		catch (final Exception e)
		{
			LOG.error("removeVehicle() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return "Vehicle data removed successfully";
	}

	@DeleteMapping("/remove-multiple")
	public String removeMultipleVehicles(@RequestBody final List<Integer> vehicleIds)
	{
		return vehicleService.removeMultipleVehicles(vehicleIds);
	}

	@GetMapping("/{vehicleId}")
	public VehicleResponse getVehicleDetailsById(@PathVariable final Integer vehicleId)
	{
		return vehicleService.getVehicleDetailsById(vehicleId);
	}

	@GetMapping("/list")
	public List<VehicleResponse> getVehiclesListByRegion(@RequestParam final String regionId)
	{
		return vehicleService.getVehicleListByRegion(regionId);
	}

	@GetMapping("/get-list")
	public List<VehicleDetails> getVehiclesList()
	{
		return vehicleService.getVehicleList();
	}

	@PostMapping("/reading")
	public VehicleReadingData addVehicleReadings(@RequestBody final VehicleReadingData vehicleReadingRequest)
	{
		return vehicleService.addVehicleReadings(vehicleReadingRequest);
	}

	@GetMapping("/get-yesterday-readings")
	public List<VehicleReading> getYesterdayVehicleReadings()
	{
		return vehicleService.getYesterdayVehicleReadings();
	}

	@GetMapping("/get-readings")
	public List<VehicleReading> getAllVehicleReadings()
	{
		return vehicleService.getAllVehicleReadings();
	}
}
