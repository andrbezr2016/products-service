package com.andrbezr2016.products.service;

import com.andrbezr2016.products.dto.Product;
import com.andrbezr2016.products.dto.ProductNotification;
import com.andrbezr2016.products.dto.ProductRequest;
import com.andrbezr2016.products.entity.ProductEntity;
import com.andrbezr2016.products.entity.ProductId;
import com.andrbezr2016.products.mapper.ProductMapper;
import com.andrbezr2016.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Product getCurrentVersion(UUID id) {
        ProductEntity productEntity = productRepository.findCurrentVersionById(id).orElse(null);
        return productMapper.toDto(productEntity);
    }

    public Collection<Product> getPreviousVersions(UUID id) {
        Collection<ProductEntity> productEntityList = productRepository.findAllPreviousVersionsById(id);
        return productMapper.toDtoCollection(productEntityList);
    }

    public Product getVersionForDate(UUID id, OffsetDateTime date) {
        ProductEntity productEntity = productRepository.findVersionForDateById(id, date).orElse(null);
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public Product createProduct(ProductRequest productRequest) {
        ProductEntity productEntity = productMapper.toEntity(productRequest);
        productEntity.setStartDate(OffsetDateTime.now());
        productEntity = productRepository.save(productEntity);
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        productRepository.findCurrentVersionById(id).ifPresent(productEntity -> productRepository.deleteAllVersionsById(id));
    }

    @Transactional
    public Product rollBackVersion(UUID id) {
        ProductEntity productEntity = productRepository.findCurrentVersionById(id).orElse(null);
        if (productEntity != null) {
            ProductId productId = new ProductId();
            productId.setId(productEntity.getId());
            productId.setVersion(productEntity.getVersion());
            productRepository.deleteById(productId);
        }
        productEntity = productRepository.findCurrentVersionById(id).orElse(null);
        if (productEntity != null) {
            productEntity.setEndDate(null);
            productRepository.save(productEntity);
        }
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public void syncTariff(ProductNotification productNotification) {
        ProductEntity productEntity = productRepository.findCurrentVersionById(productNotification.getProduct()).orElse(null);
        if (syncNeeded(productEntity, productNotification)) {
            OffsetDateTime now = OffsetDateTime.now();
            productEntity.setEndDate(now);
            productEntity = productRepository.save(productEntity);
            productEntity.setTariff(productNotification.getTariff());
            productEntity.setTariffVersion(productNotification.getTariffVersion());
            productEntity.setStartDate(now);
            productEntity.setEndDate(null);
            productEntity.setVersion(productEntity.getVersion() + 1);
            productRepository.save(productEntity);
        }
    }

    private boolean syncNeeded(ProductEntity productEntity, ProductNotification productNotification) {
        return productEntity != null
                && (!Objects.equals(productEntity.getTariff(), productNotification.getTariff())
                || !Objects.equals(productEntity.getTariffVersion(), productNotification.getTariffVersion()));
    }
}
