package com.test.captcha.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The ShopReview entity.
 */
@Table("shop_review")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "shopreview")
public class ShopReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("description")
    private String description;

    @Column("rating")
    private Integer rating;

    @Column("review_date")
    private LocalDate reviewDate;

    private Long reviewerId;

    @Transient
    private UserExtra reviewer;

    @JsonIgnoreProperties(value = { "owner", "items", "shopReviews" }, allowSetters = true)
    @Transient
    private Shop shop;

    @Column("shop_id")
    private Long shopId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ShopReview id(Long id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public ShopReview description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRating() {
        return this.rating;
    }

    public ShopReview rating(Integer rating) {
        this.rating = rating;
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDate getReviewDate() {
        return this.reviewDate;
    }

    public ShopReview reviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
        return this;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public UserExtra getReviewer() {
        return this.reviewer;
    }

    public ShopReview reviewer(UserExtra userExtra) {
        this.setReviewer(userExtra);
        this.reviewerId = userExtra != null ? userExtra.getId() : null;
        return this;
    }

    public void setReviewer(UserExtra userExtra) {
        this.reviewer = userExtra;
        this.reviewerId = userExtra != null ? userExtra.getId() : null;
    }

    public Long getReviewerId() {
        return this.reviewerId;
    }

    public void setReviewerId(Long userExtra) {
        this.reviewerId = userExtra;
    }

    public Shop getShop() {
        return this.shop;
    }

    public ShopReview shop(Shop shop) {
        this.setShop(shop);
        this.shopId = shop != null ? shop.getId() : null;
        return this;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
        this.shopId = shop != null ? shop.getId() : null;
    }

    public Long getShopId() {
        return this.shopId;
    }

    public void setShopId(Long shop) {
        this.shopId = shop;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShopReview)) {
            return false;
        }
        return id != null && id.equals(((ShopReview) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShopReview{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", rating=" + getRating() +
            ", reviewDate='" + getReviewDate() + "'" +
            "}";
    }
}
