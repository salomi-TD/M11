package com.mobile.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.app.service.MapService;

@RestController
@RequestMapping("/api/distance-matrix")
@CrossOrigin
public class MapController
{

	@Autowired
	private MapService mapService;

	@GetMapping("/loc")
	public String getLocation(final Model model)
	{
		return "index1";
	}

	@GetMapping("/coordinates/{empId}")
	public Map<String, Object> getCoordinates(@PathVariable final Integer empId)
	{
		return mapService.getAddressCoordinates(empId);
	}

}
