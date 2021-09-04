package com.test.captcha.repository;

import com.test.captcha.domain.UserExtra;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the UserExtra entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserExtraRepository extends R2dbcRepository<UserExtra, Long>, UserExtraRepositoryInternal {
    Flux<UserExtra> findAllBy(Pageable pageable);

    @Query("SELECT * FROM user_extra entity WHERE entity.user_id = :id")
    Flux<UserExtra> findByUser(Long id);

    @Query("SELECT * FROM user_extra entity WHERE entity.user_id IS NULL")
    Flux<UserExtra> findAllWhereUserIsNull();

    @Query("SELECT * FROM user_extra entity WHERE entity.user_type_id = :id")
    Flux<UserExtra> findByUserType(Long id);

    @Query("SELECT * FROM user_extra entity WHERE entity.user_type_id IS NULL")
    Flux<UserExtra> findAllWhereUserTypeIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<UserExtra> findAll();

    @Override
    Mono<UserExtra> findById(Long id);

    @Override
    <S extends UserExtra> Mono<S> save(S entity);
}

interface UserExtraRepositoryInternal {
    <S extends UserExtra> Mono<S> insert(S entity);
    <S extends UserExtra> Mono<S> save(S entity);
    Mono<Integer> update(UserExtra entity);

    Flux<UserExtra> findAll();
    Mono<UserExtra> findById(Long id);
    Flux<UserExtra> findAllBy(Pageable pageable);
    Flux<UserExtra> findAllBy(Pageable pageable, Criteria criteria);
}
