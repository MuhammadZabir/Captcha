package com.test.captcha.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.test.captcha.domain.PurchaseHistory;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link PurchaseHistory} entity.
 */
public interface PurchaseHistorySearchRepository
    extends ReactiveElasticsearchRepository<PurchaseHistory, Long>, PurchaseHistorySearchRepositoryInternal {}

interface PurchaseHistorySearchRepositoryInternal {
    Flux<PurchaseHistory> search(String query);
}

class PurchaseHistorySearchRepositoryInternalImpl implements PurchaseHistorySearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    PurchaseHistorySearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<PurchaseHistory> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, PurchaseHistory.class).map(SearchHit::getContent);
    }
}
