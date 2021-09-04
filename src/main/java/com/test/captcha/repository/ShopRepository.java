package com.test.captcha.repository;

import com.test.captcha.domain.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Shop entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShopRepository extends R2dbcRepository<Shop, Long>, ShopRepositoryInternal {
    Flux<Shop> findAllBy(Pageable pageable);

    @Query("SELECT * FROM shop entity WHERE entity.owner_id = :id")
    Flux<Shop> findByOwner(Long id);

    @Query("SELECT * FROM shop entity WHERE entity.owner_id IS NULL")
    Flux<Shop> findAllWhereOwnerIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Shop> findAll();

    @Override
    Mono<Shop> findById(Long id);

    @Override
    <S extends Shop> Mono<S> save(S entity);
}

interface ShopRepositoryInternal {
    <S extends Shop> Mono<S> insert(S entity);
    <S extends Shop> Mono<S> save(S entity);
    Mono<Integer> update(Shop entity);

    Flux<Shop> findAll();
    Mono<Shop> findById(Long id);
    Flux<Shop> findAllBy(Pageable pageable);
    Flux<Shop> findAllBy(Pageable pageable, Criteria criteria);
}
