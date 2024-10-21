package com.mobile.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.app.entity.EmployeeAttendance;

public interface EmployeeAttendanceRepo extends JpaRepository<EmployeeAttendance, Integer>
{

	EmployeeAttendance findByAttendanceId(final Integer attendanceId);

	@Query("SELECT a FROM EmployeeAttendance a WHERE a.empId = :empId AND "
					+ "MONTH(date) = MONTH(DATE(:date)) AND YEAR(date) = YEAR(DATE(:date))")
	List<EmployeeAttendance> findAllByEmpIdAndMonthYear(@Param("empId") final Integer empId, @Param("date") final Date date);

	@Query("SELECT a FROM EmployeeAttendance a WHERE a.empId = :empId AND a.date = CURRENT_DATE")
	EmployeeAttendance findByEmpIdAndDate(final Integer empId);

}