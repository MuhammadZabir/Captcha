package com.test.captcha.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.test.captcha.domain.Cart} entity.
 */
public class CartDTO implements Serializable {

    private Long id;

    private Double totalPrice;

    private UserExtraDTO buyer;

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
            ", buyer=" + getBuyer() +
            "}";
    }
}
