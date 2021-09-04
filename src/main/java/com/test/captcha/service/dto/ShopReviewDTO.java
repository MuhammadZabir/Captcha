package com.test.captcha.service.dto;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.test.captcha.domain.ShopReview} entity.
 */
@ApiModel(description = "The ShopReview entity.")
public class ShopReviewDTO implements Serializable {

    private Long id;

    private String description;

    private Integer rating;

    private LocalDate reviewDate;

    private UserExtraDTO reviewer;

    private ShopDTO shop;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public UserExtraDTO getReviewer() {
        return reviewer;
    }

    public void setReviewer(UserExtraDTO reviewer) {
        this.reviewer = reviewer;
    }

    public ShopDTO getShop() {
        return shop;
    }

    public void setShop(ShopDTO shop) {
        this.shop = shop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShopReviewDTO)) {
            return false;
        }

        ShopReviewDTO shopReviewDTO = (ShopReviewDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shopReviewDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShopReviewDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", rating=" + getRating() +
            ", reviewDate='" + getReviewDate() + "'" +
            ", reviewer=" + getReviewer() +
            ", shop=" + getShop() +
            "}";
    }
}
