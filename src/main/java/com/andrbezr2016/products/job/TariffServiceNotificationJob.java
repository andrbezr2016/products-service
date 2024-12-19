package com.andrbezr2016.products.job;

import com.andrbezr2016.products.client.TariffsServiceClient;
import com.andrbezr2016.products.entity.TariffNotificationEntity;
import com.andrbezr2016.products.mapper.TariffNotificationMapper;
import com.andrbezr2016.products.repository.TariffNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TariffServiceNotificationJob implements Job {

    private final TariffNotificationRepository tariffNotificationRepository;
    private final TariffsServiceClient tariffsServiceClient;
    private final TariffNotificationMapper tariffNotificationMapper;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Start TariffServiceNotificationJob");
        List<TariffNotificationEntity> tariffNotificationEntityList = tariffNotificationRepository.findAllByProcessedDateIsNull();
        for (TariffNotificationEntity tariffNotificationEntity : tariffNotificationEntityList) {
            log.info("Send notification with id: {}", tariffNotificationEntity.getId());
            tariffsServiceClient.syncTariff(tariffNotificationMapper.toDto(tariffNotificationEntity));
            tariffNotificationEntity.setProcessedDate(OffsetDateTime.now());
            tariffNotificationRepository.save(tariffNotificationEntity);
        }
    }
}
