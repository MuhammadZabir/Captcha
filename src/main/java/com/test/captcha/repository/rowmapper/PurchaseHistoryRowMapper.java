package com.test.captcha.repository.rowmapper;

import com.test.captcha.domain.PurchaseHistory;
import com.test.captcha.domain.enumeration.PaymentStatus;
import com.test.captcha.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PurchaseHistory}, with proper type conversions.
 */
@Service
public class PurchaseHistoryRowMapper implements BiFunction<Row, String, PurchaseHistory> {

    private final ColumnConverter converter;

    public PurchaseHistoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PurchaseHistory} stored in the database.
     */
    @Override
    public PurchaseHistory apply(Row row, String prefix) {
        PurchaseHistory entity = new PurchaseHistory();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPurchaseDate(converter.fromRow(row, prefix + "_purchase_date", LocalDate.class));
        entity.setShippingDate(converter.fromRow(row, prefix + "_shipping_date", LocalDate.class));
        entity.setBillingAddress(converter.fromRow(row, prefix + "_billing_address", String.class));
        entity.setPaymentStatus(converter.fromRow(row, prefix + "_payment_status", PaymentStatus.class));
        entity.setCartId(converter.fromRow(row, prefix + "_cart_id", Long.class));
        entity.setBuyerId(converter.fromRow(row, prefix + "_buyer_id", Long.class));
        return entity;
    }
}
