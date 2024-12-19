package com.andrbezr2016.products.repository;

import com.andrbezr2016.products.entity.TariffNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TariffNotificationRepository extends JpaRepository<TariffNotificationEntity, Long> {

    @Query(value = "FROM TariffNotificationEntity WHERE processedDate IS NULL ORDER BY id ASC")
    List<TariffNotificationEntity> findAllByProcessedDateIsNull();
}
