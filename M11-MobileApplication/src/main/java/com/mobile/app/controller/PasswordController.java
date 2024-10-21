package com.mobile.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mobile.app.service.FboService;
@CrossOrigin(origins = "http://localhost:8081")
@Controller
public class PasswordController
{

	private static final Logger LOG = LoggerFactory.getLogger(PasswordController.class);

	@Autowired
	private FboService fboService;

	@GetMapping("/set-password")
	public String showPasswordForm(Model model)
	{

		return "password-form";
	}

	@PostMapping("/update-password")
	public String updatePassword(@RequestParam("enrollmentId") final String enrollmentId,
					@RequestParam("newPassword") final String newPassword, final Model model)
	{
		try
		{
			fboService.updateFboPassword(Integer.parseInt(enrollmentId), newPassword);
			return "result";
		}
		catch (final NumberFormatException e)
		{
			model.addAttribute("error", "Invalid enrollmentId format");
			LOG.error("updatePassword() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return "error-page";
		}
	}
}
