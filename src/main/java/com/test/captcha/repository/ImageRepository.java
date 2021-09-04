package com.test.captcha.repository;

import com.test.captcha.domain.Image;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Image entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImageRepository extends R2dbcRepository<Image, Long>, ImageRepositoryInternal {
    @Query("SELECT * FROM image entity WHERE entity.item_id = :id")
    Flux<Image> findByItem(Long id);

    @Query("SELECT * FROM image entity WHERE entity.item_id IS NULL")
    Flux<Image> findAllWhereItemIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Image> findAll();

    @Override
    Mono<Image> findById(Long id);

    @Override
    <S extends Image> Mono<S> save(S entity);
}

interface ImageRepositoryInternal {
    <S extends Image> Mono<S> insert(S entity);
    <S extends Image> Mono<S> save(S entity);
    Mono<Integer> update(Image entity);

    Flux<Image> findAll();
    Mono<Image> findById(Long id);
    Flux<Image> findAllBy(Pageable pageable);
    Flux<Image> findAllBy(Pageable pageable, Criteria criteria);
}
