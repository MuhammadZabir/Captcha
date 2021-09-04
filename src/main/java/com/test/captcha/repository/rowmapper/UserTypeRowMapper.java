package com.test.captcha.repository.rowmapper;

import com.test.captcha.domain.UserType;
import com.test.captcha.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link UserType}, with proper type conversions.
 */
@Service
public class UserTypeRowMapper implements BiFunction<Row, String, UserType> {

    private final ColumnConverter converter;

    public UserTypeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserType} stored in the database.
     */
    @Override
    public UserType apply(Row row, String prefix) {
        UserType entity = new UserType();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
