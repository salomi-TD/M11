package com.mobile.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.app.entity.FoodBusinessOperator;
import com.mobile.app.request.FboAccountRequest;
import com.mobile.app.response.FboListResponse;
import com.mobile.app.response.FboRegisterForm;
import com.mobile.app.response.FboResponse;
import com.mobile.app.service.FboService;
import com.mobile.app.response.FboAccountResponse;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/fbo")
public class FboController
{

	@Autowired
	private FboService fboService;

	@PostMapping("/create")
	public String createFbo(@RequestBody final FboRegisterForm fboForm)
	{
		return fboService.createFbo(fboForm.getFbo(), fboForm.getImage());
	}

	@GetMapping("/{enrollmentId}")
	public FboResponse getFboDetails(@PathVariable("enrollmentId") final Integer enrollmentId)
	{

		return fboService.getFboDetails(enrollmentId);
	}

	@PostMapping("/update")
	public String updateFboDetails(@RequestBody final FoodBusinessOperator fbo)
	{

		return fboService.updateFboDetails(fbo);
	}

	@DeleteMapping("/remove/{enrollmentId}")
	public String removeEmployee(@PathVariable("enrollmentId") final Integer enrollmentId)
	{
		return fboService.removeFbo(enrollmentId);
	}

	@GetMapping("/list")
	public FboListResponse getFboList()
	{
		return fboService.getFboList();
	}

	@GetMapping("/list/{empId}")
	public FboListResponse getFboListAssociatedWithEmployee(@PathVariable("empId") final Integer empId)
	{
		return fboService.getFboListAssociatedWithEmployee(empId);
	}

	@GetMapping("/accountcheck/{enrollmentId}")
	public FboAccountResponse getFboAccountDetails(@PathVariable("enrollmentId") final Integer enrollmentId)
	{

		return fboService.getFboAccountDetails(enrollmentId);
	}

	@PostMapping("/account/save")
	public String saveAccountFbo(@RequestBody final FboAccountRequest accountRequest)
	{
		return fboService.saveFboAccountDetails(accountRequest);
	}

}