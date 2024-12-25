package com.andrbezr2016.products.controller;

import com.andrbezr2016.products.dto.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    private final static String GET_CURRENT_VERSION = "/product/{id}/getCurrentVersion";
    private final static String GET_PREVIOUS_VERSIONS = "/product/{id}/getPreviousVersions";
    private final static String GET_VERSION_FOR_DATE = "/product/{id}/getVersionForDate";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

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
    void createProductTest() {
    }

    @Test
    void deleteProductTest() {
    }

    @Test
    void rollBackVersionTest() {
    }

    @Test
    void syncTariffTest() {
    }
}