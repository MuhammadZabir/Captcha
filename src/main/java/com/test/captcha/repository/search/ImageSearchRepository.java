package com.test.captcha.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.test.captcha.domain.Image;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Image} entity.
 */
public interface ImageSearchRepository extends ReactiveElasticsearchRepository<Image, Long>, ImageSearchRepositoryInternal {}

interface ImageSearchRepositoryInternal {
    Flux<Image> search(String query);
}

class ImageSearchRepositoryInternalImpl implements ImageSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ImageSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Image> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Image.class).map(SearchHit::getContent);
    }
}
