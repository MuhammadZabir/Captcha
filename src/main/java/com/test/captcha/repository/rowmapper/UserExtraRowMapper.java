package com.test.captcha.repository.rowmapper;

import com.test.captcha.domain.UserExtra;
import com.test.captcha.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link UserExtra}, with proper type conversions.
 */
@Service
public class UserExtraRowMapper implements BiFunction<Row, String, UserExtra> {

    private final ColumnConverter converter;

    public UserExtraRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserExtra} stored in the database.
     */
    @Override
    public UserExtra apply(Row row, String prefix) {
        UserExtra entity = new UserExtra();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setBillingAddress(converter.fromRow(row, prefix + "_billing_address", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setUserTypeId(converter.fromRow(row, prefix + "_user_type_id", Long.class));
        return entity;
    }
}
