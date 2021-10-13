package com.test.captcha.service.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.test.captcha.domain.Cart} entity.
 */
public class CartDTO implements Serializable {

    private Long id;

    private Double totalPrice;

    private String captcha;

    private String hiddenCaptcha;

    private String realCaptcha;

    @JsonBackReference
    private UserExtraDTO buyer;

    @JsonManagedReference
    private Set<CartBasketDTO> cartBaskets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getHiddenCaptcha() {
        return hiddenCaptcha;
    }

    public void setHiddenCaptcha(String hiddenCaptcha) {
        this.hiddenCaptcha = hiddenCaptcha;
    }

    public String getRealCaptcha() {
        return realCaptcha;
    }

    public void setRealCaptcha(String realCaptcha) {
        this.realCaptcha = realCaptcha;
    }

    public UserExtraDTO getBuyer() {
        return buyer;
    }

    public void setBuyer(UserExtraDTO buyer) {
        this.buyer = buyer;
    }

    public Set<CartBasketDTO> getCartBaskets() {
        return this.cartBaskets;
    }

    public CartDTO cartBaskets(Set<CartBasketDTO> cartBaskets) {
        this.setCartBaskets(cartBaskets);
        return this;
    }

    public CartDTO addCartBasket(CartBasketDTO cartBasket) {
        this.cartBaskets.add(cartBasket);
        cartBasket.setCart(this);
        return this;
    }

    public CartDTO removeCartBasket(CartBasketDTO cartBasket) {
        this.cartBaskets.remove(cartBasket);
        cartBasket.setCart(null);
        return this;
    }

    public void setCartBaskets(Set<CartBasketDTO> cartBaskets) {
        if (this.cartBaskets != null) {
            this.cartBaskets.forEach(i -> i.setCart(null));
        }
        if (cartBaskets != null) {
            cartBaskets.forEach(i -> i.setCart(this));
        }
        this.cartBaskets = cartBaskets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartDTO)) {
            return false;
        }

        CartDTO cartDTO = (CartDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cartDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CartDTO{" +
            "id=" + getId() +
            ", totalPrice=" + getTotalPrice() +
            ", getCaptcha=" + getCaptcha() +
            ", getHiddenCaptcha=" + getHiddenCaptcha() +
            ", getRealCaptcha=" + getRealCaptcha() +
            ", buyer=" + getBuyer() +
            "}";
    }
}
