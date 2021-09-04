package com.test.captcha.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PurchaseHistorySearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PurchaseHistorySearchRepositoryMockConfiguration {

    @MockBean
    private PurchaseHistorySearchRepository mockPurchaseHistorySearchRepository;
}
