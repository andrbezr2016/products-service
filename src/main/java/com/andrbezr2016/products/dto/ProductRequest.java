package com.andrbezr2016.products.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Setter
@Getter
@Builder
@ToString
public class ProductRequest {

    private String name;
    private Product.ProductType type;
    private String description;
    private UUID author;
}
