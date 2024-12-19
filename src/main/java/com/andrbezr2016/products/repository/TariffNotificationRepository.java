package com.andrbezr2016.products.repository;

import com.andrbezr2016.products.entity.TariffNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffNotificationRepository extends JpaRepository<TariffNotificationEntity, Long> {
}
