package com.mobile.app.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mobile.app.entity.BdeEnquiryTracking;
import com.mobile.app.entity.BdeMonthlyTarget;
import com.mobile.app.entity.Employee;
import com.mobile.app.entity.EmployeeAttendance;
import com.mobile.app.entity.VehicleDetails;
import com.mobile.app.enums.EmployeeRole;
import com.mobile.app.repository.BdeEnquiryTrackingRepo;
import com.mobile.app.repository.BdeMonthlyTargetRepo;
import com.mobile.app.repository.EmployeeAttendanceRepo;
import com.mobile.app.repository.EmployeeRepo;
import com.mobile.app.repository.VehicleRepo;
import com.mobile.app.response.BdeEnquiryData;
import com.mobile.app.response.BdeTargetData;
import com.mobile.app.response.EmployeeAttendanceReport;
import com.mobile.app.response.EmployeeListResponse;
import com.mobile.app.response.EmployeeResponse;
import com.mobile.app.response.RegionalData;
import com.mobile.app.response.RoleBasedEmpResponse;
import com.mobile.app.util.MElevenUtil;

@Service
public class EmployeeService
{

	private static final Logger LOG = LoggerFactory.getLogger(EmployeeService.class);

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private VehicleRepo vehicleRepo;

	@Autowired
	private EmployeeAttendanceRepo empAttendanceRepo;

	@Autowired
	private BdeMonthlyTargetRepo bdeTargetRepo;

	@Autowired
	private BdeEnquiryTrackingRepo bdeEnquiryTrackingRepo;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private MapService mapService;

	public EmployeeResponse getEmployeeById(final Integer empId)
	{
		try
		{
			final Employee existingEmployee = employeeRepo.findById(empId).orElse(null);

			if (Objects.nonNull(existingEmployee))
			{
				return mapper.map(existingEmployee, EmployeeResponse.class);
			}
		}
		catch (final Exception e)
		{
			LOG.error("getEmployeeById() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return null;
	}

	public String updateEmployeeDetails(final Employee employee)
	{
		try
		{
			final Employee existingEmployee = employeeRepo.findById(employee.getEmpId()).orElse(null);

			if (Objects.nonNull(existingEmployee))
			{
				existingEmployee.setFullname(employee.getFullname());
				existingEmployee.setEmail(employee.getEmail());
				existingEmployee.setContact(employee.getContact());
				existingEmployee.setRegion(employee.getRegion());
				existingEmployee.setRole(employee.getRole());

				employeeRepo.save(existingEmployee);

				return "Updated Successfully";
			}
		}
		catch (final Exception e)
		{
			LOG.error("updateEmployeeDetails() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return "Employee not found";
	}

	public String removeEmployee(final Integer empId)
	{
		try
		{
			final Employee existingEmployee = employeeRepo.findById(empId).orElse(null);

			if (Objects.nonNull(existingEmployee))
			{
				employeeRepo.delete(existingEmployee);
				return "Successfully Removed";
			}
		}
		catch (final Exception e)
		{
			LOG.error("removeEmployee() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return "Employee record not found";
	}

	public String createEmployee(final Employee employee)
	{
		try
		{
			if (Objects.nonNull(employeeRepo.findByContact(employee.getContact())))
			{
				return "Contact number is already registered, try different one";
			}

			final String empPass = cryptoService.encryptData(employee.getPassword());
			employee.setPassword(empPass);

			employeeRepo.save(employee);

			final Employee createdEmployee = employeeRepo.findByContact(employee.getContact());

			if (Objects.nonNull(createdEmployee))
			{
				return "Successfully registered with id = " + createdEmployee.getEmpId();
			}
		}
		catch (final Exception e)
		{
			LOG.error("createEmployee() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return "Error while saving into database";
	}

	public EmployeeListResponse getEmployeeList()
	{
		final EmployeeListResponse employeesResponse = new EmployeeListResponse();
		try
		{
			final List<Employee> employeeList = employeeRepo.findAll();

			if (!CollectionUtils.isEmpty(employeeList))
			{
				final List<EmployeeResponse> employeeResponseList = new ArrayList<>();

				for (final Employee employee : employeeList)
				{
					employeeResponseList.add(mapper.map(employee, EmployeeResponse.class));
				}
				employeesResponse.setEmployees(employeeResponseList);
			}
		}
		catch (final Exception e)
		{
			LOG.error("getEmployeeList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return employeesResponse;
	}

	public EmployeeListResponse getEmployeeListByRoleAndRegion(final String role, final String regionId)
	{
		final EmployeeListResponse employeeData = new EmployeeListResponse();
		try
		{
			final List<Employee> employees = employeeRepo.findByRoleAndRegion(role, regionId);

			if (!CollectionUtils.isEmpty(employees))
			{
				final List<EmployeeResponse> employeeList = new ArrayList<>();

				for (final Employee employee : employees)
				{
					employeeList.add(mapper.map(employee, EmployeeResponse.class));
				}
				employeeData.setEmployees(employeeList);
			}
		}
		catch (final Exception e)
		{
			LOG.error("getEmployeeListByRole() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return employeeData;
	}

	public RegionalData getAvailableRegionalDataForNextPickup(final Integer empId)
	{
		final RegionalData regionalData = new RegionalData();
		try
		{
			final Employee employee = employeeRepo.findById(empId).orElse(null);
			if (Objects.nonNull(employee))
			{
				final String regionId = employee.getRegion();
				final List<Employee> regionalCeList = employeeRepo.findByRoleAndRegionAndIsPresent(EmployeeRole.CE.toString(),
								regionId);
				if (!CollectionUtils.isEmpty(regionalCeList))
				{
					regionalCeList.removeAll(employeeRepo.findCurrentlyAssignedCesByRegion(regionId));
					regionalData.setCollectionExecutives(regionalCeList);
				}

				final List<Employee> regionalDriverList = employeeRepo
								.findByRoleAndRegionAndIsPresent(EmployeeRole.DRIVER.toString(), regionId);
				if (!CollectionUtils.isEmpty(regionalDriverList))
				{
					regionalDriverList.removeAll(employeeRepo.findCurrentlyAssignedDriversByRegion(regionId));
					regionalData.setDrivers(regionalDriverList);
				}

				final List<VehicleDetails> allAvailableVehicles = vehicleRepo.findByRegionAndAvailable(regionId, true);
				if (!CollectionUtils.isEmpty(allAvailableVehicles))
				{
					allAvailableVehicles.removeAll(vehicleRepo.findCurrentlyAssignedVehiclesByRegion(regionId));
					regionalData.setVehicle(allAvailableVehicles);
				}

				final List<String> allAvailableAddress = mapService.getFboAddressByRegion(empId);
				if (!CollectionUtils.isEmpty(allAvailableAddress))
				{
					regionalData.setStartAddress(allAvailableAddress);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getAvailableCeAndDriverDataForNextPickupByRegion() - Exception occurred. Message: [{}]", e.getMessage(),
							e);
		}
		return regionalData;
	}

	public List<RoleBasedEmpResponse> getUcoList()
	{
		final List<RoleBasedEmpResponse> ucoListResp = new ArrayList<>();
		try
		{
			final List<Employee> ucoList = employeeRepo.findByRole(EmployeeRole.UCO.toString());
			if (!CollectionUtils.isEmpty(ucoList))
			{
				for (final Employee uco : ucoList)
				{
					final RoleBasedEmpResponse response = new RoleBasedEmpResponse();
					response.setEmpId(String.valueOf(uco.getEmpId()));
					response.setFullname(uco.getFullname());
					ucoListResp.add(response);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getUcoList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return ucoListResp;
	}

	public List<RoleBasedEmpResponse> getRucoList()
	{
		final List<RoleBasedEmpResponse> rucoListResp = new ArrayList<>();
		try
		{
			final List<Employee> rucoList = employeeRepo.findByRole(EmployeeRole.RUCO.toString());
			if (!CollectionUtils.isEmpty(rucoList))
			{
				for (final Employee ruco : rucoList)
				{
					final RoleBasedEmpResponse response = new RoleBasedEmpResponse();
					response.setEmpId(String.valueOf(ruco.getEmpId()));
					response.setFullname(ruco.getFullname());
					rucoListResp.add(response);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getRucoList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return rucoListResp;
	}

	public EmployeeAttendance markAttendance(final Boolean present, final Integer empId)
	{
		try
		{
			EmployeeAttendance attendance = empAttendanceRepo.findByEmpIdAndDate(empId);

			if (Objects.nonNull(attendance))
			{
				if (Objects.isNull(present) && attendance.getPresent().equals(true))
				{
					attendance.setMessage("Present");
					return attendance;
				}
				else if ((Objects.isNull(present) || present.equals(false)) && attendance.getPresent().equals(false))
				{
					attendance.setMessage("OnLeave");
				}
				else if (present.equals(false) && attendance.getPresent().equals(true))
				{
					attendance.setPresent(present);
					empAttendanceRepo.save(attendance);
					attendance.setMessage("Leave applied for today");
				}
				return attendance;
			}
		}
		catch (final Exception e)
		{
			LOG.error("markAttendance() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return null;
	}

	public List<EmployeeAttendanceReport> getEmployeeAttendanceList(final Integer empId, final String MMyyyy)
	{
		final List<EmployeeAttendanceReport> attendanceReportList = new ArrayList<>();
		try
		{
			final Employee employeeData = employeeRepo.findById(empId).orElse(null);
			if (Objects.nonNull(employeeData))
			{
				final List<Employee> employeesList = employeeRepo.findByRegion(employeeData.getRegion());
				for (final Employee employee : employeesList)
				{
					final EmployeeAttendanceReport attendanceData = new EmployeeAttendanceReport();
					attendanceData.setEmpData(mapper.map(employee, EmployeeResponse.class));

					final Calendar calendar = Calendar.getInstance();
					int totalNoOfDays;
					int noOfDaysPresent;
					final Date selectedMonthYear = MElevenUtil.dateFormatMMyyyy.parse(MMyyyy);
					calendar.setTime(selectedMonthYear);
					totalNoOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					noOfDaysPresent = empAttendanceRepo.findAllByEmpIdAndMonthYear(employee.getEmpId(), selectedMonthYear).size();
					attendanceData.setNoOfDaysPresent(noOfDaysPresent);
					attendanceData.setNoOfDaysAbsent(totalNoOfDays - noOfDaysPresent);
					attendanceData.setTotalNoOfDays(totalNoOfDays);
					attendanceReportList.add(attendanceData);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getEmployeeAttendanceList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return attendanceReportList;
	}

	public List<EmployeeAttendanceReport> getEmployeeAttendanceDayWise(final Integer empId)
	{
		final List<EmployeeAttendanceReport> attendanceReportList = new ArrayList<>();
		try
		{
			final Employee employeeData = employeeRepo.findById(empId).orElse(null);
			if (Objects.nonNull(employeeData))
			{
				final List<Employee> employeesList = employeeRepo.findByRegion(employeeData.getRegion());
				for (final Employee employee : employeesList)
				{
					final EmployeeAttendanceReport attendanceData = new EmployeeAttendanceReport();
					attendanceData.setEmpData(mapper.map(employee, EmployeeResponse.class));
					EmployeeAttendance employeeAttendance = empAttendanceRepo.findByEmpIdAndDate(employee.getEmpId());
					if (Objects.nonNull(employeeAttendance))
					{
						attendanceData.setPresent(employeeAttendance.getPresent());
					}
					attendanceReportList.add(attendanceData);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getEmployeeAttendanceDayWise() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return attendanceReportList;
	}

	public String createBdeTarget(final List<BdeTargetData> bdeTargetList)
	{
		try
		{
			if (!CollectionUtils.isEmpty(bdeTargetList))
			{
				for (final BdeTargetData bdeTargetData : bdeTargetList)
				{
					final String targetId = targetIdGenerator(bdeTargetData.getEmpId());
					BdeMonthlyTarget bdeMonthlyTargetData = bdeTargetRepo.findByTargetId(targetId);
					if (Objects.isNull(bdeMonthlyTargetData))
					{
						bdeMonthlyTargetData = new BdeMonthlyTarget();
						bdeMonthlyTargetData.setTargetId(targetId);
						bdeMonthlyTargetData.setBde(bdeTargetData.getEmpId());
						bdeMonthlyTargetData.setTargetToBeVisited(bdeTargetData.getTargetToBeVisited());
					}
					else
					{
						bdeMonthlyTargetData.setTargetToBeVisited(bdeTargetData.getTargetToBeVisited());
					}
					bdeTargetRepo.save(bdeMonthlyTargetData);
				}
				return "Target(s) Set Successfully.";
			}
		}
		catch (final Exception e)
		{
			LOG.error("createBdeTarget() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return MElevenUtil.SOMETHING_WENT_WRONG;
	}

	public BdeEnquiryData createBdeEnquiryTracking(final Integer empId, final BdeEnquiryData bdeEnquiryData)
	{
		final BdeEnquiryData updatedBdeEnquiryData = new BdeEnquiryData();
		try
		{
			final BdeMonthlyTarget bdeMonthlyTargetData = bdeTargetRepo.findByTargetId(targetIdGenerator(empId));

			if (Objects.nonNull(bdeEnquiryData))
			{
				final BdeEnquiryTracking bdeEnquiryTracking = bdeEnquiryData.getBdeEnquiryTracking();
				if (Objects.nonNull(bdeEnquiryTracking))
				{
					bdeEnquiryTracking.setLastVisitDate(new Date());
					bdeEnquiryTrackingRepo.save(bdeEnquiryTracking);
					updatedBdeEnquiryData.setBdeEnquiryTracking(bdeEnquiryTracking);
				}

				final BdeMonthlyTarget bdeMonthlyTarget = bdeEnquiryData.getBdeMonthlyTarget();
				if (Objects.nonNull(bdeMonthlyTarget))
				{
					final Integer targetToBeVisited = bdeMonthlyTargetData.getTargetToBeVisited();
					final Integer actualVisited = bdeMonthlyTarget.getActualVisited();
					bdeMonthlyTargetData.setActualVisited(actualVisited);
					if (targetToBeVisited >= actualVisited)
					{
						bdeMonthlyTargetData.setBalanceToVisit(targetToBeVisited - actualVisited);
					}
					bdeTargetRepo.save(bdeMonthlyTargetData);
					updatedBdeEnquiryData.setBdeMonthlyTarget(bdeMonthlyTargetData);
				}
			}
			else
			{
				updatedBdeEnquiryData.setBdeMonthlyTarget(bdeMonthlyTargetData);
			}
		}
		catch (final Exception e)
		{
			LOG.error("createBdeEnquiryTracking() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return updatedBdeEnquiryData;
	}

	private String targetIdGenerator(final Integer empId)
	{
		final LocalDate date = LocalDate.now();
		return empId.toString() + date.getMonthValue() + date.getYear();
	}

	public List<BdeMonthlyTarget> getBdeTargetList(final Integer empId, final String MMyyyy)
	{
		final List<BdeMonthlyTarget> monthlyTargetList = new ArrayList<>();
		try
		{
			final Employee employeeData = employeeRepo.findById(empId).orElse(null);
			if (Objects.nonNull(employeeData))
			{
				final List<Employee> bdeList = employeeRepo.findByRoleAndRegion(EmployeeRole.BDE.toString(),
								employeeData.getRegion());
				for (final Employee bde : bdeList)
				{
					monthlyTargetList.add(
									bdeTargetRepo.findByBdeMMyyyy(bde.getEmpId(), MElevenUtil.dateFormatMMyyyy.parse(MMyyyy)));
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getBdeTargetList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return monthlyTargetList;
	}

}
