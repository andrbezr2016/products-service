package com.andrbezr2016.products.dto;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class ProductRequest {

    private String name;
    private Product.ProductType type;
    private String description;
    private UUID author;
}
