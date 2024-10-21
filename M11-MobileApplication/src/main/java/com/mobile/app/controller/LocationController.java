package com.mobile.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.app.entity.Location;
import com.mobile.app.service.LocationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/locations")
public class LocationController
{

	@Autowired
	private LocationService locationService;

	@GetMapping
	public String updateLocation()
	{
		return "location";
	}

	@PostMapping
	public ResponseEntity<Integer> createLocation(@RequestBody final Location location)
	{
		final Integer locationId = locationService.createLocation(location);
		return new ResponseEntity<>(locationId, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<String> updateLocation(@RequestBody final Location location)
	{
		locationService.updateLocation(location);
		return new ResponseEntity<>("Location updated successfully", HttpStatus.OK);
	}

	@GetMapping("/{empId}")
	public ResponseEntity<Location> getLocation(@PathVariable final Integer empId)
	{

		return new ResponseEntity<>(locationService.getLocation(empId), HttpStatus.OK);
	}

	@PostMapping("/store")
	@ResponseBody
	public ResponseEntity<String> storeLocation(final HttpServletRequest request, @RequestBody final Location location)
	{
		// Store the Location object in the HTTP session using locationId as the key
		final HttpSession session = request.getSession();
		session.setAttribute("location_" + location.getEmpId(), location);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/get/{empId}")
	@ResponseBody
	public String getLocation(final HttpServletRequest request, @PathVariable("empId") final int empId)
	{
		// Retrieve the Location object from the HTTP session using locationId as the key
		final HttpSession session = request.getSession();
		final Location location = (Location) session.getAttribute("location_" + empId);

		if (location != null)
		{
			return "Location with ID " + empId + " in session: " + location.toString();
		}
		else
		{
			return "Location with ID " + empId + " not found in session.";
		}
	}

}
