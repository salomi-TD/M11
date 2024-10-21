package com.mobile.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.app.entity.FboFollowup;

public interface FboFollowupRepo extends JpaRepository<FboFollowup, Integer>
{

	@Query("SELECT f FROM FboFollowup f WHERE f.fboId = :fboId AND (DATE(f.actualFollowupDate) = CURRENT_DATE "
					+ "OR DATE(f.postponeDate) = CURRENT_DATE " + "OR DATE(f.readyForPickupDate) = CURRENT_DATE)")
	FboFollowup findSingleByFboIdAndTodayFollowupDate(@Param("fboId") final Integer fboId);

}
