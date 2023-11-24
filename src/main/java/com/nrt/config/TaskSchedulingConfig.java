package com.nrt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nrt.scheduler.CheckOutOfStockScheduler;
import com.nrt.scheduler.EmailTaskScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class TaskSchedulingConfig {

	private final EmailTaskScheduler emailScheduler;

	//private final CheckOutOfStockScheduler checkOutOfStockScheduler;

	public TaskSchedulingConfig(EmailTaskScheduler emailScheduler, CheckOutOfStockScheduler checkOutOfStockScheduler) {
		this.emailScheduler = emailScheduler;
		//this.checkOutOfStockScheduler = checkOutOfStockScheduler;
	}

	@Bean(destroyMethod = "shutdown")
	ScheduledExecutorService scheduledExecutorService() {
		return Executors.newScheduledThreadPool(2); // Create a double-threaded scheduler
	}

	public void scheduleTask(ScheduledExecutorService executor) {
		executor.scheduleAtFixedRate(emailScheduler, 0, 1, TimeUnit.HOURS); // Execute every 1 hour
	//	executor.scheduleAtFixedRate(checkOutOfStockScheduler, 0, 3000, TimeUnit.MILLISECONDS);
	}
}
