package com.test.captcha.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemReviewMapperTest {

    private ItemReviewMapper itemReviewMapper;

    @BeforeEach
    public void setUp() {
        itemReviewMapper = new ItemReviewMapperImpl();
    }
}
