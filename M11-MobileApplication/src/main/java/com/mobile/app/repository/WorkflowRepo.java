package com.mobile.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.app.entity.FBOApprovalWorkflow;
import com.mobile.app.enums.ApprovalType;

public interface WorkflowRepo extends JpaRepository<FBOApprovalWorkflow, Integer>
{

	List<FBOApprovalWorkflow> findAllByApprover(final Integer approver);

	List<FBOApprovalWorkflow> findByPurchaseOrder(final Integer purchaseOrder);

	List<FBOApprovalWorkflow> findByFbo(final Integer enrollmentId);

	FBOApprovalWorkflow findByFboAndApprovalType(final Integer enrollmentId, final ApprovalType approvalType);
}
