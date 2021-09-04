package com.test.captcha.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link UserTypeSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class UserTypeSearchRepositoryMockConfiguration {

    @MockBean
    private UserTypeSearchRepository mockUserTypeSearchRepository;
}