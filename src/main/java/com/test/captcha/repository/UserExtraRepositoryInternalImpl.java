package com.test.captcha.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.test.captcha.domain.UserExtra;
import com.test.captcha.repository.rowmapper.UserExtraRowMapper;
import com.test.captcha.repository.rowmapper.UserRowMapper;
import com.test.captcha.repository.rowmapper.UserTypeRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the UserExtra entity.
 */
@SuppressWarnings("unused")
class UserExtraRepositoryInternalImpl implements UserExtraRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final UserTypeRowMapper usertypeMapper;
    private final UserExtraRowMapper userextraMapper;

    private static final Table entityTable = Table.aliased("user_extra", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table userTypeTable = Table.aliased("user_type", "userType");

    public UserExtraRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        UserTypeRowMapper usertypeMapper,
        UserExtraRowMapper userextraMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.usertypeMapper = usertypeMapper;
        this.userextraMapper = userextraMapper;
    }

    @Override
    public Flux<UserExtra> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<UserExtra> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<UserExtra> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = UserExtraSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(UserTypeSqlHelper.getColumns(userTypeTable, "userType"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(userTypeTable)
            .on(Column.create("user_type_id", entityTable))
            .equals(Column.create("id", userTypeTable));

        String select = entityManager.createSelect(selectFrom, UserExtra.class, pageable, criteria);
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
    public Flux<UserExtra> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<UserExtra> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private UserExtra process(Row row, RowMetadata metadata) {
        UserExtra entity = userextraMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setUserType(usertypeMapper.apply(row, "userType"));
        return entity;
    }

    @Override
    public <S extends UserExtra> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends UserExtra> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update UserExtra with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(UserExtra entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class UserExtraSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("billing_address", table, columnPrefix + "_billing_address"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("user_type_id", table, columnPrefix + "_user_type_id"));
        return columns;
    }
}
