package com.andrbezr2016.products.kafka;

import com.andrbezr2016.products.dto.ProductNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

public class ProductNotificationDeserializer implements Deserializer<ProductNotification> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProductNotification deserialize(String topic, byte[] bytes) {
        try {
            if (bytes == null) {
                return null;
            }
            return objectMapper.readValue(new String(bytes, StandardCharsets.UTF_8), ProductNotification.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing ProductNotification");
        }
    }
}
