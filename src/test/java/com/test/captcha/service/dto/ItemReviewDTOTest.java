package com.test.captcha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.captcha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ItemReviewDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ItemReviewDTO.class);
        ItemReviewDTO itemReviewDTO1 = new ItemReviewDTO();
        itemReviewDTO1.setId(1L);
        ItemReviewDTO itemReviewDTO2 = new ItemReviewDTO();
        assertThat(itemReviewDTO1).isNotEqualTo(itemReviewDTO2);
        itemReviewDTO2.setId(itemReviewDTO1.getId());
        assertThat(itemReviewDTO1).isEqualTo(itemReviewDTO2);
        itemReviewDTO2.setId(2L);
        assertThat(itemReviewDTO1).isNotEqualTo(itemReviewDTO2);
        itemReviewDTO1.setId(null);
        assertThat(itemReviewDTO1).isNotEqualTo(itemReviewDTO2);
    }
}
