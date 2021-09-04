package com.test.captcha.repository;

import com.test.captcha.domain.Cart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Cart entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartRepository extends R2dbcRepository<Cart, Long>, CartRepositoryInternal {
    @Query("SELECT * FROM cart entity WHERE entity.buyer_id = :id")
    Flux<Cart> findByBuyer(Long id);

    @Query("SELECT * FROM cart entity WHERE entity.buyer_id IS NULL")
    Flux<Cart> findAllWhereBuyerIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Cart> findAll();

    @Override
    Mono<Cart> findById(Long id);

    @Override
    <S extends Cart> Mono<S> save(S entity);
}

interface CartRepositoryInternal {
    <S extends Cart> Mono<S> insert(S entity);
    <S extends Cart> Mono<S> save(S entity);
    Mono<Integer> update(Cart entity);

    Flux<Cart> findAll();
    Mono<Cart> findById(Long id);
    Flux<Cart> findAllBy(Pageable pageable);
    Flux<Cart> findAllBy(Pageable pageable, Criteria criteria);
}
