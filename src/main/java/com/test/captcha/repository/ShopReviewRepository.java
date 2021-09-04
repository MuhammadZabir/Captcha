package com.test.captcha.repository;

import com.test.captcha.domain.ShopReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the ShopReview entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShopReviewRepository extends R2dbcRepository<ShopReview, Long>, ShopReviewRepositoryInternal {
    @Query("SELECT * FROM shop_review entity WHERE entity.reviewer_id = :id")
    Flux<ShopReview> findByReviewer(Long id);

    @Query("SELECT * FROM shop_review entity WHERE entity.reviewer_id IS NULL")
    Flux<ShopReview> findAllWhereReviewerIsNull();

    @Query("SELECT * FROM shop_review entity WHERE entity.shop_id = :id")
    Flux<ShopReview> findByShop(Long id);

    @Query("SELECT * FROM shop_review entity WHERE entity.shop_id IS NULL")
    Flux<ShopReview> findAllWhereShopIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<ShopReview> findAll();

    @Override
    Mono<ShopReview> findById(Long id);

    @Override
    <S extends ShopReview> Mono<S> save(S entity);
}

interface ShopReviewRepositoryInternal {
    <S extends ShopReview> Mono<S> insert(S entity);
    <S extends ShopReview> Mono<S> save(S entity);
    Mono<Integer> update(ShopReview entity);

    Flux<ShopReview> findAll();
    Mono<ShopReview> findById(Long id);
    Flux<ShopReview> findAllBy(Pageable pageable);
    Flux<ShopReview> findAllBy(Pageable pageable, Criteria criteria);
}
