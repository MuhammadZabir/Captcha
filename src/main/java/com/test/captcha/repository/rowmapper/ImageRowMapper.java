package com.test.captcha.repository.rowmapper;

import com.test.captcha.domain.Image;
import com.test.captcha.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Image}, with proper type conversions.
 */
@Service
public class ImageRowMapper implements BiFunction<Row, String, Image> {

    private final ColumnConverter converter;

    public ImageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Image} stored in the database.
     */
    @Override
    public Image apply(Row row, String prefix) {
        Image entity = new Image();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setImageDir(converter.fromRow(row, prefix + "_image_dir", String.class));
        entity.setItemId(converter.fromRow(row, prefix + "_item_id", Long.class));
        return entity;
    }
}
