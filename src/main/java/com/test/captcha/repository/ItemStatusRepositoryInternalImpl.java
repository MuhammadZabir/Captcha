package com.test.captcha.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.test.captcha.domain.ItemStatus;
import com.test.captcha.domain.enumeration.AvailabilityStatus;
import com.test.captcha.repository.rowmapper.ItemRowMapper;
import com.test.captcha.repository.rowmapper.ItemStatusRowMapper;
import com.test.captcha.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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
 * Spring Data SQL reactive custom repository implementation for the ItemStatus entity.
 */
@SuppressWarnings("unused")
class ItemStatusRepositoryInternalImpl implements ItemStatusRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ItemRowMapper itemMapper;
    private final ItemStatusRowMapper itemstatusMapper;

    private static final Table entityTable = Table.aliased("item_status", EntityManager.ENTITY_ALIAS);
    private static final Table itemTable = Table.aliased("item", "item");

    public ItemStatusRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ItemRowMapper itemMapper,
        ItemStatusRowMapper itemstatusMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.itemMapper = itemMapper;
        this.itemstatusMapper = itemstatusMapper;
    }

    @Override
    public Flux<ItemStatus> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<ItemStatus> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<ItemStatus> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ItemStatusSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ItemSqlHelper.getColumns(itemTable, "item"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(itemTable)
            .on(Column.create("item_id", entityTable))
            .equals(Column.create("id", itemTable));

        String select = entityManager.createSelect(selectFrom, ItemStatus.class, pageable, criteria);
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
    public Flux<ItemStatus> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<ItemStatus> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private ItemStatus process(Row row, RowMetadata metadata) {
        ItemStatus entity = itemstatusMapper.apply(row, "e");
        entity.setItem(itemMapper.apply(row, "item"));
        return entity;
    }

    @Override
    public <S extends ItemStatus> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends ItemStatus> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update ItemStatus with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(ItemStatus entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class ItemStatusSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("amount_available", table, columnPrefix + "_amount_available"));
        columns.add(Column.aliased("amount_sold", table, columnPrefix + "_amount_sold"));
        columns.add(Column.aliased("availability_status", table, columnPrefix + "_availability_status"));

        columns.add(Column.aliased("item_id", table, columnPrefix + "_item_id"));
        return columns;
    }
}
