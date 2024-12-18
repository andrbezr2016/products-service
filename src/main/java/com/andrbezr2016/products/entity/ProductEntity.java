package com.andrbezr2016.products.entity;

import com.andrbezr2016.products.dto.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@Audited
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "name")
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Product.ProductType type;
    @Column(name = "start_date")
    private OffsetDateTime startDate;
    @Column(name = "end_date")
    private OffsetDateTime endDate;
    @Column(name = "description")
    private String description;
    @Column(name = "tariff")
    private UUID tariff;
    @Column(name = "tariff_version")
    private Long tariffVersion;
    @Column(name = "author")
    private UUID author;
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
