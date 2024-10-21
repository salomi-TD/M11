package com.mobile.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.app.entity.VehicleReading;

public interface VehicleReadingRepo extends JpaRepository<VehicleReading, Integer>
{

	@Query("SELECT vr FROM VehicleReading vr WHERE vr.vehicleNumber = :vehicleNumber AND  vr.date = CURRENT_DATE")
	VehicleReading findByVehicleNumberAndCurrentDate(@Param("vehicleNumber") String vehicleNumber);

	@Query("SELECT vr FROM VehicleReading vr WHERE vr.date = CURRENT_DATE" + -1)
	List<VehicleReading> findByYesterdayDate();

	@Query("SELECT vr FROM VehicleReading vr WHERE vr.vehicleNumber = :vehicleNumber AND vr.date BETWEEN :fromDate AND :toDate")
	List<VehicleReading> findByVehicleNumberAndDateRange(@Param("fromDate") final Date fromDate,
					@Param("toDate") final Date toDate, @Param("vehicleNumber") final String vehicleNumber);

}
