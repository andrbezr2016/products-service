package com.andrbezr2016.products.repository;

import com.andrbezr2016.products.entity.ProductAuditEntity;
import com.andrbezr2016.products.entity.ProductAuditId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAuditRepository extends JpaRepository<ProductAuditEntity, ProductAuditId> {
}
