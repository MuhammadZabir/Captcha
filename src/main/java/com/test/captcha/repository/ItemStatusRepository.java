package com.test.captcha.repository;

import com.test.captcha.domain.ItemStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the ItemStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ItemStatusRepository extends R2dbcRepository<ItemStatus, Long>, ItemStatusRepositoryInternal {
    @Query("SELECT * FROM item_status entity WHERE entity.item_id = :id")
    Flux<ItemStatus> findByItem(Long id);

    @Query("SELECT * FROM item_status entity WHERE entity.item_id IS NULL")
    Flux<ItemStatus> findAllWhereItemIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<ItemStatus> findAll();

    @Override
    Mono<ItemStatus> findById(Long id);

    @Override
    <S extends ItemStatus> Mono<S> save(S entity);
}

interface ItemStatusRepositoryInternal {
    <S extends ItemStatus> Mono<S> insert(S entity);
    <S extends ItemStatus> Mono<S> save(S entity);
    Mono<Integer> update(ItemStatus entity);

    Flux<ItemStatus> findAll();
    Mono<ItemStatus> findById(Long id);
    Flux<ItemStatus> findAllBy(Pageable pageable);
    Flux<ItemStatus> findAllBy(Pageable pageable, Criteria criteria);
}
