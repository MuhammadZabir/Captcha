package com.test.captcha.repository;

import com.test.captcha.domain.PurchaseHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PurchaseHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PurchaseHistoryRepository extends R2dbcRepository<PurchaseHistory, Long>, PurchaseHistoryRepositoryInternal {
    @Query("SELECT * FROM purchase_history entity WHERE entity.cart_id = :id")
    Flux<PurchaseHistory> findByCart(Long id);

    @Query("SELECT * FROM purchase_history entity WHERE entity.cart_id IS NULL")
    Flux<PurchaseHistory> findAllWhereCartIsNull();

    @Query("SELECT * FROM purchase_history entity WHERE entity.buyer_id = :id")
    Flux<PurchaseHistory> findByBuyer(Long id);

    @Query("SELECT * FROM purchase_history entity WHERE entity.buyer_id IS NULL")
    Flux<PurchaseHistory> findAllWhereBuyerIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<PurchaseHistory> findAll();

    @Override
    Mono<PurchaseHistory> findById(Long id);

    @Override
    <S extends PurchaseHistory> Mono<S> save(S entity);
}

interface PurchaseHistoryRepositoryInternal {
    <S extends PurchaseHistory> Mono<S> insert(S entity);
    <S extends PurchaseHistory> Mono<S> save(S entity);
    Mono<Integer> update(PurchaseHistory entity);

    Flux<PurchaseHistory> findAll();
    Mono<PurchaseHistory> findById(Long id);
    Flux<PurchaseHistory> findAllBy(Pageable pageable);
    Flux<PurchaseHistory> findAllBy(Pageable pageable, Criteria criteria);
}
