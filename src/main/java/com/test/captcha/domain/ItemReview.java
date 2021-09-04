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
 * The ItemReview entity.
 */
@Table("item_review")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "itemreview")
public class ItemReview implements Serializable {

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

    @JsonIgnoreProperties(value = { "images", "itemReviews", "shop" }, allowSetters = true)
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

    public ItemReview id(Long id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public ItemReview description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRating() {
        return this.rating;
    }

    public ItemReview rating(Integer rating) {
        this.rating = rating;
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDate getReviewDate() {
        return this.reviewDate;
    }

    public ItemReview reviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
        return this;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public UserExtra getReviewer() {
        return this.reviewer;
    }

    public ItemReview reviewer(UserExtra userExtra) {
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

    public Item getItem() {
        return this.item;
    }

    public ItemReview item(Item item) {
        this.setItem(item);
        this.itemId = item != null ? item.getId() : null;
        return this;
    }

    public void setItem(Item item) {
        this.item = item;
        this.itemId = item != null ? item.getId() : null;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public void setItemId(Long item) {
        this.itemId = item;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemReview)) {
            return false;
        }
        return id != null && id.equals(((ItemReview) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemReview{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", rating=" + getRating() +
            ", reviewDate='" + getReviewDate() + "'" +
            "}";
    }
}
