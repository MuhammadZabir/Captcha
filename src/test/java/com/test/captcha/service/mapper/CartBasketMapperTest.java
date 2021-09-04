package com.test.captcha.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartBasketMapperTest {

    private CartBasketMapper cartBasketMapper;

    @BeforeEach
    public void setUp() {
        cartBasketMapper = new CartBasketMapperImpl();
    }
}
