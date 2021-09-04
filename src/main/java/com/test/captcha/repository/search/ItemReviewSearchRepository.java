package com.test.captcha.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.test.captcha.domain.ItemReview;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link ItemReview} entity.
 */
public interface ItemReviewSearchRepository extends ReactiveElasticsearchRepository<ItemReview, Long>, ItemReviewSearchRepositoryInternal {}

interface ItemReviewSearchRepositoryInternal {
    Flux<ItemReview> search(String query);
}

class ItemReviewSearchRepositoryInternalImpl implements ItemReviewSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ItemReviewSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<ItemReview> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, ItemReview.class).map(SearchHit::getContent);
    }
}
