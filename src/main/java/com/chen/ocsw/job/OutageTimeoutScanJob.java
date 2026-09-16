package com.chen.ocsw.job;

import com.chen.ocsw.service.OcswWorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutageTimeoutScanJob implements Job {
    private final OcswWorkflowService workflowService;

    @Override
    public void execute(JobExecutionContext context) {
        int expired = workflowService.expireOutageCommands(LocalDateTime.now());
        log.info("OCSW outage timeout scan completed, expired={}", expired);
    }
}
