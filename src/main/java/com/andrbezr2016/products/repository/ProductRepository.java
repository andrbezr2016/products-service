package com.andrbezr2016.products.repository;

import com.andrbezr2016.products.entity.ProductEntity;
import com.andrbezr2016.products.entity.ProductId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, ProductId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("FROM ProductEntity WHERE id = :id ORDER BY version DESC LIMIT 1")
    Optional<ProductEntity> findCurrentVersionById(UUID id);

    @Query("FROM ProductEntity WHERE id = :id ORDER BY version DESC OFFSET 1")
    List<ProductEntity> findAllPreviousVersionsById(UUID id);

    @Query("FROM ProductEntity WHERE id = :id AND :date BETWEEN startDate AND endDate")
    Optional<ProductEntity> findVersionForDateById(UUID id, OffsetDateTime date);

    @Transactional
    @Query("DELETE FROM ProductEntity WHERE id = :id")
    void deleteAllVersionsById(UUID id);
}
