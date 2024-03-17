package com.inops.visitorpass.service.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inops.visitorpass.domain.Punch;
import com.inops.visitorpass.entity.Employee;
import com.inops.visitorpass.entity.Transaction;
import com.inops.visitorpass.repository.ScheduleEventRepository;
import com.inops.visitorpass.service.DataExtractionService;
import com.inops.visitorpass.service.IDailyTransaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service("allPunch")
@RequiredArgsConstructor
public class AllPunchReport implements DataExtractionService {

	private final IDailyTransaction dailyTransactionService;
	private final ScheduleEventRepository scheduleEventRepository;
	ZoneId defaultZoneId = ZoneId.systemDefault();

	@Override
	public Collection<Punch> dataExtraction(LocalDate from, LocalDate to, List<Employee> employeeIds, String type) {
		/*
		 * Optional<List<Transaction>> dailyTransaction = dailyTransactionService
		 * .findAllByAttendanceDateBetweenAndTransactionIdEmployeeIdIn(from, to,
		 * employeeIds.stream().map(Employee::getEmployeeId).collect(Collectors.toList()
		 * ));
		 * log.info("Data of AllPunches extracted successfully from DailyTransactions "
		 * );
		 * 
		 * return dailyTransaction.get().stream().map(trans -> { Employee employee =
		 * employeeIds.stream() .filter(empId ->
		 * trans.getTransactionId().getEmployeeId().equals(empId.getEmployeeId())).
		 * findAny() .orElse(null); return new Punch(employee.getEmployeeName(),
		 * employee.getDepartment().getDepartmentName(), employee.getEmployeeId(),
		 * trans.getAttendanceDate(),
		 * Date.from(trans.getTransactionId().getTransactionTime().atZone(ZoneId.
		 * systemDefault()).toInstant()), trans.getActualIOFlag());
		 * }).collect(Collectors.toList());
		 */

		return scheduleEventRepository.findAll().stream()
				.map(events -> new Punch(events.getEventTitle(), events.getOpenClosed(), events.getEventStatus(),
						convertToDate(events.getFromDate()), convertToDate(events.getToDate()),
						events.getRejectionComments()))
				.collect(Collectors.toList());

	}

	public static Date convertToDate(LocalDateTime localDateTime) {
		// Convert LocalDateTime to Instant
		// An Instant represents a moment on the timeline in UTC
		// Use ZoneId.systemDefault() to use the system's default time zone
		// Alternatively, you can specify a specific time zone using ZoneId.of("ZoneId")
		// Here, we're using the system's default time zone
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

}
