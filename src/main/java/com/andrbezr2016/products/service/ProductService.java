package com.andrbezr2016.products.service;

import com.andrbezr2016.products.dto.Product;
import com.andrbezr2016.products.dto.ProductNotification;
import com.andrbezr2016.products.dto.ProductRequest;
import com.andrbezr2016.products.entity.ProductEntity;
import com.andrbezr2016.products.entity.ProductId;
import com.andrbezr2016.products.entity.TariffNotificationEntity;
import com.andrbezr2016.products.mapper.ProductMapper;
import com.andrbezr2016.products.repository.ProductRepository;
import com.andrbezr2016.products.repository.TariffNotificationRepository;
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
    private final TariffNotificationRepository tariffNotificationRepository;
    private final ProductMapper productMapper;

    public Product getCurrentVersion(UUID id) {
        ProductEntity productEntity = productRepository.findCurrentVersionById(id).orElse(null);
        return productMapper.toDto(productEntity != null && productEntity.isDeleted() ? null : productEntity);
    }

    public Collection<Product> getPreviousVersions(UUID id) {
        Collection<ProductEntity> productEntityList = productRepository.findAllPreviousVersionsById(id);
        return productMapper.toDtoCollection(productEntityList.stream().filter(productEntity -> !productEntity.isDeleted()).toList());
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
        if (productEntity != null) {
            OffsetDateTime now = OffsetDateTime.now();
            productEntity.setEndDate(now);
            productRepository.save(productEntity);

            ProductEntity newProductEntity = productMapper.copyEntity(productEntity);
            newProductEntity.setStartDate(now);
            newProductEntity.setEndDate(null);
            newProductEntity.setVersion(newProductEntity.getVersion() + 1);
            newProductEntity.setDeleted(true);
            productRepository.save(newProductEntity);

            fillNotification(newProductEntity);
        }
    }

    @Transactional
    public Product rollBackVersion(UUID id) {
        ProductEntity productEntity = productRepository.findCurrentVersionById(id).orElse(null);
        ProductEntity newProductEntity = productRepository.findPreviousVersionById(id).orElse(null);
        if (productEntity != null) {
            ProductId productId = new ProductId();
            productId.setId(productEntity.getId());
            productId.setVersion(productEntity.getVersion());
            productRepository.deleteById(productId);

            if (newProductEntity != null) {
                newProductEntity.setEndDate(null);
                productRepository.save(newProductEntity);
            }

            if (newProductEntity == null || !Objects.equals(productEntity.getTariffVersion(), newProductEntity.getTariffVersion())) {
                fillNotification(productEntity);
            }
        }
        return productMapper.toDto(newProductEntity);
    }

    @Transactional
    public void syncTariff(ProductNotification productNotification) {
        ProductEntity productEntity = productRepository.findCurrentVersionById(productNotification.getProduct()).orElse(null);
        if (syncNeeded(productEntity, productNotification)) {
            OffsetDateTime now = OffsetDateTime.now();
            productEntity.setEndDate(now);
            productEntity = productRepository.save(productEntity);

            ProductEntity newProductEntity = productMapper.copyEntity(productEntity);
            newProductEntity.setTariff(productNotification.getTariff());
            newProductEntity.setTariffVersion(productNotification.getTariffVersion());
            newProductEntity.setStartDate(now);
            newProductEntity.setEndDate(null);
            newProductEntity.setVersion(newProductEntity.getVersion() + 1);
            productRepository.save(newProductEntity);
        }
    }

    private boolean syncNeeded(ProductEntity productEntity, ProductNotification productNotification) {
        return productEntity != null
                && (!Objects.equals(productEntity.getTariff(), productNotification.getTariff())
                || !Objects.equals(productEntity.getTariffVersion(), productNotification.getTariffVersion()));
    }

    private void fillNotification(ProductEntity productEntity) {
        if (productEntity != null && productEntity.getTariff() != null) {
            TariffNotificationEntity tariffNotificationEntity = new TariffNotificationEntity();
            tariffNotificationEntity.setTariff(productEntity.getId());
            tariffNotificationEntity.setTariffVersion(productEntity.getTariffVersion());
            tariffNotificationRepository.save(tariffNotificationEntity);
        }
    }
}
