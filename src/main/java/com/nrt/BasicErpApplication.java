package com.nrt;

import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.nrt.config.TaskSchedulingConfig;

@SpringBootApplication
public class BasicErpApplication {
	private static final Logger log = LoggerFactory.getLogger(BasicErpApplication.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(BasicErpApplication.class, args);
		TaskSchedulingConfig schedulingConfig = context.getBean(TaskSchedulingConfig.class);
		schedulingConfig.scheduleTask(context.getBean(ScheduledExecutorService.class));
		log.info("SuccessFully  Start Application...... ");
	}

}
