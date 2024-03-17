package com.inops.visitorpass.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.schedule.ScheduleEntryMoveEvent;
import org.primefaces.event.schedule.ScheduleEntryResizeEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.inops.visitorpass.entity.Employee;
import com.inops.visitorpass.entity.Muster;
import com.inops.visitorpass.entity.RegisteredEvents;
import com.inops.visitorpass.entity.Transaction;
import com.inops.visitorpass.entity.TransactionId;
import com.inops.visitorpass.entity.User;
import com.inops.visitorpass.repository.ScheduleEventRepository;
import com.inops.visitorpass.service.IDailyTransaction;
import com.inops.visitorpass.service.IMuster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
@Component("punchController")
@Scope("session")
@RequiredArgsConstructor
public class PunchController {

	@Autowired
	ApplicationContext ctx;
	private final IDailyTransaction dailyTransactionService;
	private final IMuster musterService;
	private final ScheduleEventRepository scheduleEventRepository;

	private List<Transaction> transactions;
	private List<com.inops.visitorpass.entity.ScheduleEvent> scheduleEvents, approvalEvents, registeredEvents;

	private Transaction transaction;
	private String employeeId;
	private LocalDate startDate;
	private LocalDate endDate;

	private List<Employee> employees;

	private ScheduleModel eventModel;

	private ScheduleEvent<?> event = new DefaultScheduleEvent<>();

	private boolean slotEventOverlap = true;
	private boolean showWeekNumbers = false;
	private boolean showHeader = true;
	private boolean draggable = true;
	private boolean resizable = true;
	private boolean selectable = false;
	private boolean showWeekends = true;
	private boolean tooltip = true;
	private boolean allDaySlot = true;
	private boolean rtl = false;
	private User user;

	ZoneId defaultZoneId = ZoneId.systemDefault();
	private double aspectRatio = Double.MIN_VALUE;
	private String serverTimeZone = ZoneId.systemDefault().toString();
	SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd");

	@PostConstruct
	public void init() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		user = (User) auth.getPrincipal();
		
		registeredEvents = scheduleEventRepository.findAllByEventStatus("Published");
				/*.stream().filter(
				event -> event.getRegistered().stream().anyMatch(usr -> usr.getUserId().equals(user.getEmail())))
				.collect(Collectors.toList());*/
		
		employees = ((Optional<List<Employee>>) ctx.getBean("getEmployees")).get();
		List<Employee> dateOfBirths = employees.stream()
				.filter(emp -> (emp.getDateOfBirth().getDayOfMonth() == LocalDate.now().getDayOfMonth())
						&& (emp.getDateOfBirth().getMonthValue() == LocalDate.now().getMonthValue()))
				.collect(Collectors.toList());

		eventModel = new DefaultScheduleModel();

		/*
		 * DefaultScheduleEvent<?> scheduleEventAllDay = DefaultScheduleEvent.builder()
		 * .title("Birthday's (AllDay) " +
		 * dateOfBirths.stream().map(Employee::getEmployeeName).collect(Collectors.
		 * toList()))
		 * .startDate(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).
		 * withNano(0))
		 * .endDate(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano
		 * (0)).description("")
		 * .backgroundColor("green").borderColor("#27AE60").allDay(true).build();
		 * eventModel.addEvent(scheduleEventAllDay);
		 */
         registeredEvents.forEach(events->{
			
			DefaultScheduleEvent<?> event = DefaultScheduleEvent.builder().data(events).id(events.getId().toString())
					.title("Event Titel : " + events.getEventTitle() + "& Location : " + events.getLocation())
					.startDate((LocalDateTime) events.getFromDate()).endDate((LocalDateTime) events.getToDate())
					.description(events.getBody() )
					.borderColor("orange").build();
			eventModel.addEvent(event);
			
		});
	}

	public void openNew() {
		this.transaction = new Transaction();
	}

	public void searchPunchs() {
		eventModel = new DefaultScheduleModel();
		/*
		 * List<Object[]> transactions = dailyTransactionService
		 * .findMinMaxPunchedTimeByDateRange(employeeId, startDate, endDate).get();
		 * List<Muster> musters =
		 * musterService.findAllByAttendanceDateBetweenAndEmployeeId(startDate, endDate,
		 * employeeId) .get(); transactions.stream().forEach(trans -> { Muster muster =
		 * musters.stream().filter(must ->
		 * must.getMusterId().getAttendanceDate().equals(trans[0]))
		 * .findAny().orElse(null); if (muster != null) { float hours =
		 * (muster.getHoursWorked() / 60); String colour = hours >= 8 ? "green" : hours
		 * >= 4 && hours <= 6 ? "orange" : "red"; DefaultScheduleEvent<?> event =
		 * DefaultScheduleEvent.builder() .title("Attendance : " +
		 * muster.getAttendanceId() + "& Hours Worked : " + hours)
		 * .startDate((LocalDateTime) trans[1]).endDate((LocalDateTime) trans[2])
		 * .description("Employee " + employeeId + " Hours Worked " +
		 * muster.getHoursWorked()) .borderColor(colour).build();
		 * eventModel.addEvent(event); } });
		 */
		registeredEvents.forEach(events->{
			
			DefaultScheduleEvent<?> event = DefaultScheduleEvent.builder().data(events).id(events.getId().toString())
					.title("Event Titel : " + events.getEventTitle() + "& Location : " + events.getLocation())
					.startDate((LocalDateTime) events.getFromDate()).endDate((LocalDateTime) events.getToDate())
					.description(events.getBody() )
					.borderColor("orange").build();
			eventModel.addEvent(event);
			
		});
		

		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "Punch searched successfully for: " + employeeId);

	}

	public void addEvent() {
		/*
		 * if (employeeId == null) { addMessage(FacesMessage.SEVERITY_ERROR,
		 * "Error Message",
		 * "exception at the time of punch addition, Please selesct employeeId  "); }
		 * else { if (event.isAllDay()) { // see
		 * https://github.com/primefaces/primefaces/issues/1164 if
		 * (event.getStartDate().toLocalDate().equals(event.getEndDate().toLocalDate()))
		 * { event.setEndDate(event.getEndDate().plusDays(1)); } }
		 */
		com.inops.visitorpass.entity.ScheduleEvent selectedScheduleEvent = (com.inops.visitorpass.entity.ScheduleEvent) event.getData();
		selectedScheduleEvent.getRegistered().add(new RegisteredEvents(null, user.getEmail(), user.getUsername(), selectedScheduleEvent));
		scheduleEventRepository.save(selectedScheduleEvent);

		
			event = new DefaultScheduleEvent<>();
			addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "Event downloaded successfully for: " + selectedScheduleEvent.getEventTitle());
		
	}

	public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
		event = DefaultScheduleEvent.builder().startDate(selectEvent.getObject())
				.endDate(selectEvent.getObject().plusHours(1)).build();
	}

	public void onEventSelect(SelectEvent<ScheduleEvent<?>> selectEvent) {
		event = selectEvent.getObject();
	}

	public void onEventMove(ScheduleEntryMoveEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved",
				"Delta:" + event.getDeltaAsDuration());

		addMessage(message);
	}

	public void onEventResize(ScheduleEntryResizeEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized",
				"Start-Delta:" + event.getDeltaStartAsDuration() + ", End-Delta: " + event.getDeltaEndAsDuration());

		addMessage(message);
	}

	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}

}
