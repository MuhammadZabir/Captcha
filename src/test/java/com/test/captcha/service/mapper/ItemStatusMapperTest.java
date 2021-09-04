package com.test.captcha.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemStatusMapperTest {

    private ItemStatusMapper itemStatusMapper;

    @BeforeEach
    public void setUp() {
        itemStatusMapper = new ItemStatusMapperImpl();
    }
}
