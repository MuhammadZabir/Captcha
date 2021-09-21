package com.test.captcha.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.test.captcha.domain.CartBasket} entity.
 */
public class CartBasketDTO implements Serializable {

    private Long id;

    private Integer amount;

    private CartDTO cart;

    private ItemDTO item;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public CartDTO getCart() {
        return cart;
    }

    public void setCart(CartDTO cart) {
        this.cart = cart;
    }

    public ItemDTO getItem() {
        return item;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartBasketDTO)) {
            return false;
        }

        CartBasketDTO cartBasketDTO = (CartBasketDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cartBasketDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CartBasketDTO{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", cart=" + getCart() +
            ", item=" + getItem() +
            "}";
    }
}
