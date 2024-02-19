package com.inops.visitorpass.service.job;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.inops.visitorpass.constant.InopsConstant;
import com.inops.visitorpass.entity.EMail;
import com.inops.visitorpass.entity.EmailTemplate;
import com.inops.visitorpass.entity.EmailTemplateAssociation;
import com.inops.visitorpass.entity.Employee;
import com.inops.visitorpass.entity.ScheduledTask;
import com.inops.visitorpass.service.IEmail;
import com.inops.visitorpass.service.IEmailTemplate;
import com.inops.visitorpass.service.impl.ReportGenerationService;
import com.inops.visitorpass.util.EMailClient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
@Service("reportComputation")
@RequiredArgsConstructor
public class ReportComputation implements IJob {

	private final ApplicationContext ctx;
	private final EMailClient emailClient;
	private final IEmail emailService;
	private final IEmailTemplate emailTemplateService;
	private final ReportGenerationService reportGenerationService;

	private List<Employee> employees;
	private List<EmailTemplate> emailTemplates;
	private EMail selectedEmail;

	@PostConstruct
	public void init() {
		employees = ((Optional<List<Employee>>) ctx.getBean("getEmployees")).get();
		/*
		 * emailTemplates = emailTemplateService.findAll().get(); selectedEmail =
		 * emailService.findAll().get().get(0);
		 */
	}

	@Override
	public void execute(Date from, Date to, ScheduledTask task) {
		try {
			emailTemplates = emailTemplateService.findAll().get();
			selectedEmail = emailService.findAll().get().get(0);
			List<Employee> employeeEmails = null;
			EmailTemplate emailTemplete = emailTemplates.stream()
					.filter(template -> template.getTemplateId() == task.getReportTask().getTemplateId()).findAny()
					.orElse(null);
			if (emailTemplete.getSelectionType().equals("Departments")) {
				employeeEmails = employees.stream()
						.filter(employee -> emailTemplete.getAssociations().stream()
								.map(EmailTemplateAssociation::getCode).collect(Collectors.toList())
								.contains(employee.getDepartment().getId()))
						.collect(Collectors.toList());

			} else if (emailTemplete.getSelectionType().equals("Employees")) {
				employeeEmails = employees.stream()
						.filter(employee -> emailTemplete.getAssociations().stream()
								.map(EmailTemplateAssociation::getCode).collect(Collectors.toList())
								.contains(employee.getEmployeeId()))
						.collect(Collectors.toList());

			} else if (emailTemplete.getSelectionType().equals("Caders")) {
				employeeEmails = employees.stream()
						.filter(employee -> emailTemplete.getAssociations().stream()
								.map(EmailTemplateAssociation::getCode).collect(Collectors.toList())
								.contains(employee.getCadre().getCadreId()))
						.collect(Collectors.toList());
			}
			// Generate reports
			Map<String, byte[]> generatedReports = new HashMap();
			for (String reportName : task.getReportTask().getReports().split(",")) {
				byte[] report = reportGeneration(reportName, from, to, employeeEmails,
						task.getReportTask().getFromDay(), task.getReportTask().getToDay());
				generatedReports.put(reportName+".pdf", report);
			}

			emailClient.sendEmailWithAttachment(selectedEmail, emailTemplete, generatedReports, employeeEmails);
		} catch (MessagingException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private byte[] reportGeneration(String reportName, Date from, Date to, List<Employee> filteredList, long startDay,
			long endDay) {
		LocalDate fromDate = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate toDate = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		fromDate = fromDate.plusDays(startDay);
		toDate = toDate.plusDays(endDay);

		byte[] buffer = null;
		switch (reportName) {
		case InopsConstant.ATTENDANCE_REGISTER:
		case InopsConstant.LATEIN_REGISTER:
		case InopsConstant.EARLYOUT_REGISTER:
		case InopsConstant.EXTRAHOURS_REGISTER:
		case InopsConstant.ABSENTEESM_REGISTER:
		case InopsConstant.OVERTIME_REGISTRY:
		case InopsConstant.LEAVE_REGISTER:
			buffer = reportGenerationService.getRegistery().generate(fromDate, toDate, filteredList, reportName);
			break;
		case "Continous Absenteesim":
			buffer = reportGenerationService.getContinousAbsenteesim().generate(fromDate, toDate, filteredList,
					reportName);
			break;
		case InopsConstant.CONSOLIDATED_REPORT:
			buffer = reportGenerationService.getConsolidated().generate(fromDate, toDate, filteredList, reportName);
			break;

		case "All Punches":
			buffer = reportGenerationService.getAllPunches().generate(fromDate, toDate, filteredList, reportName);
			break;

		case "Daily Summary":
			buffer = reportGenerationService.getDailySummary().generate(fromDate, toDate, filteredList, reportName);
			break;

		case "VisitoDaters Register":
			buffer = reportGenerationService.getDailyVisitors().generate(fromDate, toDate, filteredList, reportName);
			break;
		case InopsConstant.PHYSIXAL_DAYS:
			buffer = reportGenerationService.getPhysicalDays().generate(fromDate, toDate, filteredList, reportName);
			break;
		case InopsConstant.LEAVE_TRANSACTION:
			buffer = reportGenerationService.getLeaveTransactions().generate(fromDate, toDate, filteredList,
					reportName);
			break;
		case InopsConstant.LEAVE_BALANCE:
			buffer = reportGenerationService.getLeaveBalance().generate(fromDate, toDate, filteredList, reportName);
			break;
		case InopsConstant.LEAVE_ENCASHMENT:
			buffer = reportGenerationService.getLeaveEncashment().generate(fromDate, toDate, filteredList, reportName);
			break;
		case InopsConstant.LOG_REGISTER:
			buffer = reportGenerationService.getLogRegister().generate(fromDate, toDate, filteredList, reportName);
			break;
		case InopsConstant.LWP_DETAILS:
			buffer = reportGenerationService.getLWPDetails().generate(fromDate, toDate, filteredList, reportName);
			break;
		case InopsConstant.LWP_SUMMARY:
			buffer = reportGenerationService.getLWPSummaryDetails().generate(fromDate, toDate, filteredList,
					reportName);
			break;
		case InopsConstant.THREE_YEARS_ATTENDANCE:
			buffer = reportGenerationService.getThreeYearsAttendance().generate(fromDate, toDate, filteredList,
					reportName);
			break;

		case InopsConstant.DETAILED_PHYSICAL_DAYS:
			buffer = reportGenerationService.getDetailedPhysicalDaysDetails().generate(fromDate, toDate, filteredList,
					reportName);
			break;
		case InopsConstant.FINANTIAL_CUTLIST:
			buffer = reportGenerationService.getFinantialCutlistDetails().generate(fromDate, toDate, filteredList,
					reportName);
			break;

		case InopsConstant.EXTRA_4_HOURS:
			buffer = reportGenerationService.getFourHoursExtraDetails().generate(fromDate, toDate, filteredList,
					reportName);
			break;

		case InopsConstant.CUTLIST_OVERTIME:
			buffer = reportGenerationService.getCutListOvertimeDetails().generate(fromDate, toDate, filteredList,
					reportName);
			break;

		case InopsConstant.ONELINE_CONSOLIDATED:
			buffer = reportGenerationService.getOneLineConsolidatedDetails().generate(fromDate, toDate, filteredList,
					reportName);
			break;

		case InopsConstant.PAYROLL_SHORT_HOURS:
			buffer = reportGenerationService.getPayrollShortHoutsDetails().generate(fromDate, toDate, filteredList,
					reportName);
			break;

		case InopsConstant.MANDAYS_DETAILED:
			buffer = reportGenerationService.getMandaysDetails().generate(fromDate, toDate, filteredList, reportName);
			break;

		default:
			break;
		}

		return buffer;
	}

}
