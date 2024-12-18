package com.andrbezr2016.products.config;

import com.andrbezr2016.products.job.TariffServiceNotificationJob;
import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@RequiredArgsConstructor
@Configuration
public class TariffServiceNotificationJobConfig {

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(TariffServiceNotificationJob.class)
                .storeDurably()
                .withIdentity("TariffServiceNotificationJob")
                .withDescription("Invoke TariffServiceNotificationJob")
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("TariffServiceNotificationJob_Trigger")
                .withDescription("TariffServiceNotificationJob trigger")
                .withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(10))
                .build();
    }
}
