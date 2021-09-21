package com.test.captcha.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.test.captcha.domain.CartBasket;
import com.test.captcha.repository.rowmapper.CartBasketRowMapper;
import com.test.captcha.repository.rowmapper.CartRowMapper;
import com.test.captcha.repository.rowmapper.ItemRowMapper;
import com.test.captcha.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the CartBasket entity.
 */
@SuppressWarnings("unused")
class CartBasketRepositoryInternalImpl implements CartBasketRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CartRowMapper cartMapper;
    private final ItemRowMapper itemMapper;
    private final CartBasketRowMapper cartBasketMapper;

    private static final Table entityTable = Table.aliased("cart_basket", EntityManager.ENTITY_ALIAS);
    private static final Table cartTable = Table.aliased("cart", "cart");
    private static final Table itemTable = Table.aliased("item", "item");

    public CartBasketRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CartRowMapper cartMapper,
        ItemRowMapper itemMapper,
        CartBasketRowMapper cartBasketMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.cartMapper = cartMapper;
        this.itemMapper = itemMapper;
        this.cartBasketMapper = cartBasketMapper;
    }

    @Override
    public Flux<CartBasket> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<CartBasket> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<CartBasket> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CartBasketSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CartSqlHelper.getColumns(cartTable, "cart"));
        columns.addAll(ItemSqlHelper.getColumns(itemTable, "item"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(cartTable)
            .on(Column.create("cart_id", entityTable))
            .equals(Column.create("id", cartTable))
            .leftOuterJoin(itemTable)
            .on(Column.create("item_id", entityTable))
            .equals(Column.create("id", itemTable));

        String select = entityManager.createSelect(selectFrom, CartBasket.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<CartBasket> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<CartBasket> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private CartBasket process(Row row, RowMetadata metadata) {
        CartBasket entity = cartBasketMapper.apply(row, "e");
        entity.setCart(cartMapper.apply(row, "cart"));
        entity.setItem(itemMapper.apply(row, "item"));
        return entity;
    }

    @Override
    public <S extends CartBasket> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends CartBasket> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update CartBasket with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(CartBasket entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class CartBasketSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("amount", table, columnPrefix + "_amount"));

        columns.add(Column.aliased("cart_id", table, columnPrefix + "_cart_id"));
        columns.add(Column.aliased("item_id", table, columnPrefix + "_item_id"));
        return columns;
    }
}
