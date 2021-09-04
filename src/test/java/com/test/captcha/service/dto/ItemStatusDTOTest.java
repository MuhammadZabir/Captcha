package com.test.captcha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.captcha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ItemStatusDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ItemStatusDTO.class);
        ItemStatusDTO itemStatusDTO1 = new ItemStatusDTO();
        itemStatusDTO1.setId(1L);
        ItemStatusDTO itemStatusDTO2 = new ItemStatusDTO();
        assertThat(itemStatusDTO1).isNotEqualTo(itemStatusDTO2);
        itemStatusDTO2.setId(itemStatusDTO1.getId());
        assertThat(itemStatusDTO1).isEqualTo(itemStatusDTO2);
        itemStatusDTO2.setId(2L);
        assertThat(itemStatusDTO1).isNotEqualTo(itemStatusDTO2);
        itemStatusDTO1.setId(null);
        assertThat(itemStatusDTO1).isNotEqualTo(itemStatusDTO2);
    }
}
