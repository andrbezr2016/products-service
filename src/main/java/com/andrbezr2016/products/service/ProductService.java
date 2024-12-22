package com.andrbezr2016.products.service;

import com.andrbezr2016.products.dto.Product;
import com.andrbezr2016.products.dto.ProductNotification;
import com.andrbezr2016.products.dto.ProductRequest;
import com.andrbezr2016.products.entity.ProductAuditId;
import com.andrbezr2016.products.entity.ProductEntity;
import com.andrbezr2016.products.mapper.ProductMapper;
import com.andrbezr2016.products.repository.ProductAuditRepository;
import com.andrbezr2016.products.repository.ProductRepository;
import com.andrbezr2016.products.repository.RevisionInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RevisionInfoRepository revisionInfoRepository;
    private final ProductAuditRepository productAuditRepository;
    private final ProductMapper productMapper;
    private final LocalDateTimeService localDateTimeService;

    public Product getCurrentVersion(UUID id) {
        Revision<Long, ProductEntity> revision = productRepository.findLastChangeRevision(id).orElse(null);
        ProductEntity productEntity = revision != null ? revision.getEntity() : null;
        return productMapper.toDto(productEntity);
    }

    public Collection<Product> getPreviousVersions(UUID id) {
        Revisions<Long, ProductEntity> revisions = productRepository.findRevisions(id);
        Long lastVersion = revisions.getLatestRevision().getEntity().getVersion();
        Collection<ProductEntity> productEntityList = revisions.stream().map(Revision::getEntity).filter(p -> !Objects.equals(p.getVersion(), lastVersion)).toList();
        return productMapper.toDtoCollection(productEntityList);
    }

    public Product getVersionForDate(UUID id, LocalDateTime date) {
        Revision<Long, ProductEntity> revision = productRepository.findRevisions(id).stream().filter(r -> validDate(date, r.getEntity().getStartDate(), r.getEntity().getEndDate())).findFirst().orElse(null);
        return productMapper.toDto(revision != null ? revision.getEntity() : null);
    }

    @Transactional
    public Product createProduct(ProductRequest productRequest) {
        ProductEntity productEntity = productMapper.toEntity(productRequest);
        productEntity.setId(UUID.randomUUID());
        productEntity.setVersion(0L);
        productEntity.setStartDate(localDateTimeService.getCurrentDate());
        productEntity = productRepository.save(productEntity);
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Revision<Long, ProductEntity> revision = productRepository.findLastChangeRevision(id).orElse(null);
        ProductEntity productEntity = revision != null ? revision.getEntity() : null;
        if (productEntity != null) {
            productRepository.deleteById(id);
        }
    }

    @Transactional
    public Product rollBackVersion(UUID id) {
        Revision<Long, ProductEntity> revision = productRepository.findLastChangeRevision(id).orElse(null);
        ProductEntity productEntity = revision != null ? revision.getEntity() : null;
        if (productEntity != null && productEntity.getVersion() > 0) {
            Revisions<Long, ProductEntity> revisions = productRepository.findRevisions(id);
            Long lastVersion = revisions.getLatestRevision().getEntity().getVersion();
            Revision<Long, ProductEntity> prevRevision = revisions.stream().filter(p -> Objects.equals(p.getEntity().getVersion(), lastVersion - 1)).findFirst().orElse(null);
            ProductEntity prevProductEntity = prevRevision != null ? prevRevision.getEntity() : null;
            if (prevProductEntity != null) {
                productRepository.deleteById(id);
                revisionInfoRepository.deleteById(revision.getRequiredRevisionNumber());
                revisionInfoRepository.deleteById(prevRevision.getRequiredRevisionNumber());
                ProductAuditId productAuditId = new ProductAuditId();
                productAuditId.setId(revision.getEntity().getId());
                productAuditId.setRev(revision.getRequiredRevisionNumber());
                productAuditRepository.deleteById(productAuditId);
                productAuditId.setRev(prevRevision.getRequiredRevisionNumber());
                productAuditRepository.deleteById(productAuditId);
                productRepository.save(prevProductEntity);
                return productMapper.toDto(prevProductEntity);
            }
        }
        return productMapper.toDto(productEntity);
    }

    @Transactional
    public void syncTariff(Collection<ProductNotification> productNotificationCollection) {
        if (CollectionUtils.isNotEmpty(productNotificationCollection)) {
            List<ProductEntity> productEntityList = new ArrayList<>();
            for (ProductNotification productNotification : productNotificationCollection) {
                Revision<Long, ProductEntity> revision = productRepository.findLastChangeRevision(productNotification.getProduct()).orElse(null);
                ProductEntity productEntity = revision != null ? revision.getEntity() : null;
                if (syncNeeded(productEntity, productNotification)) {
                    productEntity.setTariff(productNotification.getTariff());
                    productEntity.setTariffVersion(productNotification.getTariffVersion());
                    productEntity.setVersion(productEntity.getVersion() + 1);
                    productEntity.setStartDate(localDateTimeService.getCurrentDate());
                    productEntityList.add(productEntity);
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

    private boolean validDate(LocalDateTime date, LocalDateTime startDate, LocalDateTime endDate) {
        return (startDate == null && endDate != null && date.isBefore(endDate))
                || (startDate != null && date.isAfter(startDate) && endDate != null && date.isBefore(endDate))
                || (startDate != null && date.isAfter(startDate) && endDate == null);
    }
}
