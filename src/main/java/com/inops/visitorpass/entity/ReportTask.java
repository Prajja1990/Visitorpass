package com.inops.visitorpass.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
@Table(name = "ReportTask")
public class ReportTask {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reportTaskId;

	@Lob
	private String reports;
	private long fromDay;	
	private long toDay;	
	private long templateId;
	

	@OneToOne
	@JoinColumn(name = "schedulerId")
	private ScheduledTask scheduledTask;

	public void setReportTaskId(Long reportTaskId) {
		this.reportTaskId = reportTaskId;
	}

	public void setReports(String reports) {
		this.reports = reports;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public void setScheduledTask(ScheduledTask scheduledTask) {
		this.scheduledTask = scheduledTask;
	}

	public void setFromDay(long fromDay) {
		this.fromDay = fromDay;
	}

	public void setToDay(long toDay) {
		this.toDay = toDay;
	}
		
}
