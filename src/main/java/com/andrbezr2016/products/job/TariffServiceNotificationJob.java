package com.andrbezr2016.products.job;

import com.andrbezr2016.products.client.TariffsServiceClient;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TariffServiceNotificationJob implements Job {

    private final TariffsServiceClient tariffsServiceClient;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }
}
