package com.test.captcha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.captcha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShopReviewDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShopReviewDTO.class);
        ShopReviewDTO shopReviewDTO1 = new ShopReviewDTO();
        shopReviewDTO1.setId(1L);
        ShopReviewDTO shopReviewDTO2 = new ShopReviewDTO();
        assertThat(shopReviewDTO1).isNotEqualTo(shopReviewDTO2);
        shopReviewDTO2.setId(shopReviewDTO1.getId());
        assertThat(shopReviewDTO1).isEqualTo(shopReviewDTO2);
        shopReviewDTO2.setId(2L);
        assertThat(shopReviewDTO1).isNotEqualTo(shopReviewDTO2);
        shopReviewDTO1.setId(null);
        assertThat(shopReviewDTO1).isNotEqualTo(shopReviewDTO2);
    }
}
