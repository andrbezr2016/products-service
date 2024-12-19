package com.andrbezr2016.products.client;

import com.andrbezr2016.products.dto.TariffNotification;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TariffsServiceClient {

    private final RestTemplate restTemplate;

    public TariffsServiceClient(@Qualifier("tariffsServiceRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void syncTariff(TariffNotification tariffNotification) {
        restTemplate.delete("/tariff/syncTariff?id={id}&version={version}", tariffNotification.getTariff(), tariffNotification.getTariffVersion());
    }
}
