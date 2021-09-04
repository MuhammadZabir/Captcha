package com.test.captcha.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.test.captcha.domain.enumeration.PaymentStatus;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The PurchaseHistory entity.
 */
@Table("purchase_history")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "purchasehistory")
public class PurchaseHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("purchase_date")
    private LocalDate purchaseDate;

    @Column("shipping_date")
    private LocalDate shippingDate;

    @Column("billing_address")
    private String billingAddress;

    @Column("payment_status")
    private PaymentStatus paymentStatus;

    private Long cartId;

    @Transient
    private Cart cart;

    @JsonIgnoreProperties(value = { "user", "purchaseHistories", "userType" }, allowSetters = true)
    @Transient
    private UserExtra buyer;

    @Column("buyer_id")
    private Long buyerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PurchaseHistory id(Long id) {
        this.id = id;
        return this;
    }

    public LocalDate getPurchaseDate() {
        return this.purchaseDate;
    }

    public PurchaseHistory purchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
        return this;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getShippingDate() {
        return this.shippingDate;
    }

    public PurchaseHistory shippingDate(LocalDate shippingDate) {
        this.shippingDate = shippingDate;
        return this;
    }

    public void setShippingDate(LocalDate shippingDate) {
        this.shippingDate = shippingDate;
    }

    public String getBillingAddress() {
        return this.billingAddress;
    }

    public PurchaseHistory billingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
        return this;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public PaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public PurchaseHistory paymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Cart getCart() {
        return this.cart;
    }

    public PurchaseHistory cart(Cart cart) {
        this.setCart(cart);
        this.cartId = cart != null ? cart.getId() : null;
        return this;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
        this.cartId = cart != null ? cart.getId() : null;
    }

    public Long getCartId() {
        return this.cartId;
    }

    public void setCartId(Long cart) {
        this.cartId = cart;
    }

    public UserExtra getBuyer() {
        return this.buyer;
    }

    public PurchaseHistory buyer(UserExtra userExtra) {
        this.setBuyer(userExtra);
        this.buyerId = userExtra != null ? userExtra.getId() : null;
        return this;
    }

    public void setBuyer(UserExtra userExtra) {
        this.buyer = userExtra;
        this.buyerId = userExtra != null ? userExtra.getId() : null;
    }

    public Long getBuyerId() {
        return this.buyerId;
    }

    public void setBuyerId(Long userExtra) {
        this.buyerId = userExtra;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseHistory)) {
            return false;
        }
        return id != null && id.equals(((PurchaseHistory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseHistory{" +
            "id=" + getId() +
            ", purchaseDate='" + getPurchaseDate() + "'" +
            ", shippingDate='" + getShippingDate() + "'" +
            ", billingAddress='" + getBillingAddress() + "'" +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            "}";
    }
}
