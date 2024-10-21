package com.mobile.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.app.entity.BdeEnquiryTracking;

public interface BdeEnquiryTrackingRepo extends JpaRepository<BdeEnquiryTracking, Integer>
{

	@Query("SELECT b FROM BdeEnquiryTracking b WHERE b.bde = :empId AND b.creationTime BETWEEN :fromDate AND :toDate")
	List<BdeEnquiryTracking> findByBdeAndTimeInterval(@Param("empId") final Integer empId, @Param("fromDate") final Date fromDate,
					@Param("toDate") final Date toDate);

}
