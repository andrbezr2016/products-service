package com.andrbezr2016.products.kafka;

import com.andrbezr2016.products.dto.ProductNotification;
import com.andrbezr2016.products.entity.ProductEntity;
import com.andrbezr2016.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecordKafkaListener {

    private final ProductRepository productRepository;

    @Transactional
    @KafkaListener(topics = "notification-topic")
    void listener(ConsumerRecord<Long, ProductNotification> record) {
        log.info("Received notification with id: {}", record.key());
        ProductNotification productNotification = record.value();
        ProductEntity productEntity = productRepository.findById(productNotification.getProduct()).orElse(null);
        if (productEntity != null) {
            productEntity.setTariff(productNotification.getTariff());
            productEntity.setTariffVersion(productNotification.getVersion());
            productRepository.save(productEntity);
        }
    }
}
