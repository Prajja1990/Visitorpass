package com.inops.visitorpass.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inops.visitorpass.entity.Employee;
import com.inops.visitorpass.entity.Muster;
import com.inops.visitorpass.service.IEmployee;
import com.inops.visitorpass.service.IMuster;

import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
public class ComputeController {

	Logger logger = LoggerFactory.getLogger(ComputeController.class);

	private final IMuster musterService;
	private final IEmployee employeeService;


	@GetMapping(path = "api/getMuster")
	public List<Muster> getMuster(@RequestParam String fromDate, @RequestParam String toDate) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

		LocalDate fromD = LocalDate.parse(fromDate, formatter);
		LocalDate toD = LocalDate.parse(toDate, formatter);
		List<Muster> musters = new ArrayList<>();
		List<Employee> employees = employeeService.findAll().get();

		logger.info("getMuster started at {}", LocalDate.now().toString());
		List<Muster> muster = musterService.findByAttendanceDateBetween(fromD.minusDays(1), toD.plusDays(1)).get();
		try {
			if (muster != null) {

				muster.forEach(must -> {
					if (must.getMusterId().getEmployeeId() != null) {
						Employee employee = employees.stream()
								.filter(emp -> emp.getEmployeeId().equals(must.getMusterId().getEmployeeId())).findAny()
								.orElse(null);
						if (employee != null) {
							// logger.info("Muster data {}", must.toString());
							must.setAttendanceDate(fromD);
							must.setEmpName(employee.getEmployeeName());
							must.setCdare(employee.getCadre().getCadre());
							must.setDepartment(employee.getDepartment().getDepartmentName());
							musters.add(must);
							logger.info("Muster data {}", must.toString());
						}
					}
				});

			}
		} catch (Exception e) {
			logger.error(e.toString());
		}

		logger.info("getMuster ended at {}", LocalDate.now().toString());
		return musters;
	}
}
