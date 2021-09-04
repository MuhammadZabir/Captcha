package com.test.captcha.repository.rowmapper;

import com.test.captcha.domain.ItemReview;
import com.test.captcha.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ItemReview}, with proper type conversions.
 */
@Service
public class ItemReviewRowMapper implements BiFunction<Row, String, ItemReview> {

    private final ColumnConverter converter;

    public ItemReviewRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ItemReview} stored in the database.
     */
    @Override
    public ItemReview apply(Row row, String prefix) {
        ItemReview entity = new ItemReview();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Integer.class));
        entity.setReviewDate(converter.fromRow(row, prefix + "_review_date", LocalDate.class));
        entity.setReviewerId(converter.fromRow(row, prefix + "_reviewer_id", Long.class));
        entity.setItemId(converter.fromRow(row, prefix + "_item_id", Long.class));
        return entity;
    }
}
