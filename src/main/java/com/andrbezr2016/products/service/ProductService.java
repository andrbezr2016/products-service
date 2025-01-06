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

    public boolean checkProduct(UUID id) {
        ProductEntity productEntity = findCurrentVersion(id);
        return productEntity != null;
    }

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
        productEntity.setState(ProductEntity.State.ACTIVE);
        productEntity = productRepository.save(productEntity);
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        ProductEntity productEntity = findCurrentVersion(id);
        if (productEntity != null) {
            LocalDateTime now = currentDateService.getCurrentDate();
            productEntity.setEndDate(now);
            productEntity.setState(ProductEntity.State.INACTIVE);

            ProductEntity newProductEntity = productMapper.copyEntity(productEntity);
            newProductEntity.setStartDate(now);
            newProductEntity.setEndDate(null);
            newProductEntity.setVersion(newProductEntity.getVersion() + 1);
            newProductEntity.setState(ProductEntity.State.DELETED);
            productRepository.saveAll(List.of(productEntity, newProductEntity));
        }
    }

    @Transactional
    public Product rollBackVersion(UUID id) {
        ProductEntity productEntity = findLastVersion(id);
        if (productEntity != null && productEntity.getVersion() > 0) {
            ProductId productId = new ProductId(productEntity.getId(), productEntity.getVersion());
            productRepository.deleteById(productId);

            ProductEntity newProductEntity = findLastVersion(id);
            if (newProductEntity != null) {
                newProductEntity.setEndDate(null);
                newProductEntity.setState(ProductEntity.State.ACTIVE);
                productRepository.save(newProductEntity);
            }

            return productMapper.toDto(newProductEntity);
        }
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public void syncTariff(Collection<ProductNotification> productNotificationCollection) {
        if (CollectionUtils.isNotEmpty(productNotificationCollection)) {
            for (ProductNotification productNotification : productNotificationCollection) {
                List<ProductEntity> productEntityList = new ArrayList<>();
                ProductEntity productEntity = findLastVersion(productNotification.getProduct());
                if (productEntity != null) {
                    final boolean isTariffReplaceNeeded = !productNotification.isToClean() && (!Objects.equals(productEntity.getTariff(), productNotification.getTariff())
                            || !Objects.equals(productEntity.getTariffVersion(), productNotification.getTariffVersion()));
                    final boolean isTariffCleanNeeded = productNotification.isToClean() && Objects.equals(productEntity.getTariff(), productNotification.getTariff())
                            && Objects.equals(productEntity.getTariffVersion(), productNotification.getTariffVersion());

                    if (isTariffReplaceNeeded || isTariffCleanNeeded) {
                        ProductEntity newProductEntity = productMapper.copyEntity(productEntity);

                        LocalDateTime now = currentDateService.getCurrentDate();
                        productEntity.setEndDate(now);
                        productEntity.setState(ProductEntity.State.INACTIVE);
                        productEntityList.add(productEntity);

                        if (isTariffReplaceNeeded) {
                            newProductEntity.setTariff(productNotification.getTariff());
                            newProductEntity.setTariffVersion(productNotification.getTariffVersion());
                        } else {
                            newProductEntity.setTariff(null);
                            newProductEntity.setTariffVersion(null);
                        }
                        newProductEntity.setStartDate(now);
                        newProductEntity.setEndDate(null);
                        newProductEntity.setVersion(newProductEntity.getVersion() + 1);
                        newProductEntity.setState(newProductEntity.getState());
                        productEntityList.add(newProductEntity);
                        productRepository.saveAll(productEntityList);
                    }
                }
            }
        }
    }

    private ProductEntity findCurrentVersion(UUID id) {
        return productRepository.findCurrentVersionById(id).orElse(null);
    }

    private ProductEntity findLastVersion(UUID id) {
        return productRepository.findLastVersionById(id).orElse(null);
    }

    private Collection<ProductEntity> findPreviousVersions(UUID id) {
        return productRepository.findAllPreviousVersionsById(id);
    }

    private ProductEntity findVersionForDate(UUID id, LocalDateTime date) {
        return productRepository.findVersionForDateById(id, date).orElse(null);
    }
}
