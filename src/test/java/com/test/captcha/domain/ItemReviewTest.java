package com.test.captcha.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.captcha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ItemReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ItemReview.class);
        ItemReview itemReview1 = new ItemReview();
        itemReview1.setId(1L);
        ItemReview itemReview2 = new ItemReview();
        itemReview2.setId(itemReview1.getId());
        assertThat(itemReview1).isEqualTo(itemReview2);
        itemReview2.setId(2L);
        assertThat(itemReview1).isNotEqualTo(itemReview2);
        itemReview1.setId(null);
        assertThat(itemReview1).isNotEqualTo(itemReview2);
    }
}
