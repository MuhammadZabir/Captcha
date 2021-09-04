package com.test.captcha.repository.rowmapper;

import com.test.captcha.domain.Item;
import com.test.captcha.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Item}, with proper type conversions.
 */
@Service
public class ItemRowMapper implements BiFunction<Row, String, Item> {

    private final ColumnConverter converter;

    public ItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Item} stored in the database.
     */
    @Override
    public Item apply(Row row, String prefix) {
        Item entity = new Item();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setCategory(converter.fromRow(row, prefix + "_category", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", Double.class));
        entity.setShopId(converter.fromRow(row, prefix + "_shop_id", Long.class));
        return entity;
    }
}
