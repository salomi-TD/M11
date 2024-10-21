package com.mobile.app.sheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mobile.app.service.FboService;
import com.mobile.app.service.PdfGenerationService;

@Component
public class ReportScheduler
{

	@Autowired
	private PdfGenerationService pdfGenerationService;

	@Autowired
	private FboService fboService;

	// Once in three months at 8:00 AM on the 1st day of the month starting from Jan.
	// @Scheduled(cron = "0 0 8 1 1/3 ?")
	public void generateFboQuartelyReport()
	{
		pdfGenerationService.generateAndSaveFboReport();
	}

	// Daily once at 8:00 AM.
	// @Scheduled(cron = "0 0 8 * * ?")
	public void generateDailyReport()
	{
		pdfGenerationService.generateAndSaveUcoReport("daily");
		pdfGenerationService.generateAndSaveBdeReport("daily");
	}

	// Weekly once on every Monday at 8:00 AM.
	// @Scheduled(cron = "0 0 8 ? * MON")
	public void generateWeeklyReport()
	{
		pdfGenerationService.generateAndSaveUcoReport("weekly");
		pdfGenerationService.generateAndSaveBdeReport("weekly");
	}

	// Monthly once on the 1st day of every month at 8:00 AM.
	// @Scheduled(cron = "0 0 8 1 * ?")
	public void generateMonthlyReport()
	{
		pdfGenerationService.generateAndSaveUcoReport("monthly");
		pdfGenerationService.generateAndSaveBdeReport("monthly");
	}

	// Create followup entries every day at 12:00 AM.
	@Scheduled(cron = "0 0 0 * * ?")
	public void createFollowupEntries()
	{
		fboService.createFollowupEntries();
	}

}
