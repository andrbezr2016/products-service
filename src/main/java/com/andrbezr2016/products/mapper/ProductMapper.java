package com.andrbezr2016.products.mapper;

import com.andrbezr2016.products.dto.Product;
import com.andrbezr2016.products.dto.ProductRequest;
import com.andrbezr2016.products.entity.ProductEntity;
import org.mapstruct.Mapper;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toDto(ProductEntity productEntity);

    Collection<Product> toDtoCollection(Collection<ProductEntity> productEntityList);

    ProductEntity toEntity(ProductRequest productRequest);
}
