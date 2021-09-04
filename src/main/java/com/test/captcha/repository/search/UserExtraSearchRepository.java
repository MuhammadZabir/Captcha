package com.test.captcha.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.test.captcha.domain.UserExtra;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link UserExtra} entity.
 */
public interface UserExtraSearchRepository extends ReactiveElasticsearchRepository<UserExtra, Long>, UserExtraSearchRepositoryInternal {}

interface UserExtraSearchRepositoryInternal {
    Flux<UserExtra> search(String query, Pageable pageable);
}

class UserExtraSearchRepositoryInternalImpl implements UserExtraSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    UserExtraSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<UserExtra> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, UserExtra.class).map(SearchHit::getContent);
    }
}
