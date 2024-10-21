package com.mobile.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.app.entity.EmployeeRole;

public interface EmployeeRoleRepo extends JpaRepository<EmployeeRole, String>
{

}
