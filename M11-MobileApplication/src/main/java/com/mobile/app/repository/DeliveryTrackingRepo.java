package com.mobile.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.app.entity.DeliveryTracking;

public interface DeliveryTrackingRepo extends JpaRepository<DeliveryTracking, Integer>
{

	DeliveryTracking findByPoId(final Integer purchaseOrderId);

}
