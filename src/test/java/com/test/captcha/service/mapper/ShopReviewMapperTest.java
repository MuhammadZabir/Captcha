package com.test.captcha.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShopReviewMapperTest {

    private ShopReviewMapper shopReviewMapper;

    @BeforeEach
    public void setUp() {
        shopReviewMapper = new ShopReviewMapperImpl();
    }
}
