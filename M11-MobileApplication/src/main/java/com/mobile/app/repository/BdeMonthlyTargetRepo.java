package com.mobile.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.app.entity.BdeMonthlyTarget;

public interface BdeMonthlyTargetRepo extends JpaRepository<BdeMonthlyTarget, String>
{

	BdeMonthlyTarget findByTargetId(final String targetId);

	@Query("SELECT b FROM BdeMonthlyTarget b WHERE b.bde = :empId AND "
					+ "MONTH(creationTime) = MONTH(DATE(:date)) AND YEAR(creationTime) = YEAR(DATE(:date))")
	BdeMonthlyTarget findByBdeMMyyyy(@Param("empId") final Integer empId, @Param("date") final Date date);

	@Query("SELECT b FROM BdeMonthlyTarget b WHERE b.bde = :empId AND b.creationTime BETWEEN :fromDate AND :toDate")
	List<BdeMonthlyTarget> findByBdeAndTimeInterval(@Param("empId") final Integer empId, @Param("fromDate") final Date fromDate,
					@Param("toDate") final Date toDate);

}
