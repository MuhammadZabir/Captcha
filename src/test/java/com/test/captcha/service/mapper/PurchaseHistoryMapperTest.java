package com.test.captcha.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseHistoryMapperTest {

    private PurchaseHistoryMapper purchaseHistoryMapper;

    @BeforeEach
    public void setUp() {
        purchaseHistoryMapper = new PurchaseHistoryMapperImpl();
    }
}
