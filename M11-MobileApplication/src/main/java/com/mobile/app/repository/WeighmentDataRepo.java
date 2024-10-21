package com.mobile.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.app.entity.WeighmentData;

public interface WeighmentDataRepo extends JpaRepository<WeighmentData, Integer>
{

	List<WeighmentData> findByCreatedBy(final Integer empId);

	@Query("SELECT w FROM WeighmentData w WHERE w.assignedFBO = :enrollmentId AND w.creationTime BETWEEN :startDate AND :endDate")
	List<WeighmentData> findAllByAssignedFBOAndDateRange(@Param("enrollmentId") final Integer enrollmentId,
					@Param("startDate") final Date startDate, @Param("endDate") final Date endDate);

	List<WeighmentData> findAllByAssignedFBO(final Integer enrollmentId);

	@Query("SELECT wd FROM WeighmentData wd JOIN Employee e ON (wd.assignedCE = :ceEmpId OR wd.driver = :driverEmpId) "
					+ "JOIN DeliveryTracking dt ON wd.purchaseOrderId = dt.poId "
					+ "WHERE (DATE(wd.creationTime) = CURRENT_DATE AND dt.endTime IS NULL)")
	WeighmentData findByAssignedCEOrDriver(@Param("ceEmpId") final Integer ceEmpId,
					@Param("driverEmpId") final Integer driverEmpId);

	@Query("SELECT w FROM WeighmentData w WHERE w.assignedCE = :empId AND DATE(w.dateToVisit) = CURRENT_DATE")
	List<WeighmentData> findAllByAssignedCEAndDateToVisit(final Integer empId);

	@Query("SELECT w FROM WeighmentData w WHERE (w.assignedCE = :empId OR w.createdBy = :empId) AND w.creationTime BETWEEN :fromDate AND :toDate")
	List<WeighmentData> findAllByEmpIdAndDateRange(@Param("empId") final Integer empId, @Param("fromDate") final Date fromDate,
					@Param("toDate") final Date toDate);

	@Query("SELECT wd FROM WeighmentData wd WHERE (wd.assignedFBO = :enrollmentId AND DATE(wd.creationTime) = CURRENT_DATE - 1)")
	WeighmentData findLatestOrderOfFbo(@Param("enrollmentId") final Integer enrollmentId);

	@Query("SELECT wd FROM WeighmentData wd JOIN Employee e ON wd.createdBy = e.empId "
					+ "JOIN DeliveryTracking dt ON wd.purchaseOrderId = dt.poId "
					+ "WHERE e.empId = :empId AND DATE(wd.creationTime) = CURRENT_DATE AND dt.endTime IS NULL")
	List<WeighmentData> findCurrentDayOrdersAssignedByRuco(@Param("empId") final Integer empId);

	@Query("SELECT wd FROM WeighmentData wd WHERE (wd.createdBy = :empId OR wd.assignedFBO = :empId) AND wd.paymentStatus = 'COMPLETED'")
	List<WeighmentData> findByCreatedByAndPaymentStatus(final Integer empId);
}
