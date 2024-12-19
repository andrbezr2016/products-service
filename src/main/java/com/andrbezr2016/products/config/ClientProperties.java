package com.andrbezr2016.products.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties("products-service.client")
public class ClientProperties {

    @ConstructorBinding
    public ClientProperties(String tariffsServiceUrl) {
        this.tariffsServiceUrl = tariffsServiceUrl;
    }

    private final String tariffsServiceUrl;
}
