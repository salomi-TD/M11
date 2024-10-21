package com.mobile.app.controller;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.mobile.app.response.FboListResponse;
import com.mobile.app.response.ReportSearchRequest;
import com.mobile.app.service.PdfGenerationService;
import com.mobile.app.service.ReportService;

import jakarta.servlet.http.HttpServletResponse;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/report")
public class ReportController
{

	private static final Logger LOG = LoggerFactory.getLogger(ReportController.class);

	@Autowired
	private ReportService reportService;

	@Autowired
	private PdfGenerationService pdfGenerationService;

	@GetMapping("/followup/{empId}")
	public FboListResponse getRucoDailyFollowupDetailsOfFbo(@PathVariable final Integer empId)
	{
		return reportService.getRucoDailyFollowupDetailsOfFbo(empId);
	}

	@GetMapping("/fbo/{enrollmentId}")
	public ResponseEntity<Resource> generateFboReport(@PathVariable final Integer enrollmentId)
	{
		return generateReport("FBO_" + enrollmentId + "_Report.pdf", pdfGenerationService.generatePdfForFboReport(enrollmentId));
	}

	@GetMapping("/uco/{empId}")
	public ResponseEntity<Resource> generateUcoReport(@PathVariable final Integer empId,
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);
		searchRequest.setFromDate(fromDate);
		searchRequest.setToDate(toDate);
		return generateReport("UCO_" + empId + "_Report.pdf", pdfGenerationService.generatePdfForUcoReport(empId, searchRequest));
	}

	@GetMapping("/uco/fbo/{empId}")
	public ResponseEntity<Resource> generateUcoFboReport(@PathVariable final Integer empId,
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);
		searchRequest.setFromDate(fromDate);
		searchRequest.setToDate(toDate);
		return generateReport("UCO_FBO_" + empId + "_Report.pdf",
						pdfGenerationService.generatePdfForUcoFboReport(empId, searchRequest));
	}

	@GetMapping("/uco/payment/{empId}")
	public ResponseEntity<Resource> generateUcoPaymentReport(@PathVariable final Integer empId,
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);
		searchRequest.setFromDate(fromDate);
		searchRequest.setToDate(toDate);
		return generateReport("UCO_PAYMENT_" + empId + "_Report.pdf",
						pdfGenerationService.generatePdfForUcoPaymentReport(empId, searchRequest));
	}

	@GetMapping("/uco/emission/{empId}")
	public ResponseEntity<Resource> generateUcoEmissionReport(@PathVariable final Integer empId,
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);
		searchRequest.setFromDate(fromDate);
		searchRequest.setToDate(toDate);
		return generateReport("UCO_EMISSION_" + empId + "_Report.pdf",
						pdfGenerationService.generatePdfForUcoEmisionReport(empId, searchRequest));
	}

	@GetMapping("/bde/{empId}")
	public ResponseEntity<Resource> generateBdeReport(@PathVariable final Integer empId,
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);
		searchRequest.setFromDate(fromDate);
		searchRequest.setToDate(toDate);
		return generateReport("BDE_" + empId + "_Report.pdf", pdfGenerationService.generatePdfForBdeReport(empId, searchRequest));
	}

	@GetMapping("/bde/productive/{empId}")
	public ResponseEntity<Resource> generateBdeProductiveReport(@PathVariable final Integer empId,
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);
		searchRequest.setFromDate(fromDate);
		searchRequest.setToDate(toDate);
		return generateReport("BDE_Productive_" + empId + "_Report.pdf",
						pdfGenerationService.generatePdfForBdeProductiveReport(empId, searchRequest));
	}

	@GetMapping("/bde/pending/{empId}")
	public ResponseEntity<Resource> generateBdePendingReport(@PathVariable final Integer empId,
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);
		searchRequest.setFromDate(fromDate);
		searchRequest.setToDate(toDate);
		return generateReport("BDE_Pending_" + empId + "_Report.pdf",
						pdfGenerationService.generatePdfForBdePendingReport(empId, searchRequest));
	}

	@GetMapping("/bde/unproductive/{empId}")
	public ResponseEntity<Resource> generateBdeUnproductiveReport(@PathVariable final Integer empId,
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);
		searchRequest.setFromDate(fromDate);
		searchRequest.setToDate(toDate);
		return generateReport("BDE_Unproductive_" + empId + "_Report.pdf",
						pdfGenerationService.generatePdfForBdeUnproductiveReport(empId, searchRequest));
	}

	@GetMapping("/bde/onHold/{empId}")
	public ResponseEntity<Resource> generateBdeOnHoldReport(@PathVariable final Integer empId,
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);
		searchRequest.setFromDate(fromDate);
		searchRequest.setToDate(toDate);
		return generateReport("BDE_onHold_" + empId + "_Report.pdf",
						pdfGenerationService.generatePdfForBdeOnHoldReport(empId, searchRequest));
	}

	@GetMapping("/bde/unregisteredFbo/{empId}")
	public ResponseEntity<Resource> generateBdeUnregisteredFboReport(@PathVariable final Integer empId,
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);
		searchRequest.setFromDate(fromDate);
		searchRequest.setToDate(toDate);
		return generateReport("BDE_Unregistered_Fbo_" + empId + "_Report.pdf",
						pdfGenerationService.generatePdfForBdeUnregisteredFboReport(empId, searchRequest));
	}

	@GetMapping("/bde/monthlyTarget/{empId}")
	public ResponseEntity<Resource> generateBdeMonthlyTargetReport(@PathVariable final Integer empId,
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);
		searchRequest.setFromDate(fromDate);
		searchRequest.setToDate(toDate);
		return generateReport("BDE_Target_" + empId + "_Report.pdf",
						pdfGenerationService.generatePdfForBdeUnregisteredFboReport(empId, searchRequest));
	}

	private ResponseEntity<Resource> generateReport(final String fileName, final byte[] pdfBytes)
	{
		try
		{
			final ByteArrayResource resource = new ByteArrayResource(pdfBytes);

			final HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

			return ResponseEntity.ok().headers(headers).contentLength(pdfBytes.length).contentType(MediaType.APPLICATION_PDF)
							.body(resource);
		}
		catch (final Exception e)
		{
			LOG.error("generateReport() - Exception occurred. Message:[{}]", e.getMessage(), e);
		}

		return ResponseEntity.status(500).body(new ByteArrayResource("Error generating PDF".getBytes()));
	}

	@GetMapping("/activities/{empId}")
	public ResponseEntity<byte[]> generateRucoOrCeReport(@PathVariable("empId") final Integer empId,
					final HttpServletResponse response, final Model model,
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		Document document = null;
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try
		{
			final ReportSearchRequest searchRequest = new ReportSearchRequest();
			searchRequest.setInterval(interval);
			searchRequest.setFromDate(fromDate);
			searchRequest.setToDate(toDate);
			document = pdfGenerationService.generateRucoOrCePdf(empId, response, model, searchRequest);
			if (Objects.nonNull(document))
			{
				PdfWriter.getInstance(document, byteArrayOutputStream);
				final HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_PDF);
				return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
			}
		}
		catch (final Exception e)
		{
			LOG.error("generateRucoOrCeReport() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return ResponseEntity.status(HttpStatus.OK).body("No data found for the specified date range.".getBytes());
	}

	@GetMapping("/emission")
	public ResponseEntity<Resource> generateEmissionReport(
					@RequestParam(value = "interval", required = false) final String interval,
					@RequestParam(value = "fromDate", required = false) final String fromDate,
					@RequestParam(value = "toDate", required = false) final String toDate)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);
		searchRequest.setFromDate(fromDate);
		searchRequest.setToDate(toDate);
		return generateReport("Emission_Report.pdf", pdfGenerationService.generatePdfForEmissionReport(searchRequest));
	}

}
