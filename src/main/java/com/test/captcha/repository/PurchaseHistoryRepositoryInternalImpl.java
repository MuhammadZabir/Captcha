package com.test.captcha.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.test.captcha.domain.PurchaseHistory;
import com.test.captcha.domain.enumeration.PaymentStatus;
import com.test.captcha.repository.rowmapper.CartRowMapper;
import com.test.captcha.repository.rowmapper.PurchaseHistoryRowMapper;
import com.test.captcha.repository.rowmapper.UserExtraRowMapper;
import com.test.captcha.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
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
 * Spring Data SQL reactive custom repository implementation for the PurchaseHistory entity.
 */
@SuppressWarnings("unused")
class PurchaseHistoryRepositoryInternalImpl implements PurchaseHistoryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CartRowMapper cartMapper;
    private final UserExtraRowMapper userextraMapper;
    private final PurchaseHistoryRowMapper purchasehistoryMapper;

    private static final Table entityTable = Table.aliased("purchase_history", EntityManager.ENTITY_ALIAS);
    private static final Table cartTable = Table.aliased("cart", "cart");
    private static final Table buyerTable = Table.aliased("user_extra", "buyer");

    public PurchaseHistoryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CartRowMapper cartMapper,
        UserExtraRowMapper userextraMapper,
        PurchaseHistoryRowMapper purchasehistoryMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.cartMapper = cartMapper;
        this.userextraMapper = userextraMapper;
        this.purchasehistoryMapper = purchasehistoryMapper;
    }

    @Override
    public Flux<PurchaseHistory> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<PurchaseHistory> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<PurchaseHistory> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PurchaseHistorySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CartSqlHelper.getColumns(cartTable, "cart"));
        columns.addAll(UserExtraSqlHelper.getColumns(buyerTable, "buyer"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(cartTable)
            .on(Column.create("cart_id", entityTable))
            .equals(Column.create("id", cartTable))
            .leftOuterJoin(buyerTable)
            .on(Column.create("buyer_id", entityTable))
            .equals(Column.create("id", buyerTable));

        String select = entityManager.createSelect(selectFrom, PurchaseHistory.class, pageable, criteria);
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
    public Flux<PurchaseHistory> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<PurchaseHistory> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private PurchaseHistory process(Row row, RowMetadata metadata) {
        PurchaseHistory entity = purchasehistoryMapper.apply(row, "e");
        entity.setCart(cartMapper.apply(row, "cart"));
        entity.setBuyer(userextraMapper.apply(row, "buyer"));
        return entity;
    }

    @Override
    public <S extends PurchaseHistory> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends PurchaseHistory> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update PurchaseHistory with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(PurchaseHistory entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class PurchaseHistorySqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("purchase_date", table, columnPrefix + "_purchase_date"));
        columns.add(Column.aliased("shipping_date", table, columnPrefix + "_shipping_date"));
        columns.add(Column.aliased("billing_address", table, columnPrefix + "_billing_address"));
        columns.add(Column.aliased("payment_status", table, columnPrefix + "_payment_status"));

        columns.add(Column.aliased("cart_id", table, columnPrefix + "_cart_id"));
        columns.add(Column.aliased("buyer_id", table, columnPrefix + "_buyer_id"));
        return columns;
    }
}
