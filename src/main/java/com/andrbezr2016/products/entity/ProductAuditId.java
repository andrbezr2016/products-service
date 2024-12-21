package com.andrbezr2016.products.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductAuditId {

    private UUID id;
    private Long rev;
}
