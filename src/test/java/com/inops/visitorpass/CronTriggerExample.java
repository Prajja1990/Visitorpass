package com.inops.visitorpass;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

public class CronTriggerExample {

    public static void main(String[] args) throws InterruptedException {
        // Example cron expression (every minute)
        String cronExpression = "0 */5 * * * ?";

        // Create a CronTrigger with the cron expression
        CronTrigger cronTrigger = new CronTrigger(cronExpression);

        // Get the current date and time
        Date now = new Date();
        

        // Create a TriggerContext with the current date and time
        TriggerContext triggerContext = new SimpleTriggerContext(now, now, now);

        // Calculate the next execution time
        Date nextExecutionTime = cronTrigger.nextExecutionTime(triggerContext);

        // Print the result
        System.out.println("Next execution time: " + nextExecutionTime);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
        scheduler.schedule(() -> {
            System.out.println("Shutting down the scheduler");
            //scheduler.shutdown();
        }, 10, TimeUnit.SECONDS);
				
        
        
        Thread.sleep(9000000);
		//scheduledTasks.put(task.getId(), future);
    }
}
