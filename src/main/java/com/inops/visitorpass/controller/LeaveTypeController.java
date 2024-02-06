package com.inops.visitorpass.controller;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.inops.visitorpass.entity.CompensatoryOff;
import com.inops.visitorpass.entity.CompensatoryOffScheduler;
import com.inops.visitorpass.entity.Division;
import com.inops.visitorpass.entity.Employee;
import com.inops.visitorpass.entity.Holiday;
import com.inops.visitorpass.entity.LeaveApplication;
import com.inops.visitorpass.entity.LeaveApplicationType;
import com.inops.visitorpass.entity.LeaveBalance;
import com.inops.visitorpass.entity.LeaveBalanceHistory;
import com.inops.visitorpass.entity.LeaveTransactions;
import com.inops.visitorpass.entity.LeaveTypeEntity;
import com.inops.visitorpass.entity.RoleEntitlement;
import com.inops.visitorpass.entity.User;
import com.inops.visitorpass.repository.LeaveApplicationTypeRepository;
import com.inops.visitorpass.service.ICompensatoryOff;
import com.inops.visitorpass.service.ICompensatoryOffScheduler;
import com.inops.visitorpass.service.IDivision;
import com.inops.visitorpass.service.IEmployee;
import com.inops.visitorpass.service.IEntitlement;
import com.inops.visitorpass.service.IHoliday;
import com.inops.visitorpass.service.ILeaveApplication;
import com.inops.visitorpass.service.ILeaveBalance;
import com.inops.visitorpass.service.ILeaveBalanceHistory;
import com.inops.visitorpass.service.ILeaveTransactions;
import com.inops.visitorpass.service.ILeaveType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import javax.enterprise.context.RequestScoped;

@Getter
@Setter
@Log4j2
@Named
@RequestScoped
@Component("leaveTypeController")
@Scope("session")
@RequiredArgsConstructor
public class LeaveTypeController implements Serializable{

	@Autowired
	ApplicationContext ctx;

	private final IEntitlement entitlementService;
	private final ILeaveType leaveTypeService;
	private final IHoliday holidayService;
	private final ICompensatoryOff compensatoryOffService;
	private final ICompensatoryOffScheduler compensatoryOffSchedulerService;
	private final ILeaveBalance leaveBalanceService;
	private final ILeaveBalanceHistory leaveBalanceHistoryService;
	private final IEmployee employeeService;
	private final ILeaveTransactions leaveTransactionsService;
	private final IDivision division;
	private final ILeaveApplication leaveApplicationService;
	private final LeaveApplicationTypeRepository leaveApplicationTypeRepository;

	ZoneId defaultZoneId = ZoneId.systemDefault();
	private List<Employee> employees;
	private List<Long> selectedEmployees;

	private List<Division> divisions;
	private Set<Long> selectedDivisions;
	private Division divisionObj;

	private LeaveTransactions selectedLeaveTransaction;
	private List<LeaveTransactions> selectedLeaveTransactions;
	private List<LeaveTransactions> leaveTransactions;

	private LeaveApplication selectedLeaveApplication;
	private List<LeaveApplication> selectedLeaveApplications;
	private List<LeaveApplication> leaveApplications;

	private LeaveBalance selectedLeaveBalance;
	private List<LeaveBalance> selectedLeaveBalances;
	private List<LeaveBalance> leaveBalances;

	private LeaveTypeEntity leaveTypeEntity;
	private LeaveTypeEntity selectedLeaveTypeEntity;
	private List<LeaveTypeEntity> selectedLeaveTypeEntitys;

	private CompensatoryOff selectedCompensatoryOff;
	private List<CompensatoryOff> selectedCompensatoryOffs;

	private CompensatoryOffScheduler selectedCompensatoryOffScheduler;
	private List<CompensatoryOffScheduler> selectedCompensatoryOffSchedulers;

	private Holiday selectedHoliday;
	private List<Holiday> selectedHolidays;
	private long leaveTypeId;

	private List<LeaveTypeEntity> leaveTypes;
	private LeaveTypeEntity leaveTypeObject;

	private List<Holiday> holidays;
	private List<CompensatoryOff> compensatoryOffs;
	private List<CompensatoryOffScheduler> compensatoryOffSchedulers;

	private List<RoleEntitlement> dbRoleEntitlements;
	private List<Long> selectedRoleEntitlements;

	private String[] durationAllowed;

	private UploadedFile file;

	@PostConstruct
	public void init() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		employees = ((Optional<List<Employee>>) ctx.getBean("getEmployees")).get();
		leaveTypes = leaveTypeService.findAll().get();
		holidays = holidayService.findAll().get();
		compensatoryOffs = compensatoryOffService.findAll().get();
		compensatoryOffSchedulers = compensatoryOffSchedulerService.findAll().get();
		leaveBalances = leaveBalanceService.findAll().get();
		// leaveTransactions = leaveTransactionsService.findAll().get();
		divisions = division.findAll().get();
		dbRoleEntitlements = entitlementService.findAll().get();
		leaveApplications = leaveApplicationService.findAll().get();
	}

	public void openNew() {
		this.selectedHoliday = new Holiday();
		this.selectedLeaveApplication = new LeaveApplication();
		this.selectedLeaveTypeEntity = new LeaveTypeEntity();
		this.selectedCompensatoryOff = new CompensatoryOff();
		this.selectedCompensatoryOffScheduler = new CompensatoryOffScheduler();
		this.selectedLeaveBalance = new LeaveBalance();
	}

	public void leaveDetails() {
		this.selectedLeaveTypeEntity = leaveTypes.stream().filter(leave -> leave.getLeaveTypeId() == leaveTypeId)
				.findAny().orElse(null);
	}

	public void saveLeave() {
		try {
			if (!selectedRoleEntitlements.isEmpty()) {
				selectedLeaveTypeEntity.setEntitlementRoles(dbRoleEntitlements.stream()
						.filter(ent -> selectedRoleEntitlements.contains(ent.getEntitlementRoleId()))
						.collect(Collectors.toList()));
			}
			if (this.selectedLeaveTypeEntity.getLeaveTypeId() == 0l) {

				leaveTypeService.create(selectedLeaveTypeEntity);
				leaveTypes.add(selectedLeaveTypeEntity);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Leave " + selectedLeaveTypeEntity.getLeaveName() + " created successfully"));

			} else {
				leaveTypeService.create(selectedLeaveTypeEntity);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Leave " + selectedLeaveTypeEntity.getLeaveName() + " updated successfully"));
			}

			PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
			PrimeFaces.current().ajax().update("leaveNameAdvanced", "manage-product-content", ":form:messages",
					":form:dt-products");
		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", e.getMessage());
		}
	}

	public void deleteLeave() {

		leaveTypeService.delete(selectedLeaveTypeEntity);
		this.leaveTypes.remove(this.selectedLeaveTypeEntity);
		if (this.selectedLeaveTypeEntitys != null) {
			this.selectedLeaveTypeEntitys.remove(this.selectedLeaveTypeEntity);
		}
		this.selectedLeaveTypeEntity = null;
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "LeaveType deleted successfully");
	}

	public void deleteLeaves() {
		leaveTypeService.deleteAll(this.selectedLeaveTypeEntitys);
		this.leaveTypes.removeAll(this.selectedLeaveTypeEntitys);
		this.selectedLeaveTypeEntitys = null;
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "LeaveTypes deleted successfully");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
	}

	public String getDeleteLeaveButtonMessage() {
		if (hasSelectedLeavess()) {
			int size = this.selectedLeaveTypeEntitys.size();
			return size > 1 ? size + " leaves selected" : "1 leave selected";
		}

		return "Delete";
	}

	public boolean hasSelectedLeavess() {
		return this.selectedLeaveTypeEntitys != null && !this.selectedLeaveTypeEntitys.isEmpty();
	}

	public void saveHoliday() {
		try {

			selectedHoliday.setDivisions(divisions.stream()
					.filter(div -> selectedDivisions.contains(div.getDivisionId())).collect(Collectors.toList()));
			holidayService.save(selectedHoliday);
			holidays.add(selectedHoliday);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Holiday " + selectedHoliday.getHolidayName() + " created successfully"));

			PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
			PrimeFaces.current().ajax().update("leaveNameAdvanced", "manage-product-content", ":form:messages",
					":form:dt-products");
		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", e.getMessage());
		}
	}

	public void deleteHoliday() {

		holidayService.delete(selectedHoliday);
		this.holidays.remove(this.selectedHoliday);
		if (this.selectedHolidays != null) {
			this.selectedHolidays.remove(this.selectedHoliday);
		}
		this.selectedHoliday = null;
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "Holiday deleted successfully");
	}

	public void deleteHolidays() {
		holidayService.deleteAll(this.selectedHolidays);
		this.holidays.removeAll(this.selectedHolidays);
		this.selectedHolidays = null;
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "holidays deleted successfully");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
	}

	public String getDeleteHolidayButtonMessage() {
		if (hasSelectedHolidays()) {
			int size = this.selectedHolidays.size();
			return size > 1 ? size + " holidays selected" : "1 holiday selected";
		}

		return "Delete";
	}

	public boolean hasSelectedHolidays() {
		return this.selectedHolidays != null && !this.selectedHolidays.isEmpty();
	}

	public void saveCompOff() {
		try {
			if (durationAllowed.length > 0) {
				selectedCompensatoryOff.setDurationAllowed(String.join(",", durationAllowed));
			}
			if (this.selectedCompensatoryOff.getId() == 0l) {
				compensatoryOffService.save(selectedCompensatoryOff);
				this.compensatoryOffs.add(selectedCompensatoryOff);
			} else {
				compensatoryOffService.save(selectedCompensatoryOff);
			}
			addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "CompOff Added successfully");

			PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
			PrimeFaces.current().ajax().update("manage-product-content", "form:messages", "form:dt-products");

		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", e.getMessage());
		}
	}

	public void deleteCompOff() {

		compensatoryOffService.delete(selectedCompensatoryOff);
		this.compensatoryOffs.remove(this.selectedCompensatoryOff);
		if (this.selectedCompensatoryOffs != null) {
			this.selectedCompensatoryOffs.remove(this.selectedCompensatoryOff);
		}
		this.selectedCompensatoryOff = null;
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "CompOff deleted successfully");
	}

	public void deleteCompOffs() {
		compensatoryOffService.deleteAll(this.selectedCompensatoryOffs);
		this.compensatoryOffs.removeAll(this.selectedCompensatoryOffs);
		this.selectedCompensatoryOffs = null;
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "CompOffs deleted successfully");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
	}

	public String getDeleteCompOffButtonMessage() {
		if (hasSelectedCompOffs()) {
			int size = this.selectedCompensatoryOffs.size();
			return size > 1 ? size + " compOffs selected" : "1 compOff selected";
		}
		return "Delete";
	}

	public boolean hasSelectedCompOffs() {
		return this.selectedCompensatoryOffs != null && !this.selectedCompensatoryOffs.isEmpty();
	}

	public void saveCompOffScheduler() {
		try {
			if (this.selectedCompensatoryOffScheduler.getId() == null) {
				compensatoryOffSchedulerService.save(selectedCompensatoryOffScheduler);
				this.compensatoryOffSchedulers.add(selectedCompensatoryOffScheduler);
				addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "CompOff Scheduler Added successfully");

			} else {
				CompensatoryOffScheduler compOffScheduler = compensatoryOffSchedulers.stream()
						.filter(scheduler -> scheduler.getId() == selectedCompensatoryOffScheduler.getId()).findAny()
						.orElse(null);
				compOffScheduler.setDateBefore(selectedCompensatoryOffScheduler.getDateBefore());
				compOffScheduler.setIncludeOverTime(selectedCompensatoryOffScheduler.isIncludeOverTime());
				compOffScheduler.setName(selectedCompensatoryOffScheduler.getName());
				compOffScheduler.setTimeToSchedule(selectedCompensatoryOffScheduler.getTimeToSchedule());
				compOffScheduler.setDivision(selectedCompensatoryOffScheduler.getDivision());
				compensatoryOffSchedulerService.save(selectedCompensatoryOffScheduler);
				addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "CompOff Scheduler updated successfully");
			}
		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", e.getMessage());
		}
	}

	public void deleteCompOffScheduler() {

		compensatoryOffSchedulerService.delete(selectedCompensatoryOffScheduler);
		this.compensatoryOffSchedulers.remove(this.selectedCompensatoryOffScheduler);
		if (this.selectedCompensatoryOffSchedulers != null) {
			this.selectedCompensatoryOffSchedulers.remove(this.selectedCompensatoryOffScheduler);
		}
		this.selectedCompensatoryOffScheduler = null;
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "CompOff Scheduler deleted successfully");
	}

	public void deleteCompOffSchedulers() {
		compensatoryOffSchedulerService.deleteAll(this.selectedCompensatoryOffSchedulers);
		this.compensatoryOffSchedulers.removeAll(this.selectedCompensatoryOffSchedulers);
		this.selectedCompensatoryOffSchedulers = null;
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "CompOff Scheduler deleted successfully");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
	}

	public String getDeleteSchedulerButtonMessage() {
		if (hasSelectedSchedulers()) {
			int size = this.selectedCompensatoryOffSchedulers.size();
			return size > 1 ? size + " scheduler selected" : "1 scheduler selected";
		}

		return "Delete";
	}

	public boolean hasSelectedSchedulers() {
		return this.selectedCompensatoryOffSchedulers != null && !this.selectedCompensatoryOffSchedulers.isEmpty();
	}

	public void edidLeaveApplicationType(RowEditEvent<LeaveApplicationType> event) {

		leaveApplicationTypeRepository.save(event.getObject());
		FacesMessage msg = new FacesMessage("Product Edited", String.valueOf(event.getObject().getApplicationTypeId()));
		FacesContext.getCurrentInstance().addMessage(null, msg);

	}

	public void saveLeaveApplication() {
		try {
			List<LeaveApplicationType> applicationTypes = new ArrayList<>();
			if (selectedLeaveApplication.getApplicationTypes() == null) {

				long daysDifference = ChronoUnit.DAYS.between(
						convertToLocalDate(selectedLeaveApplication.getFromDate()),
						convertToLocalDate(selectedLeaveApplication.getToDate()));
				LongStream.rangeClosed(1, daysDifference).forEach(range -> {
					LeaveApplicationType appType = new LeaveApplicationType();

					appType.setAppliedDate(Date.from(convertToLocalDate(selectedLeaveApplication.getFromDate())
							.plusDays(range - 1).atStartOfDay(defaultZoneId).toInstant()));
					appType.setLeaveApplication(selectedLeaveApplication);
					appType.setStatus("new");
					applicationTypes.add(appType);
				});

				selectedLeaveApplication.setApplicationTypes(applicationTypes);

			}

			if (selectedLeaveApplication.getApplicationId() == 0l) {
				selectedLeaveApplication.setApplicationStatus("new");
				selectedLeaveApplication.setEditable(true);
				leaveApplicationService.save(selectedLeaveApplication);
				leaveApplications.add(selectedLeaveApplication);

			} else {
				selectedLeaveApplication.getApplicationTypes()
						.forEach(type -> type.setStatus(selectedLeaveApplication.getApplicationStatus()));
				if (!selectedLeaveApplication.getApplicationStatus().equals("new"))
					selectedLeaveApplication.setEditable(false);
				leaveApplicationService.save(selectedLeaveApplication);
			}

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("LeaveApplication created successfully"));

			PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
			PrimeFaces.current().ajax().update("leaveNameAdvanced", "manage-product-content", ":form:messages",
					":form:dt-products");
		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", e.getMessage());
		}
	}

	public void deleteLeaveApplication() {

		leaveApplicationService.delete(selectedLeaveApplication);
		this.leaveApplications.remove(this.selectedLeaveApplication);
		if (this.selectedLeaveApplications != null) {
			this.selectedLeaveApplications.remove(this.selectedLeaveApplication);
		}
		this.selectedHoliday = null;
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "selectedLeaveApplication deleted successfully");
	}

	public void deleteLeaveApplications() {
		leaveApplicationService.deleteAll(this.selectedLeaveApplications);
		this.leaveApplications.removeAll(this.selectedLeaveApplications);
		this.selectedLeaveApplications = null;
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "selectedLeaveApplication deleted successfully");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
	}

	public String getDeleteLeaveApplicationButtonMessage() {
		if (hasSelectedLeaveApplication()) {
			int size = this.selectedLeaveApplications.size();
			return size > 1 ? size + " LeaveApplication selected" : "1 LeaveApplication selected";
		}

		return "Delete";
	}

	public boolean hasSelectedLeaveApplication() {
		return this.selectedLeaveApplications != null && !this.selectedLeaveApplications.isEmpty();
	}

	public void handleFileUpload(FileUploadEvent event) {
		FacesMessage message = new FacesMessage("Successful", event.getFile().getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void saveLeaveBalance() {

		LeaveBalanceHistory leaveBalanceHistory = new LeaveBalanceHistory(0l, LocalDate.now().getMonth().getValue(),
				LocalDate.now().getYear(), new Date(), selectedLeaveBalance.getPreviousBalance(),
				selectedLeaveBalance.getEarnedBalance(), selectedLeaveBalance.getRedeemedBalance(),
				selectedLeaveBalance.getClosingBalance(), selectedLeaveBalance.getCarryForwardBalance(),
				selectedLeaveBalance.getEncashedBalance(), selectedLeaveBalance);
		selectedLeaveBalance.getLeaveBalanceHistorys().add(leaveBalanceHistory);

		leaveBalanceService.save(selectedLeaveBalance);

		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("balance updated"));

		PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
	}

	public void deleteLeaveBalance() {
		this.leaveBalances.remove(this.selectedLeaveBalance);
		this.leaveBalanceService.delete(this.selectedLeaveBalance);
		this.selectedLeaveBalance = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product Removed"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
	}

	public String getDeleteLeaveBalanceButtonMessage() {
		if (hasSelectedLeaveBalances()) {
			int size = this.selectedLeaveBalances.size();
			return size > 1 ? size + " Leave Balance selected" : "1 Leave Balance selected";
		}

		return "Delete";
	}

	public boolean hasSelectedLeaveBalances() {
		return this.selectedLeaveBalances != null && !this.selectedLeaveBalances.isEmpty();
	}

	public void deleteSelectedProducts() { //
		this.leaveBalanceService.deleteAll(this.selectedLeaveBalances);
		this.leaveBalances.removeAll(this.selectedLeaveBalances);
		this.selectedLeaveBalances = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Leave Balances Removed"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
	}

	public void openLevel2() {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		PrimeFaces.current().dialog().openDynamic("level2", options, null);
	}

	public void onReturnFromLevel2(SelectEvent event) {
		// pass back to root
		PrimeFaces.current().dialog().closeDynamic(event.getObject());
	}

	public void openLevel1() {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		PrimeFaces.current().dialog().openDynamic("level1", options, null);
	}

	public void onTabChange(TabChangeEvent event) {
		FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab: " + event.getTab().getTitle());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onTabClose(TabCloseEvent event) {
		FacesMessage msg = new FacesMessage("Tab Closed", "Closed tab: " + event.getTab().getTitle());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onReturnFromLevel1(SelectEvent event) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Data Returned", event.getObject().toString()));
	}

	private static LocalDate convertToLocalDate(Date utilDate) {
		// Convert java.util.Date to java.time.Instant
		Instant instant = utilDate.toInstant();

		// Convert Instant to java.time.LocalDate
		LocalDate localDate = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate();

		return localDate;
	}

	public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}

}
