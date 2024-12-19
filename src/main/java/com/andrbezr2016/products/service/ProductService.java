package com.andrbezr2016.products.service;

import com.andrbezr2016.products.dto.Product;
import com.andrbezr2016.products.dto.ProductNotification;
import com.andrbezr2016.products.dto.ProductRequest;
import com.andrbezr2016.products.entity.ProductEntity;
import com.andrbezr2016.products.mapper.ProductMapper;
import com.andrbezr2016.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Product getCurrentVersion(UUID id) {
        ProductEntity productEntity = productRepository.findById(id).orElse(null);
        return productMapper.toDto(productEntity);
    }

    public Collection<Product> getPreviousVersions(UUID id) {
        return Collections.emptyList();
    }

    public Product getVersionForDate(UUID id, OffsetDateTime date) {
        return null;
    }

    public Product createProduct(ProductRequest productRequest) {
        ProductEntity productEntity = productRepository.save(productMapper.toEntity(productRequest));
        return productMapper.toDto(productEntity);
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    public Product rollBackVersion(UUID id) {
        return null;
    }

    @Transactional
    public void syncTariff(ProductNotification productNotification) {
        ProductEntity productEntity = productRepository.findById(productNotification.getProduct()).orElse(null);
        if (syncNeeded(productEntity, productNotification)) {
            productEntity.setTariff(productNotification.getTariff());
            productEntity.setTariffVersion(productNotification.getVersion());
            productRepository.save(productEntity);
        }
    }

    private boolean syncNeeded(ProductEntity productEntity, ProductNotification productNotification) {
        return productEntity != null
                && (!Objects.equals(productEntity.getTariff(), productNotification.getTariff())
                || !Objects.equals(productEntity.getTariffVersion(), productNotification.getVersion()));
    }
}
