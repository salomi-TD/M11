package com.mobile.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.app.request.WorkflowApprovalRequest;
import com.mobile.app.response.FboApprovalWorkflowListResponse;
import com.mobile.app.service.WorkflowService;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/workflow")
public class WorkflowController
{

	@Autowired
	private WorkflowService workflowService;

	@GetMapping("/registerList/{empId}")
	public FboApprovalWorkflowListResponse getRegisterWorkflowList(@PathVariable("empId") final Integer empId)
	{

		return workflowService.getRegisterWorkflowList(empId);
	}

	@GetMapping("/paymentList/{empId}")
	public FboApprovalWorkflowListResponse getPaymentWorkflowList(@PathVariable("empId") final Integer empId)
	{

		return workflowService.getPaymentWorkflowList(empId);
	}

	@PostMapping("/approval")
	public String approvalWorkflow(@RequestBody final WorkflowApprovalRequest request)
	{

		return workflowService.approvalRequest(request);
	}

	@GetMapping("/fbo/approvals/{enrollmentId}")
	public FboApprovalWorkflowListResponse getFboPaymentApprovals(@PathVariable("enrollmentId") final Integer enrollmentId)
	{

		return workflowService.getFboPaymentApprovals(enrollmentId);
	}

	@PostMapping("/fbo/approvalRequest")
	public String paymentApprovalWorkflowByFbo(@RequestBody final WorkflowApprovalRequest approvalRequest)
	{

		return workflowService.fboPaymentApprovalWorkflow(approvalRequest);
	}

}