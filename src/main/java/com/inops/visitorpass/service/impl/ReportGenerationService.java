package com.inops.visitorpass.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.inops.visitorpass.constant.InopsConstant;
import com.inops.visitorpass.domain.AttendanceRegister;
import com.inops.visitorpass.domain.ContinousAbsenteesim;
import com.inops.visitorpass.domain.Punch;
import com.inops.visitorpass.entity.Company;
import com.inops.visitorpass.entity.Visitor;
import com.inops.visitorpass.service.DataExtractionService;
import com.inops.visitorpass.service.ICompany;
import com.inops.visitorpass.service.IReport;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service("reportGenerationService")
public class ReportGenerationService {

	private final DataExtractionService registery;
	private final DataExtractionService continousAbsenteesim;
	private final DataExtractionService allPunch;
	private final DataExtractionService dailySummary;
	private final ICompany company;
	ZoneId defaultZoneId = ZoneId.systemDefault();

	public ReportGenerationService(DataExtractionService registery, DataExtractionService continousAbsenteesim,
			DataExtractionService allPunch, DataExtractionService dailySummary, ICompany company) {
		super();
		this.registery = registery;
		this.continousAbsenteesim = continousAbsenteesim;
		this.allPunch = allPunch;
		this.dailySummary = dailySummary;
		this.company = company;
	}

	public byte[] generateReport(Visitor visitor, String fileName) {
		try {

			List<Visitor> visitors = new ArrayList<Visitor>();
			visitors.add(visitor);
			// empLst.add(emp2);

			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

			String newFileName = externalContext.getRealPath("") + File.separator + "resources" + File.separator
					+ "demo" + File.separator + "images" + File.separator + "photocam" + File.separator + fileName
					+ ".jpeg";

			Company companyDetails = company.findAll().get().get(0);
			// String newFileName = companyDetails.getVisitorsPhotoPath() + File.separator +
			// fileName + ".jpeg";

			// dynamic parameters required for report
			Map<String, Object> empParams = new HashMap<String, Object>();
			empParams.put("CompanyName", companyDetails.getCompanyName());
			empParams.put("VisitDate", new Date());
			empParams.put("VisitorImage", newFileName);
			empParams.put("VisitorId", visitor.getVisitorId());
			empParams.put("BadgeNo", visitor.getBadgeNo());
			empParams.put("visitorData", new JRBeanCollectionDataSource(visitors));

			JasperPrint visitorReport = JasperFillManager.fillReport(JasperCompileManager
					// .compileReport(ResourceUtils.getFile("classpath:VisitorPass2.jrxml").getAbsolutePath())
					// // path of
					.compileReport(ResourceUtils
							.getFile(companyDetails.getReportsJRXMLFilePath() + File.separator + "VisitorPass2.jrxml")
							.getAbsolutePath()) // path of
			// the
			// jasper
			// report
					, empParams // dynamic parameters
					, new JREmptyDataSource());

			// the report in PDF format
			// JasperExportManager.exportReportToPdfFile(visitorReport, newPdfFileName);

			return JasperExportManager.exportReportToPdf(visitorReport);
			// return new ResponseEntity<byte[]>
			// (JasperExportManager.exportReportToPdf(empReport), headers, HttpStatus.OK);

		} catch (Exception e) {
			// return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
			System.out.println(e);
		}
		return null;

	}

	public StreamedContent downloadFile(File file) {

		if (!file.exists()) {
			// logger.info("File {} does not exist", file.getPath());
			// BeanUtil.addErrorMessage("File does not exist", "");
			return null;
		}

		try {

			InputStream stream = new BufferedInputStream(new FileInputStream(file));

			return DefaultStreamedContent.builder().contentType("application/pdf").name("TEST.pdf").stream(() -> stream)
					.build();

		} catch (IOException e) {
			// BeanUtil.addErrorMessage("Download failed", e.getMessage());
		}

		return null;
	}

	public IReport getRegistery() {
		return (from, to, id, type) -> {
			try {
				String jrxmlFile = null;
				String reportParameter = null;
				switch (type) {
				case InopsConstant.ATTENDANCE_REGISTER:
					jrxmlFile = "AttendanceRegister.jrxml";
					reportParameter = "attendanceRegister";
					break;
				case InopsConstant.LATEIN_REGISTER:
					jrxmlFile = "LateInRegister.jrxml";
					reportParameter = "lateInRegister";
					break;
				case InopsConstant.EARLYOUT_REGISTER:
					jrxmlFile = "EarlyOutRegister.jrxml";
					reportParameter = "earlyOutRegister";
					break;
				case InopsConstant.EXTRAHOURS_REGISTER:
					jrxmlFile = "ExtraHoutsRegister.jrxml";
					reportParameter = "extrahoursRegister";
					break;

				default:
					break;
				}
				List<AttendanceRegister> attRegister = (List<AttendanceRegister>) registery.dataExtraction(from, to, id,
						type);

				return generateFinalReport(from, to, attRegister, jrxmlFile, reportParameter);

			} catch (Exception e) {
				System.out.println(e);
			}
			return null;
		};

	}

	public IReport getContinousAbsenteesim() {
		return (from, to, id, type) -> {
			try {

				List<ContinousAbsenteesim> continuoursAbsentees = (List<ContinousAbsenteesim>) continousAbsenteesim
						.dataExtraction(from, to, id, type);

				return generateFinalReport(from, to, continuoursAbsentees, "ContinousAbsenteesim.jrxml",
						"continousAbsenteesim");

			} catch (Exception e) {
				System.out.println(e);
			}
			return null;
		};
	}

	public IReport getAllPunches() {
		return (from, to, id, type) -> {
			try {

				List<Punch> allPunches = (List<Punch>) allPunch.dataExtraction(from, to, id, type);

				return generateFinalReport(from, to, allPunches, "Allpunches.jrxml", "Allpunches");

			} catch (Exception e) {
				System.out.println(e);
			}
			return null;
		};
	}

	public IReport getDailySummary() {
		return (from, to, id, type) -> {
			try {

				List<Punch> allPunches = (List<Punch>) dailySummary.dataExtraction(from, to, id, type);

				return generateFinalReport(from, to, allPunches, "DailySummary.jrxml", "dailySummary");

			} catch (Exception e) {
				System.out.println(e);
			}
			return null;
		};
	}

	private byte[] generateFinalReport(LocalDate from, LocalDate to, Collection<?> beanCollection, String jrxmlFileName,
			String type) throws JRException, FileNotFoundException {
		// dynamic parameters required for report
		Company companyDetails = company.findAll().get().get(0);
		Map<String, Object> empParams = new HashMap<String, Object>();
		empParams.put("CompanyName", companyDetails.getCompanyName());
		empParams.put("fromDate", Date.from(from.atStartOfDay(defaultZoneId).toInstant()));
		empParams.put("toDate", Date.from(to.atStartOfDay(defaultZoneId).toInstant()));

		empParams.put(type, new JRBeanCollectionDataSource(beanCollection));

		JasperPrint extractReport = JasperFillManager.fillReport(JasperCompileManager
				// .compileReport(ResourceUtils.getFile("classpath:" +
				// jrxmlFileName).getAbsolutePath()) // path
				.compileReport(
						ResourceUtils.getFile(companyDetails.getReportsJRXMLFilePath() + File.separator + jrxmlFileName)
								.getAbsolutePath()) // path
		// of
		// the
		// jasper
		// report
				, empParams // dynamic parameters
				, new JREmptyDataSource());

		// the report in PDF format
		// JasperExportManager.exportReportToPdfFile(visitorReport, newPdfFileName);

		return JasperExportManager.exportReportToPdf(extractReport);
	}

}
