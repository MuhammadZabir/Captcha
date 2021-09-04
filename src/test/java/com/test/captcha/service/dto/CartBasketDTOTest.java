package com.test.captcha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.captcha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartBasketDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartBasketDTO.class);
        CartBasketDTO cartBasketDTO1 = new CartBasketDTO();
        cartBasketDTO1.setId(1L);
        CartBasketDTO cartBasketDTO2 = new CartBasketDTO();
        assertThat(cartBasketDTO1).isNotEqualTo(cartBasketDTO2);
        cartBasketDTO2.setId(cartBasketDTO1.getId());
        assertThat(cartBasketDTO1).isEqualTo(cartBasketDTO2);
        cartBasketDTO2.setId(2L);
        assertThat(cartBasketDTO1).isNotEqualTo(cartBasketDTO2);
        cartBasketDTO1.setId(null);
        assertThat(cartBasketDTO1).isNotEqualTo(cartBasketDTO2);
    }
}
