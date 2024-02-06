package com.inops.visitorpass.service.job;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.jfree.util.Log;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.inops.visitorpass.entity.ScheduledTask;
import com.inops.visitorpass.service.IScheduledTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service("scheduledTaskManager")
@RequiredArgsConstructor
@Log4j2
public class ScheduledTaskManager implements IScheduled {

	private final IScheduledTask schedulerTaskService;
	private final ApplicationContext ctx;
	private final IJobService jobService;
	// private final TaskScheduler taskScheduler;

	private Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

	@Override
	public void scheduleTask(ScheduledTask task) {

		if (task.isActive()) {
			try {
				TaskScheduler taskScheduler = ctx.getBean(TaskScheduler.class);
				ScheduledFuture<?> future = taskScheduler.schedule(() -> {
					Date now = new Date();
					Log.info(Thread.currentThread().getName() + " The Task1 executed at " + now + " and task "
							+ task.getTaskName());
					IJob job = jobService.getJob(task.getTaskType());
					job.execute(now, now , task);
					task.setLastActualExecutionTime(now);
					task.setLastCompletionTime(now);
					task.setLastScheduledExecutionTime(now);
					schedulerTaskService.save(task);

				}, new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						String cronExp = task.getCronExpression();// "0/5 * * * * ?";
						return new CronTrigger(cronExp).nextExecutionTime(triggerContext);
					}
				});

				scheduledTasks.put(task.getId(), future);

			} catch (Exception e) {
				Log.error(e);
			}
		}
	}

	@Override
	public void pauseTask(long taskId) {
		ScheduledTask task = schedulerTaskService.findById(taskId).orElse(null);
		if (task != null && scheduledTasks.containsKey(taskId)) {
			task.setActive(false);
			schedulerTaskService.save(task);
			ScheduledFuture<?> future = scheduledTasks.get(taskId);
			future.cancel(false);
			scheduledTasks.remove(taskId);
		}

	}

	@Override
	public void cancelTask(long taskId) {
		ScheduledTask task = schedulerTaskService.findById(taskId).orElse(null);
		if (task != null && scheduledTasks.containsKey(taskId)) {
			task.setActive(false);
			schedulerTaskService.save(task);
			ScheduledFuture<?> future = scheduledTasks.get(taskId);
			future.cancel(true);
			scheduledTasks.remove(taskId);
		}
	}

	@Override
	public void startTask(ScheduledTask scheduledTask) {
		scheduleTask(scheduledTask);
	}

	@Override
	public void resumeTask(long taskId) {
		ScheduledTask task = schedulerTaskService.findById(taskId).orElse(null);
		if (task != null && !scheduledTasks.containsKey(taskId)) {
			task.setActive(true);
			schedulerTaskService.save(task);
			scheduleTask(task);
		}
	}

	@Override
	public void scheduleAll() {
		List<ScheduledTask> tasks = schedulerTaskService.findAll().get();
		tasks.forEach(task -> {
			scheduleTask(task);
		});
	}
}
