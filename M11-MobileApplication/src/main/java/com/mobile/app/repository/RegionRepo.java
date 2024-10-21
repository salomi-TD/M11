package com.mobile.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.app.entity.Regions;

public interface RegionRepo extends JpaRepository<Regions, String>
{

	@Query("SELECT r FROM Regions r JOIN Employee e ON r.regionId = e.region WHERE e.empId = :empId")
	Regions findRegionByEmployeeId(@Param("empId") final Integer empId);

}
