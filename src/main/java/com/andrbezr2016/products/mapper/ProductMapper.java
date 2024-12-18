package com.andrbezr2016.products.mapper;

import com.andrbezr2016.products.dto.Product;
import com.andrbezr2016.products.dto.ProductRequest;
import com.andrbezr2016.products.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toDto(ProductEntity productEntity);

    ProductEntity toEntity(ProductRequest productRequest);
}
