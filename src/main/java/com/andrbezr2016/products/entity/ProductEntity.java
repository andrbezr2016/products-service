package com.andrbezr2016.products.entity;

import com.andrbezr2016.products.dto.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@Entity
@IdClass(ProductId.class)
@Table(name = "products")
public class ProductEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Product.ProductType type;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Column(name = "description")
    private String description;
    @Column(name = "tariff")
    private UUID tariff;
    @Column(name = "tariff_version")
    private Long tariffVersion;
    @Column(name = "author", nullable = false)
    private UUID author;
    @Id
    @Column(name = "version", nullable = false)
    private Long version;
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state;

    public enum State {
        ACTIVE, INACTIVE, DELETED
    }
}
