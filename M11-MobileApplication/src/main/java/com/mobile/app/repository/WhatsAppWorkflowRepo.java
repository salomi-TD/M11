package com.mobile.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.app.entity.WhatsAppWorkflow;

public interface WhatsAppWorkflowRepo extends JpaRepository<WhatsAppWorkflow, Integer>
{

	@Query("SELECT w FROM WhatsAppWorkflow w WHERE w.fboContact = :fboContact ORDER BY w.creationTime DESC")
	WhatsAppWorkflow findByFboContactAndRecentCreationTime(@Param("fboContact") final String fboContact);

}
