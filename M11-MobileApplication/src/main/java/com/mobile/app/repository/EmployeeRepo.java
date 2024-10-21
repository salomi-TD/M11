package com.mobile.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.app.entity.Employee;

public interface EmployeeRepo extends JpaRepository<Employee, Integer>
{

	Employee findByContact(final String contact);

	List<Employee> findByRole(final String role);

	List<Employee> findByRegion(final String region);

	List<Employee> findByRoleAndRegion(final String role, final String region);

//	@Query("SELECT e FROM Employee e JOIN EmployeeAttendance ea ON e.empId = ea.empId "
//					+ "WHERE e.role = :role AND e.region = :region AND DATE(ea.punchInTime) = CURRENT_DATE "
//					+ "AND ea.punchOutTime IS NULL AND DATE(ea.creationTime) = CURRENT_DATE")
//	List<Employee> findByRoleAndRegionAndIsPresent(@Param("role") final String role, @Param("region") final String region);
	
	@Query("SELECT e FROM Employee e JOIN EmployeeAttendance ea ON e.empId = ea.empId "
					+ "WHERE e.role = :role AND e.region = :region AND ea.present = true AND DATE(ea.date) = CURRENT_DATE")
	List<Employee> findByRoleAndRegionAndIsPresent(@Param("role") final String role, @Param("region") final String region);

	@Query("SELECT e FROM Employee e JOIN WeighmentData w ON e.empId = w.assignedCE JOIN DeliveryTracking dt "
					+ "ON w.purchaseOrderId = dt.poId WHERE dt.endTime IS NULL AND e.region = :regionId AND DATE(w.creationTime) = CURRENT_DATE")
	List<Employee> findCurrentlyAssignedCesByRegion(@Param("regionId") final String regionId);

	@Query("SELECT e FROM Employee e JOIN WeighmentData w ON e.empId = w.driver JOIN DeliveryTracking dt "
					+ "ON w.purchaseOrderId = dt.poId WHERE dt.endTime IS NULL AND e.region = :regionId AND DATE(w.creationTime) = CURRENT_DATE")
	List<Employee> findCurrentlyAssignedDriversByRegion(@Param("regionId") final String regionId);

}
