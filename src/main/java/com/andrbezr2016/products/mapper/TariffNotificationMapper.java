package com.andrbezr2016.products.mapper;

import com.andrbezr2016.products.dto.TariffNotification;
import com.andrbezr2016.products.entity.TariffNotificationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TariffNotificationMapper {

    TariffNotification toDto(TariffNotificationEntity tariffNotificationEntity);
}
