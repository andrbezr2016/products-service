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

    public boolean checkCurrentVersion(UUID id) {
        ProductEntity productEntity = findActiveVersion(id);
        return productEntity != null;
    }

    public Product getCurrentVersion(UUID id) {
        ProductEntity productEntity = findActiveVersion(id);
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
        productEntity.setState(ProductEntity.State.ACTIVE);
        productEntity = productRepository.save(productEntity);
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        ProductEntity productEntity = findActiveVersion(id);
        if (productEntity != null) {
            ProductEntity newProductEntity = productMapper.copyEntity(productEntity);

            LocalDateTime now = currentDateService.getCurrentDate();
            productEntity.setEndDate(now);
            productEntity.setState(ProductEntity.State.INACTIVE);

            Long maxVersion = findMaxVersion(productEntity.getId()).getVersion();
            newProductEntity.setStartDate(now);
            newProductEntity.setEndDate(null);
            newProductEntity.setVersion(maxVersion + 1);
            newProductEntity.setState(ProductEntity.State.DELETED);
            productRepository.saveAll(List.of(productEntity, newProductEntity));
        }
    }

    @Transactional
    public Product rollBackVersion(UUID id) {
        ProductEntity productEntity = findLastVersion(id);
        if (productEntity != null && productEntity.getVersion() > 0) {
            LocalDateTime now = currentDateService.getCurrentDate();
            productEntity.setEndDate(now);
            productEntity.setState(ProductEntity.State.INACTIVE);

            ProductId productId = new ProductId(productEntity.getId(), productEntity.getVersion() - 1);
            ProductEntity newProductEntity = productRepository.findById(productId).orElse(null);
            if (newProductEntity != null) {
                newProductEntity.setEndDate(null);
                newProductEntity.setState(ProductEntity.State.ACTIVE);
                productRepository.saveAll(List.of(productEntity, newProductEntity));
                return productMapper.toDto(newProductEntity);
            }
        }
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public void syncTariff(Collection<ProductNotification> productNotificationCollection) {
        if (CollectionUtils.isNotEmpty(productNotificationCollection)) {
            for (ProductNotification productNotification : productNotificationCollection) {
                List<ProductEntity> productEntityList = new ArrayList<>();
                ProductEntity productEntity = findActiveVersion(productNotification.getProduct());
                if (isSyncNeeded(productEntity, productNotification)) {
                    ProductEntity newProductEntity = productMapper.copyEntity(productEntity);

                    LocalDateTime now = currentDateService.getCurrentDate();
                    productEntity.setEndDate(now);
                    productEntity.setState(ProductEntity.State.INACTIVE);
                    productEntityList.add(productEntity);

                    Long maxVersion = findMaxVersion(productEntity.getId()).getVersion();
                    newProductEntity.setTariff(productNotification.getTariff());
                    newProductEntity.setTariffVersion(productNotification.getTariffVersion());
                    newProductEntity.setStartDate(now);
                    newProductEntity.setEndDate(null);
                    newProductEntity.setVersion(maxVersion + 1);
                    newProductEntity.setState(newProductEntity.getState());
                    productEntityList.add(newProductEntity);
                    productRepository.saveAll(productEntityList);
                }
            }
        }
    }

    private boolean isSyncNeeded(ProductEntity productEntity, ProductNotification productNotification) {
        return productEntity != null && (!Objects.equals(productEntity.getTariff(), productNotification.getTariff())
                || !Objects.equals(productEntity.getTariffVersion(), productNotification.getTariffVersion()));
    }

    private ProductEntity findActiveVersion(UUID id) {
        return productRepository.findActiveVersionById(id).orElse(null);
    }

    private ProductEntity findLastVersion(UUID id) {
        return productRepository.findLastVersionById(id).orElse(null);
    }

    private ProductEntity findMaxVersion(UUID id) {
        return productRepository.findMaxVersionById(id).orElse(null);
    }

    private Collection<ProductEntity> findPreviousVersions(UUID id) {
        return productRepository.findAllPreviousVersionsById(id);
    }

    private ProductEntity findVersionForDate(UUID id, LocalDateTime date) {
        return productRepository.findVersionForDateById(id, date).orElse(null);
    }
}
