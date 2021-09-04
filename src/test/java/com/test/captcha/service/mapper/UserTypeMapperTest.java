package com.test.captcha.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTypeMapperTest {

    private UserTypeMapper userTypeMapper;

    @BeforeEach
    public void setUp() {
        userTypeMapper = new UserTypeMapperImpl();
    }
}
