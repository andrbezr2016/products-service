package com.andrbezr2016.products.service;

import com.andrbezr2016.products.dto.Product;
import com.andrbezr2016.products.dto.ProductNotification;
import com.andrbezr2016.products.dto.ProductRequest;
import com.andrbezr2016.products.entity.ProductEntity;
import com.andrbezr2016.products.entity.ProductId;
import com.andrbezr2016.products.mapper.ProductMapper;
import com.andrbezr2016.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Product getCurrentVersion(UUID id) {
        ProductEntity productEntity = productRepository.findCurrentVersionById(id).orElse(null);
        return productMapper.toDto(productEntity != null && productEntity.isDeleted() ? null : productEntity);
    }

    public Collection<Product> getPreviousVersions(UUID id) {
        Collection<ProductEntity> productEntityList = productRepository.findAllPreviousVersionsById(id);
        return productMapper.toDtoCollection(productEntityList);
    }

    public Product getVersionForDate(UUID id, OffsetDateTime date) {
        ProductEntity productEntity = productRepository.findVersionForDateById(id, date).orElse(null);
        return productMapper.toDto(productEntity != null && productEntity.isDeleted() ? null : productEntity);
    }

    @Transactional
    public Product createProduct(ProductRequest productRequest) {
        ProductEntity productEntity = productMapper.toEntity(productRequest);
        productEntity.setId(UUID.randomUUID());
        productEntity.setStartDate(OffsetDateTime.now());
        productEntity.setVersion(0L);
        productEntity = productRepository.save(productEntity);
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        ProductEntity productEntity = productRepository.findCurrentVersionById(id).orElse(null);
        if (isActiveProduct(productEntity)) {
            OffsetDateTime now = OffsetDateTime.now();
            productEntity.setEndDate(now);

            ProductEntity newProductEntity = productMapper.copyEntity(productEntity);
            newProductEntity.setStartDate(now);
            newProductEntity.setEndDate(null);
            newProductEntity.setVersion(newProductEntity.getVersion() + 1);
            newProductEntity.setDeleted(true);
            productRepository.saveAll(List.of(productEntity, newProductEntity));
        }
    }

    @Transactional
    public Product rollBackVersion(UUID id) {
        ProductEntity productEntity = productRepository.findCurrentVersionById(id).orElse(null);
        if (productEntity != null && productEntity.getVersion() > 0) {
            ProductId productId = new ProductId();
            productId.setId(productEntity.getId());
            productId.setVersion(productEntity.getVersion());
            productRepository.deleteById(productId);

            ProductEntity newProductEntity = productRepository.findCurrentVersionById(id).orElse(null);
            if (newProductEntity != null) {
                newProductEntity.setEndDate(null);
                productRepository.save(newProductEntity);
            }

            return productMapper.toDto(newProductEntity);
        }
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public void syncTariff(Collection<ProductNotification> productNotificationCollection) {
        if (CollectionUtils.isNotEmpty(productNotificationCollection)) {
            List<ProductEntity> productEntityList = new ArrayList<>();
            for (ProductNotification productNotification : productNotificationCollection) {
                ProductEntity productEntity = productRepository.findCurrentVersionById(productNotification.getProduct()).orElse(null);
                if (syncNeeded(productEntity, productNotification)) {
                    OffsetDateTime now = OffsetDateTime.now();
                    productEntity.setEndDate(now);
                    productEntityList.add(productEntity);

                    ProductEntity newProductEntity = productMapper.copyEntity(productEntity);
                    newProductEntity.setTariff(productNotification.getTariff());
                    newProductEntity.setTariffVersion(productNotification.getTariffVersion());
                    newProductEntity.setStartDate(now);
                    newProductEntity.setEndDate(null);
                    newProductEntity.setVersion(newProductEntity.getVersion() + 1);
                    productEntityList.add(newProductEntity);
                }
            }
            productRepository.saveAll(productEntityList);
        }
    }

    private boolean syncNeeded(ProductEntity productEntity, ProductNotification productNotification) {
        return productEntity != null
                && (!Objects.equals(productEntity.getTariff(), productNotification.getTariff())
                || !Objects.equals(productEntity.getTariffVersion(), productNotification.getTariffVersion()));
    }

    private boolean isActiveProduct(ProductEntity productEntity) {
        return productEntity != null && !productEntity.isDeleted();
    }
}
