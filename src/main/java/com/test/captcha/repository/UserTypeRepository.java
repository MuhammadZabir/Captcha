package com.test.captcha.repository;

import com.test.captcha.domain.UserType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the UserType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserTypeRepository extends R2dbcRepository<UserType, Long>, UserTypeRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<UserType> findAll();

    @Override
    Mono<UserType> findById(Long id);

    @Override
    <S extends UserType> Mono<S> save(S entity);
}

interface UserTypeRepositoryInternal {
    <S extends UserType> Mono<S> insert(S entity);
    <S extends UserType> Mono<S> save(S entity);
    Mono<Integer> update(UserType entity);

    Flux<UserType> findAll();
    Mono<UserType> findById(Long id);
    Flux<UserType> findAllBy(Pageable pageable);
    Flux<UserType> findAllBy(Pageable pageable, Criteria criteria);
}
