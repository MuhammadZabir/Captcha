package com.test.captcha.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.captcha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShopReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShopReview.class);
        ShopReview shopReview1 = new ShopReview();
        shopReview1.setId(1L);
        ShopReview shopReview2 = new ShopReview();
        shopReview2.setId(shopReview1.getId());
        assertThat(shopReview1).isEqualTo(shopReview2);
        shopReview2.setId(2L);
        assertThat(shopReview1).isNotEqualTo(shopReview2);
        shopReview1.setId(null);
        assertThat(shopReview1).isNotEqualTo(shopReview2);
    }
}
