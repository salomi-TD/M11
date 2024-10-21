package com.mobile.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mobile.app.entity.Employee;
import com.mobile.app.entity.Regions;
import com.mobile.app.enums.EmployeeRole;
import com.mobile.app.repository.EmployeeRepo;
import com.mobile.app.repository.RegionRepo;
import com.mobile.app.repository.VehicleRepo;
import com.mobile.app.response.RegionResponse;
import com.mobile.app.response.RegionalData;

@Service
public class RegionService
{

	private static final Logger LOG = LoggerFactory.getLogger(RegionService.class);

	@Autowired
	private RegionRepo regionRepo;

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private VehicleRepo vehicleRepo;

	@Autowired
	private ModelMapper mapper;

	public RegionResponse getRegionByEmpId(final Integer empId)
	{
		try
		{
			final Regions region = regionRepo.findRegionByEmployeeId(empId);
			return mapper.map(region, RegionResponse.class);
		}
		catch (final Exception e)
		{
			LOG.error("getRegionByEmpId() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return null;
	}

	public List<RegionResponse> getRegionList()
	{
		final List<RegionResponse> regionList = new ArrayList<>();
		try
		{
			final List<Regions> regions = regionRepo.findAll();
			if (!CollectionUtils.isEmpty(regions))
			{
				for (final Regions region : regions)
				{
					regionList.add(mapper.map(region, RegionResponse.class));
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getRegionList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return regionList;
	}

	public RegionalData getRegionalDataByRegion(final String regionId)
	{
		final RegionalData employeeData = new RegionalData();
		try
		{
			final List<Employee> regionalEmployeeList = employeeRepo.findByRegion(regionId);

			if (!CollectionUtils.isEmpty(regionalEmployeeList))
			{
				employeeData.setUcoList(regionalEmployeeList.stream()
								.filter(employee -> employee.getRole().equalsIgnoreCase(EmployeeRole.UCO.toString()))
								.collect(Collectors.toList()));

				employeeData.setRucoList(regionalEmployeeList.stream()
								.filter(employee -> employee.getRole().equalsIgnoreCase(EmployeeRole.RUCO.toString()))
								.collect(Collectors.toList()));

				employeeData.setCollectionExecutives(regionalEmployeeList.stream()
								.filter(employee -> employee.getRole().equalsIgnoreCase(EmployeeRole.CE.toString()))
								.collect(Collectors.toList()));

				employeeData.setDrivers(regionalEmployeeList.stream()
								.filter(employee -> employee.getRole().equalsIgnoreCase(EmployeeRole.DRIVER.toString()))
								.collect(Collectors.toList()));
			}
			employeeData.setVehicle(vehicleRepo.findByRegion(regionId));
		}
		catch (final Exception e)
		{
			LOG.error("getRegionalDataByRegion() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return employeeData;
	}

}
