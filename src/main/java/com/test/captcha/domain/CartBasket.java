package com.test.captcha.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A CartBasket.
 */
@Table("cart_basket")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cartbasket")
public class CartBasket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("amount")
    private Integer amount;

    @JsonIgnoreProperties(value = { "buyer", "cartBaskets" }, allowSetters = true)
    @Transient
    private Cart cart;

    @Column("cart_id")
    private Long cartId;

    @JsonIgnoreProperties(value = { "images", "itemReviews", "shop", "cartBaskets" }, allowSetters = true)
    @Transient
    private Item item;

    @Column("item_id")
    private Long itemId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CartBasket id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public CartBasket amount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Cart getCart() {
        return this.cart;
    }

    public CartBasket cart(Cart cart) {
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

    public Item getItem() {
        return item;
    }

    public CartBasket item(Item item) {
        this.setItem(item);
        this.itemId = item != null ? item.getId() : null;
        return this;
    }

    public void setItem(Item item) {
        this.item = item;
        this.itemId = item != null ? item.getId() : null;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartBasket)) {
            return false;
        }
        return id != null && id.equals(((CartBasket) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CartBasket{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            "}";
    }
}
