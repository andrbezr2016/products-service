package com.andrbezr2016.products.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class LocalDateTimeService {

    public LocalDateTime getCurrentDate() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }
}
