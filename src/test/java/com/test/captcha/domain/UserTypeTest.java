package com.test.captcha.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.captcha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserType.class);
        UserType userType1 = new UserType();
        userType1.setId(1L);
        UserType userType2 = new UserType();
        userType2.setId(userType1.getId());
        assertThat(userType1).isEqualTo(userType2);
        userType2.setId(2L);
        assertThat(userType1).isNotEqualTo(userType2);
        userType1.setId(null);
        assertThat(userType1).isNotEqualTo(userType2);
    }
}
