package com.andrbezr2016.products.controller;

import com.andrbezr2016.products.dto.Product;
import com.andrbezr2016.products.dto.ProductRequest;
import com.andrbezr2016.products.entity.ProductEntity;
import com.andrbezr2016.products.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    private final static String GET_CURRENT_VERSION = "/product/{id}/getCurrentVersion";
    private final static String GET_PREVIOUS_VERSIONS = "/product/{id}/getPreviousVersions";
    private final static String GET_VERSION_FOR_DATE = "/product/{id}/getVersionForDate";
    private final static String CREATE_PRODUCT = "/product/create";
    private final static String DELETE_PRODUCT = "/product/{id}/delete";
    private final static String ROLL_BACK_VERSION = "/product/{id}/rollBackVersion";
    private final static String SYNC_TARIFF = "/product/syncTariff";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ProductRepository productRepository;

    @Test
    void getCurrentVersionTest() throws Exception {
        UUID id = UUID.fromString("548ea2e0-bcef-4e12-b933-803a4de50106");
        UUID tariff = UUID.fromString("5c50cc6c-8600-48a3-acf8-a83298035857");
        UUID author = UUID.fromString("53d15658-5493-4828-80d9-f1c1f8eae252");
        String name = "Product 1 Update 2";
        LocalDateTime startDate = LocalDateTime.parse("2020-01-01T14:00:00.000");
        long version = 2L;
        long tariffVersion = 3L;

        Product product = Product.builder()
                .id(id)
                .name(name)
                .type(Product.ProductType.CARD)
                .startDate(startDate)
                .tariff(tariff)
                .tariffVersion(tariffVersion)
                .author(author)
                .version(version)
                .build();

        mvc.perform(get(GET_CURRENT_VERSION, id))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(product)));
    }

    @Test
    void getPreviousVersionsTest() throws Exception {
        UUID id = UUID.fromString("548ea2e0-bcef-4e12-b933-803a4de50106");

        mvc.perform(get(GET_PREVIOUS_VERSIONS, id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getVersionForDateTest() throws Exception {
        UUID id = UUID.fromString("548ea2e0-bcef-4e12-b933-803a4de50106");
        UUID tariff = UUID.fromString("284add3b-e6f2-45f6-8a5e-1dfbed6a1f40");
        UUID author = UUID.fromString("53d15658-5493-4828-80d9-f1c1f8eae252");
        String name = "Product 1 Update 1";
        LocalDateTime startDate = LocalDateTime.parse("2020-01-01T13:00:00.000");
        LocalDateTime endDate = LocalDateTime.parse("2020-01-01T14:00:00.000");
        long version = 1L;
        long tariffVersion = 2L;
        LocalDateTime date = LocalDateTime.parse("2020-01-01T13:30:00.000");

        Product product = Product.builder()
                .id(id)
                .name(name)
                .type(Product.ProductType.CARD)
                .startDate(startDate)
                .endDate(endDate)
                .tariff(tariff)
                .tariffVersion(tariffVersion)
                .author(author)
                .version(version)
                .build();

        mvc.perform(get(GET_VERSION_FOR_DATE, id).queryParam("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(product)));
    }

    @Test
    void createProductTest() throws Exception {
        String name = "Product 2 Create";
        UUID author = UUID.fromString("53d15658-5493-4828-80d9-f1c1f8eae252");
        String description = "Product 2 description";
        long version = 0L;

        ProductRequest productRequest = ProductRequest.builder()
                .name(name)
                .type(Product.ProductType.LOAN)
                .description(description)
                .author(author)
                .build();

        mvc.perform(post(CREATE_PRODUCT).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.type").value(Product.ProductType.LOAN.toString()))
                .andExpect(jsonPath("$.startDate").isNotEmpty())
                .andExpect(jsonPath("$.endDate").isEmpty())
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.author").value(author.toString()))
                .andExpect(jsonPath("$.version").value(version));
    }

    @Test
    void deleteProductTest() throws Exception {
        UUID id = UUID.fromString("15cfb4c1-7083-475e-838d-4a1e696cf917");

        mvc.perform(delete(DELETE_PRODUCT, id))
                .andExpect(status().isOk());

        ProductEntity deletedProductEntity = productRepository.findCurrentVersionById(id).orElse(null);
        assertNull(deletedProductEntity);

        deletedProductEntity = productRepository.findLastVersionById(id).orElse(null);
        assertNotNull(deletedProductEntity);
        assertEquals(id, deletedProductEntity.getId());
        assertEquals(ProductEntity.State.DELETED, deletedProductEntity.getState());
    }

    @Test
    void rollBackVersionTest() {
    }

    @Test
    void syncTariffTest() {
    }
}