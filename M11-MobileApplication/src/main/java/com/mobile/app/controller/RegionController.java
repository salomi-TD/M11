package com.mobile.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.app.response.RegionResponse;
import com.mobile.app.response.RegionalData;
import com.mobile.app.service.RegionService;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/region")
public class RegionController
{

	@Autowired
	private RegionService regionService;

	@GetMapping("/{empId}")
	public RegionResponse getRegionByEmpId(@PathVariable final Integer empId)
	{
		return regionService.getRegionByEmpId(empId);
	}

	@GetMapping("/list")
	public List<RegionResponse> getRegionList()
	{
		return regionService.getRegionList();
	}

	@GetMapping("/data/{regionId}")
	public RegionalData getRegionalData(@PathVariable("regionId") final String regionId)
	{
		return regionService.getRegionalDataByRegion(regionId);
	}

}
