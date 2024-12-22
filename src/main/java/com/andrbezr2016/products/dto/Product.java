package com.andrbezr2016.products.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
@ToString
public class Product {

    private UUID id;
    private String name;
    private ProductType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private UUID tariff;
    private Long tariffVersion;
    private UUID author;
    private Long version;

    public enum ProductType {
        LOAN, CARD
    }
}
