package com.mobile.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mobile.app.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer>
{

	Location findByEmpId(int empId);

}
