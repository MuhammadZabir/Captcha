package com.test.captcha.repository.rowmapper;

import com.test.captcha.domain.CartBasket;
import com.test.captcha.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CartBasket}, with proper type conversions.
 */
@Service
public class CartBasketRowMapper implements BiFunction<Row, String, CartBasket> {

    private final ColumnConverter converter;

    public CartBasketRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CartBasket} stored in the database.
     */
    @Override
    public CartBasket apply(Row row, String prefix) {
        CartBasket entity = new CartBasket();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAmount(converter.fromRow(row, prefix + "_amount", Integer.class));
        entity.setCartId(converter.fromRow(row, prefix + "_cart_id", Long.class));
        return entity;
    }
}
