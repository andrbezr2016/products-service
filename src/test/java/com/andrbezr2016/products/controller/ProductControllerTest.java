package com.andrbezr2016.products.controller;

import com.andrbezr2016.products.dto.Product;
import com.andrbezr2016.products.dto.ProductNotification;
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
import java.util.List;
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
        Product product = Product.builder()
                .id(UUID.fromString("548ea2e0-bcef-4e12-b933-803a4de50106"))
                .name("Product 1 Update 2")
                .type(Product.ProductType.CARD)
                .startDate(LocalDateTime.parse("2020-01-01T14:00:00.000"))
                .tariff(UUID.fromString("5c50cc6c-8600-48a3-acf8-a83298035857"))
                .tariffVersion(3L)
                .author(UUID.fromString("53d15658-5493-4828-80d9-f1c1f8eae252"))
                .version(2L)
                .build();

        mvc.perform(get(GET_CURRENT_VERSION, product.getId()))
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
        LocalDateTime date = LocalDateTime.parse("2020-01-01T13:30:00.000");

        Product product = Product.builder()
                .id(UUID.fromString("548ea2e0-bcef-4e12-b933-803a4de50106"))
                .name("Product 1 Update 1")
                .type(Product.ProductType.CARD)
                .startDate(LocalDateTime.parse("2020-01-01T13:00:00.000"))
                .endDate(LocalDateTime.parse("2020-01-01T14:00:00.000"))
                .tariff(UUID.fromString("284add3b-e6f2-45f6-8a5e-1dfbed6a1f40"))
                .tariffVersion(2L)
                .author(UUID.fromString("53d15658-5493-4828-80d9-f1c1f8eae252"))
                .version(1L)
                .build();

        mvc.perform(get(GET_VERSION_FOR_DATE, product.getId()).queryParam("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(product)));
    }

    @Test
    void createProductTest() throws Exception {
        long version = 0L;

        ProductRequest productRequest = ProductRequest.builder()
                .name("Product 2 Create")
                .type(Product.ProductType.LOAN)
                .description("Product 2 description")
                .author(UUID.fromString("53d15658-5493-4828-80d9-f1c1f8eae252"))
                .build();

        mvc.perform(post(CREATE_PRODUCT).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(productRequest.getName()))
                .andExpect(jsonPath("$.type").value(productRequest.getType().toString()))
                .andExpect(jsonPath("$.startDate").isNotEmpty())
                .andExpect(jsonPath("$.endDate").isEmpty())
                .andExpect(jsonPath("$.description").value(productRequest.getDescription()))
                .andExpect(jsonPath("$.author").value(productRequest.getAuthor().toString()))
                .andExpect(jsonPath("$.version").value(version));
    }

    @Test
    void deleteProductTest() throws Exception {
        UUID id = UUID.fromString("15cfb4c1-7083-475e-838d-4a1e696cf917");

        mvc.perform(delete(DELETE_PRODUCT, id))
                .andExpect(status().isOk());

        ProductEntity deletedProductEntity = productRepository.findActiveVersionById(id).orElse(null);
        assertNull(deletedProductEntity);

        deletedProductEntity = productRepository.findLastVersionById(id).orElse(null);
        assertNotNull(deletedProductEntity);
        assertEquals(id, deletedProductEntity.getId());
        assertEquals(ProductEntity.State.DELETED, deletedProductEntity.getState());
    }

    @Test
    void rollBackVersionTest() throws Exception {
        UUID id = UUID.fromString("272fff11-4790-4488-9564-7370724816c2");

        mvc.perform(patch(ROLL_BACK_VERSION, id))
                .andExpect(status().isOk());

        ProductEntity productEntity = productRepository.findLastVersionById(id).orElse(null);
        assertNotNull(productEntity);
        assertEquals(id, productEntity.getId());
        assertEquals(ProductEntity.State.ACTIVE, productEntity.getState());
        assertEquals(0L, productEntity.getVersion());
    }

    @Test
    void syncTariffTest() throws Exception {
        ProductNotification productNotification1 = new ProductNotification();
        productNotification1.setProduct(UUID.fromString("a8ddef4d-5942-42b8-9354-41a715e03b56"));
        productNotification1.setTariff(UUID.randomUUID());
        productNotification1.setTariffVersion(1L);
        productNotification1.setStartDate(LocalDateTime.now());

        ProductNotification productNotification2 = new ProductNotification();
        productNotification2.setProduct(UUID.fromString("a929d899-1f06-418d-a002-77f4d6584676"));
        productNotification2.setTariff(UUID.randomUUID());
        productNotification2.setTariffVersion(1L);
        productNotification2.setStartDate(LocalDateTime.now());

        mvc.perform(post(SYNC_TARIFF).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(List.of(productNotification1, productNotification2))))
                .andExpect(status().isOk());

        ProductEntity productEntity1 = productRepository.findLastVersionById(productNotification1.getProduct()).orElse(null);
        assertNotNull(productEntity1);
        assertEquals(productNotification1.getProduct(), productEntity1.getId());
        assertEquals(productNotification1.getTariff(), productEntity1.getTariff());
        assertEquals(productNotification1.getTariffVersion(), productEntity1.getTariffVersion());
        assertEquals(ProductEntity.State.ACTIVE, productEntity1.getState());
        assertEquals(1L, productEntity1.getVersion());

        ProductEntity productEntity2 = productRepository.findLastVersionById(productNotification2.getProduct()).orElse(null);
        assertNotNull(productEntity2);
        assertEquals(productNotification2.getProduct(), productEntity2.getId());
        assertEquals(productNotification2.getTariff(), productEntity2.getTariff());
        assertEquals(productNotification2.getTariffVersion(), productEntity2.getTariffVersion());
        assertEquals(ProductEntity.State.ACTIVE, productEntity2.getState());
        assertEquals(1L, productEntity2.getVersion());
    }
}