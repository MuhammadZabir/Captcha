package com.test.captcha.repository.rowmapper;

import com.test.captcha.domain.Shop;
import com.test.captcha.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Shop}, with proper type conversions.
 */
@Service
public class ShopRowMapper implements BiFunction<Row, String, Shop> {

    private final ColumnConverter converter;

    public ShopRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Shop} stored in the database.
     */
    @Override
    public Shop apply(Row row, String prefix) {
        Shop entity = new Shop();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", LocalDate.class));
        entity.setOwnerId(converter.fromRow(row, prefix + "_owner_id", Long.class));
        return entity;
    }
}
