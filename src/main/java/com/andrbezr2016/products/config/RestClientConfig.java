package com.andrbezr2016.products.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Configuration
public class RestClientConfig {

    private final ClientProperties clientProperties;

    @Bean(name = "tariffsServiceRestTemplate")
    public RestTemplate tariffsServiceRestTemplate(RestTemplateBuilder builder) {
        return builder.rootUri(clientProperties.getTariffsServiceUrl()).build();
    }
}
