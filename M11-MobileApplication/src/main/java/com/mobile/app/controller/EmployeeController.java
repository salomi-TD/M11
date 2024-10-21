package com.mobile.app.controller;

import java.util.List;

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

import com.mobile.app.entity.BdeMonthlyTarget;
import com.mobile.app.entity.Employee;
import com.mobile.app.entity.EmployeeAttendance;
import com.mobile.app.response.BdeEnquiryData;
import com.mobile.app.response.BdeTargetData;
import com.mobile.app.response.EmployeeAttendanceReport;
import com.mobile.app.response.EmployeeListResponse;
import com.mobile.app.response.RegionalData;
import com.mobile.app.response.EmployeeResponse;
import com.mobile.app.response.RoleBasedEmpResponse;
import com.mobile.app.service.EmployeeService;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/employee")
public class EmployeeController
{

	@Autowired
	private EmployeeService employeeService;

	@GetMapping("/{empId}")
	public EmployeeResponse getEmployeeById(@PathVariable("empId") final Integer empId)
	{
		return employeeService.getEmployeeById(empId);
	}

	@PostMapping("/create")
	public String createEmployee(@RequestBody final Employee employee)
	{
		return employeeService.createEmployee(employee);
	}

	@PostMapping("/update")
	public String updateEmployeeDetails(@RequestBody final Employee employee)
	{
		return employeeService.updateEmployeeDetails(employee);
	}

	@DeleteMapping("/remove/{empId}")
	public String removeEmployee(@PathVariable("empId") final Integer empId)
	{
		return employeeService.removeEmployee(empId);
	}

	@GetMapping("/list")
	public EmployeeListResponse getEmployeeList()
	{
		return employeeService.getEmployeeList();
	}

	@GetMapping("/list/{role}")
	public EmployeeListResponse getEmployeeListByRole(@PathVariable("role") final String role,
					@RequestParam final String regionId)
	{
		return employeeService.getEmployeeListByRoleAndRegion(role, regionId);
	}

	@GetMapping("/regionalList/{empId}")
	public RegionalData getAvailableRegionalEmployeeList(@PathVariable("empId") final Integer empId)
	{
		return employeeService.getAvailableRegionalDataForNextPickup(empId);
	}

	@GetMapping("/ucolist")
	public List<RoleBasedEmpResponse> getUcoList()
	{
		return employeeService.getUcoList();
	}

	@GetMapping("/rucolist")
	public List<RoleBasedEmpResponse> getRucoList()
	{
		return employeeService.getRucoList();
	}

	@PostMapping("/attendance/{empId}")
	public EmployeeAttendance markAttendance(@PathVariable final Integer empId,
					@RequestParam(value = "present", required = false) final Boolean present)
	{
		return employeeService.markAttendance(present, empId);
	}

	@GetMapping("/attendance-list/{empId}")
	public List<EmployeeAttendanceReport> getEmployeeAttendanceList(@PathVariable final Integer empId,
					@RequestParam final String monthYear)
	{
		return employeeService.getEmployeeAttendanceList(empId, monthYear);
	}

	@GetMapping("/attendance-day-wise/{empId}")
	public List<EmployeeAttendanceReport> getEmployeeAttendanceDayWise(@PathVariable final Integer empId)
	{
		return employeeService.getEmployeeAttendanceDayWise(empId);
	}

	@PostMapping("/bde-target")
	public String createBdeTarget(@RequestBody final List<BdeTargetData> bdeTargetList)
	{
		return employeeService.createBdeTarget(bdeTargetList);
	}

	@GetMapping("/bde-target/{empId}")
	public List<BdeMonthlyTarget> getBdeTargetList(@PathVariable final Integer empId, @RequestParam final String MMyyyy)
	{
		return employeeService.getBdeTargetList(empId, MMyyyy);
	}

	@PostMapping("/bde/{empId}")
	public BdeEnquiryData createBdeEnquiryTracking(@PathVariable("empId") final Integer empId,
					@RequestBody(required = false) final BdeEnquiryData bdeEnquiryData)
	{
		return employeeService.createBdeEnquiryTracking(empId, bdeEnquiryData);
	}

}
