package com.test.captcha.repository;

import com.test.captcha.domain.ItemReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the ItemReview entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ItemReviewRepository extends R2dbcRepository<ItemReview, Long>, ItemReviewRepositoryInternal {
    @Query("SELECT * FROM item_review entity WHERE entity.reviewer_id = :id")
    Flux<ItemReview> findByReviewer(Long id);

    @Query("SELECT * FROM item_review entity WHERE entity.reviewer_id IS NULL")
    Flux<ItemReview> findAllWhereReviewerIsNull();

    @Query("SELECT * FROM item_review entity WHERE entity.item_id = :id")
    Flux<ItemReview> findByItem(Long id);

    @Query("SELECT * FROM item_review entity WHERE entity.item_id IS NULL")
    Flux<ItemReview> findAllWhereItemIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<ItemReview> findAll();

    @Override
    Mono<ItemReview> findById(Long id);

    @Override
    <S extends ItemReview> Mono<S> save(S entity);
}

interface ItemReviewRepositoryInternal {
    <S extends ItemReview> Mono<S> insert(S entity);
    <S extends ItemReview> Mono<S> save(S entity);
    Mono<Integer> update(ItemReview entity);

    Flux<ItemReview> findAll();
    Mono<ItemReview> findById(Long id);
    Flux<ItemReview> findAllBy(Pageable pageable);
    Flux<ItemReview> findAllBy(Pageable pageable, Criteria criteria);
}
