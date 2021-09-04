package com.test.captcha.repository;

import com.test.captcha.domain.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Item entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long>, ItemRepositoryInternal {
    Flux<Item> findAllBy(Pageable pageable);

    @Query("SELECT * FROM item entity WHERE entity.shop_id = :id")
    Flux<Item> findByShop(Long id);

    @Query("SELECT * FROM item entity WHERE entity.shop_id IS NULL")
    Flux<Item> findAllWhereShopIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Item> findAll();

    @Override
    Mono<Item> findById(Long id);

    @Override
    <S extends Item> Mono<S> save(S entity);
}

interface ItemRepositoryInternal {
    <S extends Item> Mono<S> insert(S entity);
    <S extends Item> Mono<S> save(S entity);
    Mono<Integer> update(Item entity);

    Flux<Item> findAll();
    Mono<Item> findById(Long id);
    Flux<Item> findAllBy(Pageable pageable);
    Flux<Item> findAllBy(Pageable pageable, Criteria criteria);
}
