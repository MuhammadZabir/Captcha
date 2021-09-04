package com.test.captcha.repository.rowmapper;

import com.test.captcha.domain.ItemStatus;
import com.test.captcha.domain.enumeration.AvailabilityStatus;
import com.test.captcha.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ItemStatus}, with proper type conversions.
 */
@Service
public class ItemStatusRowMapper implements BiFunction<Row, String, ItemStatus> {

    private final ColumnConverter converter;

    public ItemStatusRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ItemStatus} stored in the database.
     */
    @Override
    public ItemStatus apply(Row row, String prefix) {
        ItemStatus entity = new ItemStatus();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAmountAvailable(converter.fromRow(row, prefix + "_amount_available", Integer.class));
        entity.setAmountSold(converter.fromRow(row, prefix + "_amount_sold", Integer.class));
        entity.setAvailabilityStatus(converter.fromRow(row, prefix + "_availability_status", AvailabilityStatus.class));
        entity.setItemId(converter.fromRow(row, prefix + "_item_id", Long.class));
        return entity;
    }
}
