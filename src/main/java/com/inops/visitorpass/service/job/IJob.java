package com.inops.visitorpass.service.job;

import java.util.Date;

import com.inops.visitorpass.entity.ScheduledTask;

interface IJob {

	public void execute(Date from, Date to, ScheduledTask task);
}
