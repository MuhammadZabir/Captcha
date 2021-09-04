package com.test.captcha.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.captcha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ItemStatusTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ItemStatus.class);
        ItemStatus itemStatus1 = new ItemStatus();
        itemStatus1.setId(1L);
        ItemStatus itemStatus2 = new ItemStatus();
        itemStatus2.setId(itemStatus1.getId());
        assertThat(itemStatus1).isEqualTo(itemStatus2);
        itemStatus2.setId(2L);
        assertThat(itemStatus1).isNotEqualTo(itemStatus2);
        itemStatus1.setId(null);
        assertThat(itemStatus1).isNotEqualTo(itemStatus2);
    }
}
