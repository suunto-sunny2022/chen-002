package com.chen.framework.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class HeartbeatJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        LOGGER.info("Quartz heartbeat executed at {}", LocalDateTime.now());
    }
}
