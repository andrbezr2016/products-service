package com.andrbezr2016.products.repository;

import com.andrbezr2016.products.entity.ProductEntity;
import com.andrbezr2016.products.entity.ProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, ProductId> {

    @Query("FROM ProductEntity WHERE id = :id AND state = 'ACTIVE'")
    Optional<ProductEntity> findCurrentVersionById(UUID id);

    @Query("FROM ProductEntity WHERE (id, version) IN (SELECT id, MAX(version) FROM ProductEntity WHERE id = :id GROUP BY id)")
    Optional<ProductEntity> findLastVersionById(UUID id);

    @Query("FROM ProductEntity WHERE id = :id AND state = 'INACTIVE' ORDER BY version DESC")
    List<ProductEntity> findAllPreviousVersionsById(UUID id);

    @Query("FROM ProductEntity WHERE id = :id AND state <> 'DELETED' AND ((:date BETWEEN startDate AND endDate) OR (:date > startDate AND endDate IS NULL))")
    Optional<ProductEntity> findVersionForDateById(UUID id, LocalDateTime date);
}
