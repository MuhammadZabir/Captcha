package com.test.captcha.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.captcha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartBasketTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartBasket.class);
        CartBasket cartBasket1 = new CartBasket();
        cartBasket1.setId(1L);
        CartBasket cartBasket2 = new CartBasket();
        cartBasket2.setId(cartBasket1.getId());
        assertThat(cartBasket1).isEqualTo(cartBasket2);
        cartBasket2.setId(2L);
        assertThat(cartBasket1).isNotEqualTo(cartBasket2);
        cartBasket1.setId(null);
        assertThat(cartBasket1).isNotEqualTo(cartBasket2);
    }
}
