package com.test.captcha.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.test.captcha.domain.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Shop} entity.
 */
public interface ShopSearchRepository extends ReactiveElasticsearchRepository<Shop, Long>, ShopSearchRepositoryInternal {}

interface ShopSearchRepositoryInternal {
    Flux<Shop> search(String query, Pageable pageable);
}

class ShopSearchRepositoryInternalImpl implements ShopSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ShopSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Shop> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Shop.class).map(SearchHit::getContent);
    }
}
