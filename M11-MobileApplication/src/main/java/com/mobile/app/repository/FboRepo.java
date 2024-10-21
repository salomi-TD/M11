package com.mobile.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.app.entity.FoodBusinessOperator;
import com.mobile.app.enums.ApprovalStatus;
import com.mobile.app.enums.ApprovalType;

public interface FboRepo extends JpaRepository<FoodBusinessOperator, Integer>
{

	FoodBusinessOperator findByContactNo(final String contactNo);

	List<FoodBusinessOperator> findByActive(final boolean active);

	@Query("SELECT f FROM FoodBusinessOperator f " + "JOIN FboFollowup fu ON f.enrollmentId = fu.fboId "
					+ "WHERE f.assignedRUCO = :assignedRuco " + "AND f.active = true "
					+ "AND ((DATE(fu.actualFollowupDate) = CURRENT_DATE AND fu.followedupOnActualDate = false) "
					+ "OR (DATE(fu.postponeDate) = CURRENT_DATE AND fu.followedupOnPostponeDate = false AND fu.followedupOnActualDate = true) "
					+ "OR (DATE(fu.readyForPickupDate) = CURRENT_DATE AND fu.followedupOnReadyForPickupDate = false))")
	List<FoodBusinessOperator> findByAssignedRUCOAndTodayFollowupDate(@Param("assignedRuco") final Integer assignedRuco);

	List<FoodBusinessOperator> findByCreatedByOrAssignedRUCOOrAssignedUCO(final Integer createdBy, final Integer assignedRuco,
					final Integer assignedUco);

	@Query("SELECT f FROM FoodBusinessOperator f " + "JOIN Employee e ON (f.createdBy = e.empId OR f.assignedUCO = e.empId)"
					+ " JOIN FBOApprovalWorkflow w ON f.enrollmentId = w.fbo "
					+ " WHERE e.empId = :empId AND w.approvalType = :approvalType AND w.approvalStatus = :approvalStatus"
					+ " AND f.creationTime BETWEEN :fromDate AND :toDate")
	List<FoodBusinessOperator> findByUcoOrBde(@Param("empId") final Integer empId,
					@Param("approvalType") final ApprovalType approvalType,
					@Param("approvalStatus") final ApprovalStatus approvalStatus, @Param("fromDate") final Date fromDate,
					@Param("toDate") final Date toDate);

}
