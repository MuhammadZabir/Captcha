package com.test.captcha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.captcha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PurchaseHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchaseHistoryDTO.class);
        PurchaseHistoryDTO purchaseHistoryDTO1 = new PurchaseHistoryDTO();
        purchaseHistoryDTO1.setId(1L);
        PurchaseHistoryDTO purchaseHistoryDTO2 = new PurchaseHistoryDTO();
        assertThat(purchaseHistoryDTO1).isNotEqualTo(purchaseHistoryDTO2);
        purchaseHistoryDTO2.setId(purchaseHistoryDTO1.getId());
        assertThat(purchaseHistoryDTO1).isEqualTo(purchaseHistoryDTO2);
        purchaseHistoryDTO2.setId(2L);
        assertThat(purchaseHistoryDTO1).isNotEqualTo(purchaseHistoryDTO2);
        purchaseHistoryDTO1.setId(null);
        assertThat(purchaseHistoryDTO1).isNotEqualTo(purchaseHistoryDTO2);
    }
}
