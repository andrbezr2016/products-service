package com.andrbezr2016.products.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductId {

    private UUID id;
    private Long version;
}
