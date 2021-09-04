package com.test.captcha.repository.rowmapper;

import com.test.captcha.domain.ShopReview;
import com.test.captcha.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ShopReview}, with proper type conversions.
 */
@Service
public class ShopReviewRowMapper implements BiFunction<Row, String, ShopReview> {

    private final ColumnConverter converter;

    public ShopReviewRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ShopReview} stored in the database.
     */
    @Override
    public ShopReview apply(Row row, String prefix) {
        ShopReview entity = new ShopReview();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Integer.class));
        entity.setReviewDate(converter.fromRow(row, prefix + "_review_date", LocalDate.class));
        entity.setReviewerId(converter.fromRow(row, prefix + "_reviewer_id", Long.class));
        entity.setShopId(converter.fromRow(row, prefix + "_shop_id", Long.class));
        return entity;
    }
}
