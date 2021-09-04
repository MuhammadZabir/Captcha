package com.test.captcha.repository;

import com.test.captcha.domain.CartBasket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CartBasket entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartBasketRepository extends R2dbcRepository<CartBasket, Long>, CartBasketRepositoryInternal {
    @Query("SELECT * FROM cart_basket entity WHERE entity.cart_id = :id")
    Flux<CartBasket> findByCart(Long id);

    @Query("SELECT * FROM cart_basket entity WHERE entity.cart_id IS NULL")
    Flux<CartBasket> findAllWhereCartIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<CartBasket> findAll();

    @Override
    Mono<CartBasket> findById(Long id);

    @Override
    <S extends CartBasket> Mono<S> save(S entity);
}

interface CartBasketRepositoryInternal {
    <S extends CartBasket> Mono<S> insert(S entity);
    <S extends CartBasket> Mono<S> save(S entity);
    Mono<Integer> update(CartBasket entity);

    Flux<CartBasket> findAll();
    Mono<CartBasket> findById(Long id);
    Flux<CartBasket> findAllBy(Pageable pageable);
    Flux<CartBasket> findAllBy(Pageable pageable, Criteria criteria);
}
