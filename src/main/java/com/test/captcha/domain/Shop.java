package com.test.captcha.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The Shop entity.
 */
@Table("shop")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "shop")
public class Shop implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("created_date")
    private LocalDate createdDate;

    private Long ownerId;

    @Transient
    private UserExtra owner;

    @Transient
    @JsonIgnoreProperties(value = { "images", "itemReviews", "shop" }, allowSetters = true)
    private Set<Item> items = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "reviewer", "shop" }, allowSetters = true)
    private Set<ShopReview> shopReviews = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shop id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Shop name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Shop description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    public Shop createdDate(LocalDate createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public UserExtra getOwner() {
        return this.owner;
    }

    public Shop owner(UserExtra userExtra) {
        this.setOwner(userExtra);
        this.ownerId = userExtra != null ? userExtra.getId() : null;
        return this;
    }

    public void setOwner(UserExtra userExtra) {
        this.owner = userExtra;
        this.ownerId = userExtra != null ? userExtra.getId() : null;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(Long userExtra) {
        this.ownerId = userExtra;
    }

    public Set<Item> getItems() {
        return this.items;
    }

    public Shop items(Set<Item> items) {
        this.setItems(items);
        return this;
    }

    public Shop addItem(Item item) {
        this.items.add(item);
        item.setShop(this);
        return this;
    }

    public Shop removeItem(Item item) {
        this.items.remove(item);
        item.setShop(null);
        return this;
    }

    public void setItems(Set<Item> items) {
        if (this.items != null) {
            this.items.forEach(i -> i.setShop(null));
        }
        if (items != null) {
            items.forEach(i -> i.setShop(this));
        }
        this.items = items;
    }

    public Set<ShopReview> getShopReviews() {
        return this.shopReviews;
    }

    public Shop shopReviews(Set<ShopReview> shopReviews) {
        this.setShopReviews(shopReviews);
        return this;
    }

    public Shop addShopReview(ShopReview shopReview) {
        this.shopReviews.add(shopReview);
        shopReview.setShop(this);
        return this;
    }

    public Shop removeShopReview(ShopReview shopReview) {
        this.shopReviews.remove(shopReview);
        shopReview.setShop(null);
        return this;
    }

    public void setShopReviews(Set<ShopReview> shopReviews) {
        if (this.shopReviews != null) {
            this.shopReviews.forEach(i -> i.setShop(null));
        }
        if (shopReviews != null) {
            shopReviews.forEach(i -> i.setShop(this));
        }
        this.shopReviews = shopReviews;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shop)) {
            return false;
        }
        return id != null && id.equals(((Shop) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Shop{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            "}";
    }
}
