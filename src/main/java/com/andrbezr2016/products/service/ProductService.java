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

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CurrentDateService currentDateService;

    public Product getCurrentVersion(UUID id) {
        ProductEntity productEntity = findCurrentVersion(id);
        return productMapper.toDto(productEntity);
    }

    public Collection<Product> getPreviousVersions(UUID id) {
        Collection<ProductEntity> productEntityList = findPreviousVersions(id);
        return productMapper.toDtoCollection(productEntityList);
    }

    public Product getVersionForDate(UUID id, LocalDateTime date) {
        ProductEntity productEntity = findVersionForDate(id, date);
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public Product createProduct(ProductRequest productRequest) {
        ProductEntity productEntity = productMapper.toEntity(productRequest);
        productEntity.setId(UUID.randomUUID());
        productEntity.setStartDate(currentDateService.getCurrentDate());
        productEntity.setVersion(0L);
        productEntity = productRepository.save(productEntity);
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        ProductEntity productEntity = findCurrentVersion(id);
        if (isActiveProduct(productEntity)) {
            LocalDateTime now = currentDateService.getCurrentDate();
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
        ProductEntity productEntity = findCurrentVersion(id);
        if (productEntity != null && productEntity.getVersion() > 0) {
            ProductId productId = new ProductId(productEntity.getId(), productEntity.getVersion());
            productRepository.deleteById(productId);

            ProductEntity newProductEntity = findCurrentVersion(id);
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
                ProductEntity productEntity = findCurrentVersion(productNotification.getProduct());
                if (syncNeeded(productEntity, productNotification)) {
                    LocalDateTime now = currentDateService.getCurrentDate();
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

    private ProductEntity findCurrentVersion(UUID id) {
        ProductEntity productEntity = productRepository.findCurrentVersionById(id).orElse(null);
        return productEntity != null && productEntity.isDeleted() ? null : productEntity;
    }

    private Collection<ProductEntity> findPreviousVersions(UUID id) {
        return productRepository.findAllPreviousVersionsById(id);
    }

    private ProductEntity findVersionForDate(UUID id, LocalDateTime date) {
        ProductEntity productEntity = productRepository.findVersionForDateById(id, date).orElse(null);
        return productEntity != null && productEntity.isDeleted() ? null : productEntity;
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
