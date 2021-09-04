package com.test.captcha.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Cart.
 */
@Table("cart")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cart")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("total_price")
    private Double totalPrice;

    private Long buyerId;

    @Transient
    private UserExtra buyer;

    @Transient
    @JsonIgnoreProperties(value = { "cart" }, allowSetters = true)
    private Set<CartBasket> cartBaskets = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart id(Long id) {
        this.id = id;
        return this;
    }

    public Double getTotalPrice() {
        return this.totalPrice;
    }

    public Cart totalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public UserExtra getBuyer() {
        return this.buyer;
    }

    public Cart buyer(UserExtra userExtra) {
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

    public Set<CartBasket> getCartBaskets() {
        return this.cartBaskets;
    }

    public Cart cartBaskets(Set<CartBasket> cartBaskets) {
        this.setCartBaskets(cartBaskets);
        return this;
    }

    public Cart addCartBasket(CartBasket cartBasket) {
        this.cartBaskets.add(cartBasket);
        cartBasket.setCart(this);
        return this;
    }

    public Cart removeCartBasket(CartBasket cartBasket) {
        this.cartBaskets.remove(cartBasket);
        cartBasket.setCart(null);
        return this;
    }

    public void setCartBaskets(Set<CartBasket> cartBaskets) {
        if (this.cartBaskets != null) {
            this.cartBaskets.forEach(i -> i.setCart(null));
        }
        if (cartBaskets != null) {
            cartBaskets.forEach(i -> i.setCart(this));
        }
        this.cartBaskets = cartBaskets;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cart)) {
            return false;
        }
        return id != null && id.equals(((Cart) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cart{" +
            "id=" + getId() +
            ", totalPrice=" + getTotalPrice() +
            "}";
    }
}
