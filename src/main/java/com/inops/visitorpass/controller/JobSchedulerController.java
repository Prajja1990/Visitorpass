package com.inops.visitorpass.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inops.visitorpass.entity.ScheduledTask;
import com.inops.visitorpass.service.IScheduledTask;
import com.inops.visitorpass.service.job.IScheduled;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
@Component("jobSchedulerController")
@Scope("session")
@RequiredArgsConstructor
public class JobSchedulerController {

	private final IScheduledTask schedulerTaskService;
	private final IScheduled scheduledTaskManager;

	private ScheduledTask selectedScheduledTask;
	private List<ScheduledTask> selectedScheduledTasks;
	private List<ScheduledTask> scheduledTasks;

	@PostConstruct
	public void init() {
		scheduledTasks = schedulerTaskService.findAll().get();
	}

	public void openNew() {
		this.selectedScheduledTask = new ScheduledTask();
	}

	public void saveScheduledTask() {
		selectedScheduledTask.setCronExpression(generateCronExpression(selectedScheduledTask));
		selectedScheduledTask.setActive(true);
		try {
			if (this.selectedScheduledTask.getId() == 0L) {
				schedulerTaskService.save(selectedScheduledTask);
				this.scheduledTasks.add(selectedScheduledTask);
				scheduledTaskManager.startTask(selectedScheduledTask);
				addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "ScheduledTask Added successfully");

			} else {
				
				schedulerTaskService.save(selectedScheduledTask);
				scheduledTaskManager.cancelTask(selectedScheduledTask.getId());
				scheduledTaskManager.startTask(selectedScheduledTask);
				addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "ScheduledTask updated successfully");
			}
			
		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", e.getMessage());
		}
	}

	public void deleteScheduledTask() {

		schedulerTaskService.delete(selectedScheduledTask);
		this.scheduledTasks.remove(this.selectedScheduledTask);
		if (this.selectedScheduledTasks != null) {
			this.selectedScheduledTasks.remove(this.selectedScheduledTask);
		}
		this.selectedScheduledTask = null;
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "ScheduledTask deleted successfully");
	}

	public void deleteScheduledTasks() {
		schedulerTaskService.deleteAll(this.selectedScheduledTasks);
		this.scheduledTasks.removeAll(this.selectedScheduledTasks);
		this.selectedScheduledTasks = null;
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "ScheduledTask deleted successfully");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
	}

	public String getDeleteScheduledTasksButtonMessage() {
		if (hasSelectedScheduledTasks()) {
			int size = this.selectedScheduledTasks.size();
			return size > 1 ? size + " divisions selected" : "1 divisions selected";
		}

		return "Delete";
	}

	public boolean hasSelectedScheduledTasks() {
		return this.selectedScheduledTasks != null && !this.selectedScheduledTasks.isEmpty();
	}
	
	public void pauseScheduledTask()
	{
		try {
			if (this.selectedScheduledTask.getId() == 0L) {				
				addMessage(FacesMessage.SEVERITY_ERROR, "Info Message", "ScheduledTask Not Fount to Pause");

			} else {
				selectedScheduledTask.setActive(false);
				schedulerTaskService.save(selectedScheduledTask);
				scheduledTaskManager.pauseTask(selectedScheduledTask.getId());
				addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "ScheduledTask Paused successfully");
			}
			
		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", e.getMessage());
		}
	}
	
	public void resumeScheduledTask() {
		try {
			if (this.selectedScheduledTask.getId() == 0L) {				
				addMessage(FacesMessage.SEVERITY_ERROR, "Info Message", "ScheduledTask Not Fount to Pause");

			} else {
				selectedScheduledTask.setActive(true);
				schedulerTaskService.save(selectedScheduledTask);
				scheduledTaskManager.resumeTask(selectedScheduledTask.getId());
				addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "ScheduledTask Resumed successfully");
			}
			
		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", e.getMessage());
		}
	}
	
	public void cancelScheduledTask() {
		try {
			if (this.selectedScheduledTask.getId() == 0L) {				
				addMessage(FacesMessage.SEVERITY_ERROR, "Info Message", "ScheduledTask Not Fount to Pause");

			} else {
				selectedScheduledTask.setActive(false);
				schedulerTaskService.save(selectedScheduledTask);
				scheduledTaskManager.cancelTask(selectedScheduledTask.getId());
				addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "ScheduledTask Canceled successfully");
			}
			
		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", e.getMessage());
		}
	}

	private String generateCronExpression(ScheduledTask selectedScheduledTask) {
		try {
			// Generate cron expression format: "0 <minutes> <hours> <days of month> * ?"
			String hours = selectedScheduledTask.getMinutes();
			if (selectedScheduledTask.getEveryMinutes() != null) {
				hours = selectedScheduledTask.getEveryMinutes() + selectedScheduledTask.getMinutes();
			}
			return String.format("0 %S %S %S %S %S", hours,
					selectedScheduledTask.getHours().equals("0") ? "*" : selectedScheduledTask.getHours(),
					selectedScheduledTask.getDayOfMonth().equals("0") ? "*" : selectedScheduledTask.getDayOfMonth(),
					selectedScheduledTask.getMonth(), selectedScheduledTask.getDayOfWeek());
		} catch (Exception e) {
			// Handle exception if values are out of range
			System.err.println("Invalid values for cron expression.");
			return null;
		}
	}

	public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}

}