/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.inops.visitorpass.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.ResponsiveOption;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.donut.DonutChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.animation.Animation;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.optionconfig.tooltip.Tooltip;
import org.primefaces.model.dashboard.DashboardModel;
import org.primefaces.model.dashboard.DashboardWidget;
import org.primefaces.model.dashboard.DefaultDashboardModel;
import org.primefaces.model.dashboard.DefaultDashboardWidget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import javax.enterprise.context.RequestScoped;

import com.inops.visitorpass.entity.Employee;
import com.inops.visitorpass.entity.LeaveBalance;
import com.inops.visitorpass.entity.Muster;
import com.inops.visitorpass.entity.ScheduleEvent;
import com.inops.visitorpass.entity.User;
import com.inops.visitorpass.repository.ScheduleEventRepository;
import com.inops.visitorpass.service.ILeaveBalance;
import com.inops.visitorpass.service.IMuster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
@Component("dashboardController")
@Scope("session")
@RequiredArgsConstructor
@RequestScoped
public class DashboardController implements Serializable {

	@Autowired
	ApplicationContext ctx;

	private final ILeaveBalance leaveBalanceService;
	private final IMuster musterService;
	private final ScheduleEventRepository scheduleEventRepository;

	private List<LeaveBalance> leaveBalances;
	private ScheduleEvent selectedScheduleEvent;
	private List<Muster> musters;
	private List<ResponsiveOption> responsiveOptions;
	private List<Employee> employees;
	private int presentCount, absentCount, lateCount, earlyCount;

	private static final long serialVersionUID = 1L;
	private static final String RESPONSIVE_CLASS = "col-12 lg:col-6 xl:col-6";

	private DashboardModel responsiveModel;
	ZoneId defaultZoneId = ZoneId.systemDefault();

	private BarChartModel barModel;
	private DonutChartModel donutModel;
	private LineChartModel lineModel;
	private BarChartModel stackedBarModel;
	private User user;
	private List<ScheduleEvent> scheduleEvents, approvalEvents, registeredEvents;
	Random random = new Random();
	private String months[] = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
			"October", "November", "December" };

	@PostConstruct
	public void init() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		user = (User) auth.getPrincipal();

		approvalEvents = scheduleEventRepository
				.findAllByEventStatusAndFromDateGreaterThan("Published", LocalDateTime.now())		
		  .stream() .filter(event -> event.getRegistered().stream() .anyMatch(reg ->
		  reg.getUserId().equals(user.getEmail()))) .collect(Collectors.toList());
		 

		Employee employee = user.getEmployee();
		employees = ((Optional<List<Employee>>) ctx.getBean("getEmployees")).get();
		leaveBalances = leaveBalanceService.findAllByEmployeeId(LocalDate.of(LocalDate.now().getYear(), 1, 1),
				LocalDate.of(LocalDate.now().getYear(), 12, 31), employee.getEmployeeId()).get();

		getTodaysCount();
		leaveCountLineModel(employee);
		presentCountBarModel(employee);
		createStackedBarModel(employee);

		responsiveOptions = new ArrayList<>();
		responsiveOptions.add(new ResponsiveOption("1024px", 3, 3));
		responsiveOptions.add(new ResponsiveOption("768px", 2, 2));
		responsiveOptions.add(new ResponsiveOption("560px", 1, 1));

		responsiveModel = new DefaultDashboardModel();
		responsiveModel.addWidget(new DefaultDashboardWidget("bar", RESPONSIVE_CLASS));
		responsiveModel.addWidget(new DefaultDashboardWidget("stacked", RESPONSIVE_CLASS));
		responsiveModel.addWidget(
				new DefaultDashboardWidget("donut", RESPONSIVE_CLASS.replaceFirst("xl:col-\\d+", "xl:col-4")));
		responsiveModel.addWidget(
				new DefaultDashboardWidget("cartesian", RESPONSIVE_CLASS.replaceFirst("xl:col-\\d+", "xl:col-8")));

	}

	public void createStackedBarModel(Employee employee) {

		List<Object[]> lateCount = musterService.countAllLateDaysByEmployeeId(employee.getEmployeeId(), 2019).get();// LocalDate.now().getYear()).get();

		List<Object[]> earlyCount = musterService.countAllEarlyDaysByEmployeeId(employee.getEmployeeId(), 2019).get();// LocalDate.now().getYear()).get();

		List<Object[]> extraCount = musterService.countAllExtraDaysByEmployeeId(employee.getEmployeeId(), 2019).get();// LocalDate.now().getYear()).get();

		stackedBarModel = new BarChartModel();
		ChartData data = new ChartData();

		BarChartDataSet barDataSet = new BarChartDataSet();
		barDataSet.setLabel("Canceled 1");
		barDataSet.setBackgroundColor("rgb(255, 99, 132)");
		List<Number> dataVal = new ArrayList<>();
		lateCount.forEach(count -> {
			dataVal.add((Number) count[0]);
		});

		barDataSet.setData(dataVal);

		BarChartDataSet barDataSet2 = new BarChartDataSet();
		barDataSet2.setLabel("Re-Scheduled 2");
		barDataSet2.setBackgroundColor("rgb(54, 162, 235)");
		List<Number> dataVal2 = new ArrayList<>();
		earlyCount.forEach(count -> {
			dataVal2.add((Number) count[0]);
		});

		barDataSet2.setData(dataVal2);

		BarChartDataSet barDataSet3 = new BarChartDataSet();
		barDataSet3.setLabel("Completed 3");
		barDataSet3.setBackgroundColor("rgb(75, 192, 192)");
		List<Number> dataVal3 = new ArrayList<>();
		extraCount.forEach(count -> {
			dataVal3.add((Number) count[0]);
		});

		barDataSet3.setData(dataVal3);

		data.addChartDataSet(barDataSet);
		data.addChartDataSet(barDataSet2);
		data.addChartDataSet(barDataSet3);

		List<String> labels = new ArrayList<>();
		labels.add("January");
		labels.add("February");
		labels.add("March");
		labels.add("April");
		labels.add("May");
		labels.add("June");
		labels.add("August");
		labels.add("September");
		labels.add("October");
		labels.add("November");
		labels.add("December");
		data.setLabels(labels);
		stackedBarModel.setData(data);

		// Options
		BarChartOptions options = new BarChartOptions();
		options.setMaintainAspectRatio(false);
		CartesianScales cScales = new CartesianScales();
		CartesianLinearAxes linearAxes = new CartesianLinearAxes();
		linearAxes.setStacked(true);
		linearAxes.setOffset(true);
		cScales.addXAxesData(linearAxes);
		cScales.addYAxesData(linearAxes);
		options.setScales(cScales);

		Title title = new Title();
		title.setDisplay(true);
		title.setText("Canceled,Pending Approval & Completed");
		options.setTitle(title);

		Tooltip tooltip = new Tooltip();
		tooltip.setMode("index");
		tooltip.setIntersect(false);
		options.setTooltip(tooltip);

		stackedBarModel.setOptions(options);
	}

	private void getTodaysCount() {
		//musters = musterService.findByAttendanceDateBetween(LocalDate.now(), LocalDate.now()).get();
		presentCount = 10;/*
						 * musters.stream().filter(attId -> !attId.getAttendanceId().equals("AA") &&
						 * !attId.getAttendanceId().equals("HH") &&
						 * !attId.getAttendanceId().equals("WW")) .collect(Collectors.toList()).size();
						 */
		absentCount = 2;//employees.size() - presentCount;
		lateCount = 0;//musters.stream().filter(late -> late.getLatePunch() > '0').collect(Collectors.toList()).size();
		earlyCount = 6;//musters.stream().filter(early -> early.getEarlyOut() > '0').collect(Collectors.toList()).size();

		donutModel = new DonutChartModel();
		ChartData data = new ChartData();
		DonutChartOptions options = new DonutChartOptions();
		options.setMaintainAspectRatio(false);
		donutModel.setOptions(options);

		DonutChartDataSet dataSet = new DonutChartDataSet();
		List<Number> values = new ArrayList<>();
		values.add(presentCount);
		values.add(absentCount);
		values.add(lateCount);
		values.add(earlyCount);
		dataSet.setData(values);

		List<String> bgColors = new ArrayList<>();
		bgColors.add("rgb(205, 255, 99)");
		bgColors.add("rgb(255, 99, 132)");
		bgColors.add("rgb(255, 205, 86)");
		bgColors.add("rgb(54, 162, 235)");
		dataSet.setBackgroundColor(bgColors);

		data.addChartDataSet(dataSet);
		List<String> labels = new ArrayList<>();
		labels.add("Present");
		labels.add("Absent");
		labels.add("Late");
		labels.add("Early");
		data.setLabels(labels);

		donutModel.setData(data);
	}

	public void leaveCountLineModel(Employee employee) {

		//List<Object[]> leaveCount = musterService.countAllLeaveDaysByEmployeeId(employee.getEmployeeId(), 2019).get();// LocalDate.now().getYear()).get();

		lineModel = new LineChartModel();
		ChartData data = new ChartData();

		LineChartDataSet dataSet = new LineChartDataSet();
		List<Object> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		/*
		 * leaveCount.forEach(count -> { values.add((Number) count[0]);
		 * labels.add(months[(int) count[1] - 1]); });
		 */
		
		int z =3;
		for(int y=0;y<6;y++)
		{
        	 int x = random.nextInt(5) + 1;
        	 values.add((Number)x);
        	 
 			labels.add(months[(int) z++]);
         }
		
		
		dataSet.setData(values);
		dataSet.setFill(false);
		dataSet.setLabel("Future Events count");
		dataSet.setBorderColor("rgb(75, 192, 192)");
		dataSet.setTension(0.1);
		data.addChartDataSet(dataSet);

		data.setLabels(labels);

		// Options
		LineChartOptions options = new LineChartOptions();
		options.setMaintainAspectRatio(false);
		Title title = new Title();
		title.setDisplay(true);
		title.setText("Future Events Chart");
		options.setTitle(title);

		Title subtitle = new Title();
		subtitle.setDisplay(true);
		subtitle.setText("Monthly Leave Availed");
		options.setSubtitle(subtitle);

		lineModel.setOptions(options);
		lineModel.setData(data);
	}

	protected void presentCountBarModel(Employee employee) {
		List<Object[]> presentCount = musterService.countAllPresentDaysByEmployeeId(employee.getEmployeeId(), 2019)
				.get();// LocalDate.now().getYear()).get();

		barModel = new BarChartModel();
		ChartData data = new ChartData();

		BarChartDataSet barDataSet = new BarChartDataSet();
		barDataSet.setLabel("My Events Attendance Dataset");

		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		presentCount.forEach(count -> {
			values.add((Number) count[0]);
			labels.add(months[(int) count[1] - 1]);
		});

		barDataSet.setData(values);
		data.setLabels(labels);
		barModel.setData(data);

		List<String> bgColor = new ArrayList<>();
		bgColor.add("rgba(255, 99, 132, 0.2)");
		bgColor.add("rgba(255, 159, 64, 0.2)");
		bgColor.add("rgba(255, 205, 86, 0.2)");
		bgColor.add("rgba(75, 192, 192, 0.2)");
		bgColor.add("rgba(54, 162, 235, 0.2)");
		bgColor.add("rgba(153, 102, 255, 0.2)");
		bgColor.add("rgba(201, 203, 207, 0.2)");
		barDataSet.setBackgroundColor(bgColor);

		List<String> borderColor = new ArrayList<>();
		borderColor.add("rgb(255, 99, 132)");
		borderColor.add("rgb(255, 159, 64)");
		borderColor.add("rgb(255, 205, 86)");
		borderColor.add("rgb(75, 192, 192)");
		borderColor.add("rgb(54, 162, 235)");
		borderColor.add("rgb(153, 102, 255)");
		borderColor.add("rgb(201, 203, 207)");
		barDataSet.setBorderColor(borderColor);
		barDataSet.setBorderWidth(1);

		data.addChartDataSet(barDataSet);

		// Options
		BarChartOptions options = new BarChartOptions();
		options.setMaintainAspectRatio(false);
		CartesianScales cScales = new CartesianScales();
		CartesianLinearAxes linearAxes = new CartesianLinearAxes();
		linearAxes.setOffset(true);
		linearAxes.setBeginAtZero(true);
		CartesianLinearTicks ticks = new CartesianLinearTicks();
		linearAxes.setTicks(ticks);
		cScales.addYAxesData(linearAxes);
		options.setScales(cScales);

		Title title = new Title();
		title.setDisplay(true);
		title.setText("");
		options.setTitle(title);

		Legend legend = new Legend();
		legend.setDisplay(true);
		legend.setPosition("top");
		LegendLabel legendLabels = new LegendLabel();
		legendLabels.setFontStyle("italic");
		legendLabels.setFontColor("#2980B9");
		legendLabels.setFontSize(24);
		legend.setLabels(legendLabels);
		options.setLegend(legend);

		// disable animation
		Animation animation = new Animation();
		animation.setDuration(0);
		options.setAnimation(animation);

		barModel.setOptions(options);
	}

	public void handleReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		String result = String.format("Dragged index: %d, Dropped Index: %d, Widget Index: %d",
				event.getSenderColumnIndex(), event.getColumnIndex(), event.getItemIndex());
		message.setDetail(result);

		addMessage(message);
	}

	public void handleClose(CloseEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Panel Closed",
				"Closed panel ID:'" + event.getComponent().getId() + "'");

		addMessage(message);
	}

	public void handleToggle(ToggleEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Panel Toggled",
				"Toggle panel ID:'" + event.getComponent().getId() + "' Status:" + event.getVisibility().name());

		addMessage(message);
	}

	/**
	 * Dashboard panel has been resized.
	 *
	 * @param widget the DashboardPanel
	 * @param size   the new size CSS
	 */
	public void onDashboardResize(final String widget, final String size) {
		final DashboardWidget dashboard = responsiveModel.getWidget(widget);
		if (dashboard != null) {
			final String newCss = dashboard.getStyleClass().replaceFirst("xl:col-\\d+", size);
			dashboard.setStyleClass(newCss);
		}
	}

	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

}
