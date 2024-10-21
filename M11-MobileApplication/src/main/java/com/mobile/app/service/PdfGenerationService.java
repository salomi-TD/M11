package com.mobile.app.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.lowagie.text.DocumentException;
import com.mobile.app.entity.Employee;
import com.mobile.app.entity.FoodBusinessOperator;
import com.mobile.app.repository.EmployeeRepo;
import com.mobile.app.repository.FboRepo;
import com.mobile.app.response.BdeReport;
import com.mobile.app.response.CalculationResponse;
import com.mobile.app.response.EmployeeResponse;
import com.mobile.app.response.FboReportResponse;
import com.mobile.app.response.ReportSearchRequest;
import com.mobile.app.response.VehicleResponse;
import com.mobile.app.response.WeighmentDataResponse;
import com.mobile.app.util.MElevenUtil;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class PdfGenerationService
{

	@Value("${report.upload.dir}")
	private String reportDir;

	@Value("${logo.path}")
	private String logoPath;

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private ReportService reportService;

	@Autowired
	private FboRepo fboRepo;

	private static final Logger LOG = LoggerFactory.getLogger(PdfGenerationService.class);

	public Document generateRucoOrCePdf(final Integer empId, final HttpServletResponse response, final Model model,
					final ReportSearchRequest searchRequest) throws IOException
	{
		final List<WeighmentDataResponse> wDataRespList = reportService.getReportAssignedToEmpByDateRange(empId, searchRequest);
		if (!wDataRespList.isEmpty())
		{
			final List<String> fieldNames = getHeaders(wDataRespList.get(0).getClass());
			model.addAttribute("logoPath", logoPath);
			model.addAttribute("fieldNames", fieldNames);
			model.addAttribute("dataList", wDataRespList);
			model.addAttribute("calculationData", getCalculationData(wDataRespList, searchRequest));
			model.addAttribute("EmpDetails", mapper.map(employeeRepo.findById(empId).get(), EmployeeResponse.class));

			return renderPdf(response, readHtmlContent(), model);
		}
		return null;
	}

	private String readHtmlContent() throws IOException
	{
		final ClassPathResource resource = new ClassPathResource("templates/pdf-ce-ruco-report-template.html");
		final byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
		return new String(bytes, StandardCharsets.UTF_8);
	}

	private Document renderPdf(HttpServletResponse response, String htmlContent, Model model)
	{
		// Render PDF from HTML content
		final Document document = new Document();
		try
		{
			final PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
			document.open();

			// Process HTML content with Thymeleaf
			final Context thymeleafContext = new Context();
			thymeleafContext.setVariables(model.asMap());

			final String processedHtmlContent = new SpringTemplateEngine().process(htmlContent, thymeleafContext);

			// Parse processed HTML content to PDF
			XMLWorkerHelper.getInstance().parseXHtml(writer, document,
							new ByteArrayInputStream(processedHtmlContent.getBytes(StandardCharsets.UTF_8)));

			return document;
		}
		catch (final Exception e)
		{
			LOG.error("renderPdf() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		finally
		{
			document.close();
		}
		return document;
	}

	private List<String> getHeaders(final Class<?> clazz)
	{
		final List<String> fieldNames = new ArrayList<>();
		final Field[] fields = clazz.getDeclaredFields();
		for (final Field field : fields)
		{
			fieldNames.add(field.getName());
		}
		return fieldNames;
	}

	public CalculationResponse getCalculationData(final List<WeighmentDataResponse> ordersList,
					final ReportSearchRequest searchRequest)
	{
		int totalOrders = 0;
		double totalOil = 0.0;
		double totalPrice = 0.0;

		if (!CollectionUtils.isEmpty(ordersList))
		{
			for (final WeighmentDataResponse order : ordersList)
			{
				totalOrders++;
				totalOil += MElevenUtil.getDoubleValue(order.getWeightCalculatedInKG());
				totalPrice += MElevenUtil.getDoubleValue(order.getTotalPrice());
			}
		}
		final CalculationResponse calculationReponse = new CalculationResponse();
		calculationReponse.setDuration(setDuration(searchRequest));
		calculationReponse.setTotalOil(totalOil);
		calculationReponse.setTotalOrders(totalOrders);
		calculationReponse.setTotalPrice(totalPrice);
		return calculationReponse;

	}

	public byte[] generatePdfForFboReport(final Integer enrollmentId)
	{
		final FboReportResponse fboReport = reportService.generateFboReport(enrollmentId);
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("fboData", fboReport.getFboResponse());
			context.setVariable("weighmentDataList", fboReport.getWeighmentDataList());
			context.setVariable("totalWeightOfOil", MElevenUtil.getDoubleValue(fboReport.getTotalWeightOfOil()));
			context.setVariable("pendingAmount", MElevenUtil.getDoubleValue(fboReport.getPendingAmount()));
			context.setVariable("paidAmount", MElevenUtil.getDoubleValue(fboReport.getPaidAmount()));

			return renderPdf(templateEngine.process("pdf-fbo-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForFboReport() - IOException occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}

	private byte[] renderPdf(final String processedHtml, final ByteArrayOutputStream baos)
	{
		final ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(processedHtml);
		renderer.layout();
		try
		{
			renderer.createPDF(baos);
		}
		catch (final DocumentException de)
		{
			LOG.error("renderPdf() - DocumentException occurred. Message: [{}]", de.getMessage(), de);
		}
		return baos.toByteArray();
	}

	// FBO Report Cronjob
	public void generateAndSaveFboReport()
	{
		final List<FoodBusinessOperator> activeFbos = fboRepo.findByActive(true);
		final List<Integer> enrollmentIds = activeFbos.stream().map(FoodBusinessOperator::getEnrollmentId).toList();
		if (!CollectionUtils.isEmpty(enrollmentIds))
		{
			for (final Integer enrollmentId : enrollmentIds)
			{
				final String fileName = "FBO_" + enrollmentId + "_" + MElevenUtil.dateFormatddMMyyyy.format(new Date())
								+ "_Report.pdf";
				savePdfToFile(generatePdfForFboReport(enrollmentId), fileName);
			}
		}
	}

	private void savePdfToFile(final byte[] pdfBytes, final String fileName)
	{
		try
		{
			final Path pdfPath = Path.of(reportDir, fileName);
			final Path directoryPath = pdfPath.getParent();
			if (!Files.exists(directoryPath))
			{
				Files.createDirectories(directoryPath);
			}
			Files.write(pdfPath, pdfBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}
		catch (final IOException e)
		{
			LOG.error("savePdfToFile() - Error saving PDF to file. Message: {}", e.getMessage(), e);
		}
	}

	public byte[] generatePdfForUcoReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("duration", setDuration(searchRequest));
			context.setVariable("ucoReport", reportService.generateUcoReport(empId, searchRequest));

			return renderPdf(templateEngine.process("pdf-uco-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForUcoReport() - Exception occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}

	public byte[] generatePdfForUcoFboReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("duration", setDuration(searchRequest));
			context.setVariable("ucoReport", reportService.generateUcoFboReport(empId, searchRequest));

			return renderPdf(templateEngine.process("pdf-uco-fbo-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForUcoReport() - Exception occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}

	public byte[] generatePdfForUcoPaymentReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("duration", setDuration(searchRequest));
			context.setVariable("ucoReport", reportService.generateUcoPaymentReport(empId, searchRequest));

			return renderPdf(templateEngine.process("pdf-uco-payment-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForUcoReport() - Exception occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}

	public byte[] generatePdfForUcoEmisionReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("duration", setDuration(searchRequest));
			context.setVariable("ucoReport", reportService.generateUcoEmissionReport(empId, searchRequest));

			return renderPdf(templateEngine.process("pdf-uco-emission-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForUcoReport() - Exception occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}

	private String setDuration(final ReportSearchRequest searchRequest)
	{
		if (StringUtils.isBlank(searchRequest.getInterval()))
		{
			try
			{
				return "From " + MElevenUtil.dateFormatddMMyyyy
								.format(MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getFromDate())) + " To "
								+ MElevenUtil.dateFormatddMMyyyy
												.format(MElevenUtil.dateFormatYYYYMMdd.parse(searchRequest.getToDate()));
			}
			catch (final ParseException e)
			{
				LOG.error("setDuration() - Exception occurred. Message: [{}]", e.getMessage(), e);
			}
		}
		return searchRequest.getInterval();
	}

	public void generateAndSaveUcoReport(final String interval)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);

		final List<Employee> ucoList = employeeRepo.findByRole("UCO");
		final List<Integer> empIds = ucoList.stream().map(Employee::getEmpId).toList();
		if (!CollectionUtils.isEmpty(empIds))
		{
			for (final Integer empId : empIds)
			{
				final String fileName = "UCO_" + empId + "_" + MElevenUtil.dateFormatddMMyyyy.format(new Date()) + "_" + interval
								+ "_Report.pdf";
				savePdfToFile(generatePdfForUcoReport(empId, searchRequest), fileName);
			}
		}
	}

	public byte[] generatePdfForBdeReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		final BdeReport bdeReport = reportService.generateBdeReport(empId, searchRequest);
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("duration", setDuration(searchRequest));
			context.setVariable("bdeData", bdeReport.getBdeData());
			context.setVariable("productiveList", bdeReport.getProductiveList());
			context.setVariable("pendingList", bdeReport.getPendingList());
			context.setVariable("unproductiveList", bdeReport.getUnproductiveList());
			context.setVariable("onHoldList", bdeReport.getOnHoldList());
			context.setVariable("unregisteredFboList", bdeReport.getUnregisteredFboList());
			context.setVariable("bdeMonthlyTargetList", bdeReport.getBdeMonthlyTargetList());

			return renderPdf(templateEngine.process("pdf-bde-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForBdeReport() - IOException occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}

	public byte[] generatePdfForBdePendingReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		final BdeReport bdeReport = reportService.generateBdeReport(empId, searchRequest);
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("duration", setDuration(searchRequest));
			context.setVariable("pendingList", bdeReport.getPendingList());

			return renderPdf(templateEngine.process("pdf-bde-pending-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForBdePendingReport() - IOException occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}

	public byte[] generatePdfForBdeProductiveReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		final BdeReport bdeReport = reportService.generateBdeReport(empId, searchRequest);
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("duration", setDuration(searchRequest));
			context.setVariable("productiveList", bdeReport.getProductiveList());

			return renderPdf(templateEngine.process("pdf-bde-productive-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForBdeProductiveReport() - IOException occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}

	public byte[] generatePdfForBdeUnproductiveReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		final BdeReport bdeReport = reportService.generateBdeReport(empId, searchRequest);
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("duration", setDuration(searchRequest));
			context.setVariable("unproductiveList", bdeReport.getUnproductiveList());

			return renderPdf(templateEngine.process("pdf-bde-unproductive-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForBdeUnproductiveReport() - IOException occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}

	public byte[] generatePdfForBdeOnHoldReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		final BdeReport bdeReport = reportService.generateBdeReport(empId, searchRequest);
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("duration", setDuration(searchRequest));
			context.setVariable("onHoldList", bdeReport.getOnHoldList());

			return renderPdf(templateEngine.process("pdf-bde-onhold-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForBdeOnHoldReport() - IOException occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}

	public byte[] generatePdfForBdeUnregisteredFboReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		final BdeReport bdeReport = reportService.generateBdeReport(empId, searchRequest);
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("duration", setDuration(searchRequest));
			context.setVariable("unregisteredFboList", bdeReport.getUnregisteredFboList());

			return renderPdf(templateEngine.process("pdf-bde-unregistered-fbo-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForBdeUnregisteredFboReport() - IOException occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}

	public byte[] generatePdfForBdeMonthlyTargetReport(final Integer empId, final ReportSearchRequest searchRequest)
	{
		final BdeReport bdeReport = reportService.generateBdeReport(empId, searchRequest);
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("duration", setDuration(searchRequest));
			context.setVariable("bdeMonthlyTargetList", bdeReport.getBdeMonthlyTargetList());

			return renderPdf(templateEngine.process("pdf-bde-monthly-target-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForBdeMonthlyTargetReport() - IOException occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}

	public void generateAndSaveBdeReport(final String interval)
	{
		final ReportSearchRequest searchRequest = new ReportSearchRequest();
		searchRequest.setInterval(interval);

		final List<Employee> bdeList = employeeRepo.findByRole("BDE");
		final List<Integer> empIds = bdeList.stream().map(Employee::getEmpId).toList();
		if (!CollectionUtils.isEmpty(empIds))
		{
			for (final Integer empId : empIds)
			{
				final String fileName = "BDE_" + empId + "_" + MElevenUtil.dateFormatddMMyyyy.format(new Date()) + "_" + interval
								+ "_Report.pdf";
				savePdfToFile(generatePdfForUcoReport(empId, searchRequest), fileName);
			}
		}
	}

	public byte[] generatePdfForEmissionReport(final ReportSearchRequest searchRequest)
	{
		final List<VehicleResponse> emissionReport = reportService.generateEmissionReport(searchRequest);
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final Context context = new Context();
			context.setVariable("logoPath", logoPath);
			context.setVariable("duration", setDuration(searchRequest));
			context.setVariable("emissionReport", emissionReport);
			return renderPdf(templateEngine.process("pdf-emission-report-template", context), baos);
		}
		catch (final IOException ioe)
		{
			LOG.error("generatePdfForEmissionReport() - IOException occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		return new byte[0];
	}
}