package com.chen.config;

import com.chen.framework.quartz.HeartbeatJob;
import com.chen.ocsw.job.OutageTimeoutScanJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail heartbeatJobDetail() {
        return JobBuilder.newJob(HeartbeatJob.class)
            .withIdentity("heartbeatJob")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger heartbeatTrigger(JobDetail heartbeatJobDetail) {
        return TriggerBuilder.newTrigger()
            .forJob(heartbeatJobDetail)
            .withIdentity("heartbeatTrigger")
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(30)
                .repeatForever())
            .build();
    }

    @Bean
    public JobDetail outageTimeoutScanJobDetail() {
        return JobBuilder.newJob(OutageTimeoutScanJob.class)
            .withIdentity("outageTimeoutScanJob")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger outageTimeoutScanTrigger(JobDetail outageTimeoutScanJobDetail) {
        return TriggerBuilder.newTrigger()
            .forJob(outageTimeoutScanJobDetail)
            .withIdentity("outageTimeoutScanTrigger")
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(1)
                .repeatForever())
            .build();
    }
}
