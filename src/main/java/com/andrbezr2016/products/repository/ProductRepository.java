package com.andrbezr2016.products.repository;

import com.andrbezr2016.products.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, RevisionRepository<ProductEntity, UUID, Long> {
}
