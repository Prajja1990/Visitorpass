package com.inops.visitorpass.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
//@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ScheduledTask")
public class ScheduledTask {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String taskName;
	private String taskType;
	private String cronExpression;
	private boolean active;
	private Date lastScheduledExecutionTime;
	private Date lastActualExecutionTime;
	private Date lastCompletionTime;

	@Transient
	private String everyMinutes;
	@Transient
	private String minutes;
	@Transient
	private String hours;
	@Transient
	private String dayOfMonth;
	@Transient
	private String month;
	@Transient
	private String dayOfWeek;

	@OneToOne(mappedBy = "scheduledTask", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private ReportTask reportTask = new ReportTask();

	public void setId(long id) {
		this.id = id;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setLastScheduledExecutionTime(Date lastScheduledExecutionTime) {
		this.lastScheduledExecutionTime = lastScheduledExecutionTime;
	}

	public void setLastActualExecutionTime(Date lastActualExecutionTime) {
		this.lastActualExecutionTime = lastActualExecutionTime;
	}

	public void setLastCompletionTime(Date lastCompletionTime) {
		this.lastCompletionTime = lastCompletionTime;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public void setEveryMinutes(String everyMinutes) {
		this.everyMinutes = everyMinutes;
	}

	public void setReportTask(ReportTask reportTask) {
		this.reportTask = reportTask;
	}
	
	

}
