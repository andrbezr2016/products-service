package com.andrbezr2016.products.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class ProductNotification {

    private UUID tariff;
    private Long tariffVersion;
    private UUID product;
    private LocalDateTime startDate;
}
