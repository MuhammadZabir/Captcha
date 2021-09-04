package com.test.captcha.service.dto;

import com.test.captcha.domain.enumeration.PaymentStatus;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.test.captcha.domain.PurchaseHistory} entity.
 */
@ApiModel(description = "The PurchaseHistory entity.")
public class PurchaseHistoryDTO implements Serializable {

    private Long id;

    private LocalDate purchaseDate;

    private LocalDate shippingDate;

    private String billingAddress;

    private PaymentStatus paymentStatus;

    private CartDTO cart;

    private UserExtraDTO buyer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(LocalDate shippingDate) {
        this.shippingDate = shippingDate;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public CartDTO getCart() {
        return cart;
    }

    public void setCart(CartDTO cart) {
        this.cart = cart;
    }

    public UserExtraDTO getBuyer() {
        return buyer;
    }

    public void setBuyer(UserExtraDTO buyer) {
        this.buyer = buyer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseHistoryDTO)) {
            return false;
        }

        PurchaseHistoryDTO purchaseHistoryDTO = (PurchaseHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, purchaseHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseHistoryDTO{" +
            "id=" + getId() +
            ", purchaseDate='" + getPurchaseDate() + "'" +
            ", shippingDate='" + getShippingDate() + "'" +
            ", billingAddress='" + getBillingAddress() + "'" +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", cart=" + getCart() +
            ", buyer=" + getBuyer() +
            "}";
    }
}
