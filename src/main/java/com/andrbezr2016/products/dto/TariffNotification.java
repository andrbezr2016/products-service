package com.andrbezr2016.products.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class TariffNotification {

    private UUID tariff;
    private Long tariffVersion;
}
