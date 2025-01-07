package com.andrbezr2016.products.controller;

import com.andrbezr2016.products.dto.Product;
import com.andrbezr2016.products.dto.ProductNotification;
import com.andrbezr2016.products.dto.ProductRequest;
import com.andrbezr2016.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/product")
@RestController
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}/checkCurrentVersion")
    public Boolean checkCurrentVersion(@PathVariable("id") UUID id) {
        log.info("Check current version of product with id: {}", id);
        return productService.checkCurrentVersion(id);
    }

    @GetMapping("/{id}/getCurrentVersion")
    public Product getCurrentVersion(@PathVariable("id") UUID id) {
        log.info("Get current version of product with id: {}", id);
        return productService.getCurrentVersion(id);
    }

    @GetMapping("/{id}/getPreviousVersions")
    public Collection<Product> getPreviousVersions(@PathVariable("id") UUID id) {
        log.info("Get previous versions of product with id: {}", id);
        return productService.getPreviousVersions(id);
    }

    @GetMapping("/{id}/getVersionForDate")
    public Product getVersionForDate(@PathVariable("id") UUID id, @RequestParam("date") LocalDateTime date) {
        log.info("Get version of product with id: {} for date: {}", id, date);
        return productService.getVersionForDate(id, date);
    }

    @PostMapping("/create")
    public Product createProduct(@RequestBody ProductRequest productRequest) {
        log.info("Create new product");
        return productService.createProduct(productRequest);
    }

    @DeleteMapping("/{id}/delete")
    public void deleteProduct(@PathVariable("id") UUID id) {
        log.info("Delete product with id: {}", id);
        productService.deleteProduct(id);
    }

    @PatchMapping("/{id}/rollBackVersion")
    public Product rollBackVersion(@PathVariable("id") UUID id) {
        log.info("Roll back product with id: {}", id);
        return productService.rollBackVersion(id);
    }

    @PostMapping("/syncTariff")
    public void syncTariff(@RequestBody Collection<ProductNotification> productNotificationCollection) {
        log.info("Sync tariff for products with ids: {}", productNotificationCollection.stream().map(ProductNotification::getProduct).toList());
        productService.syncTariff(productNotificationCollection);
    }
}
