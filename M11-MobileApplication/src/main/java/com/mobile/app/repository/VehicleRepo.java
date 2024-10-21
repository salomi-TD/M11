package com.mobile.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.app.entity.VehicleDetails;

public interface VehicleRepo extends JpaRepository<VehicleDetails, Integer>
{

	VehicleDetails findByVehicleNumber(final String vehicleNumber);

	VehicleDetails findByVehicleId(final Integer vehicleId);

	List<VehicleDetails> findByRegion(final String regionId);

	List<VehicleDetails> findByRegionAndAvailable(final String regionId, final boolean available);

	@Query("SELECT v FROM VehicleDetails v JOIN WeighmentData w ON v.vehicleId = w.vehicle JOIN DeliveryTracking t "
					+ "ON w.purchaseOrderId = t.poId WHERE t.endTime IS NULL and v.region = :regionId AND DATE(w.creationTime) = CURRENT_DATE")
	List<VehicleDetails> findCurrentlyAssignedVehiclesByRegion(@Param("regionId") final String regionId);

}
