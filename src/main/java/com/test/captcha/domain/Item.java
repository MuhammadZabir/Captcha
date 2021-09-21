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
 * The Item entity.
 */
@Table("item")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "item")
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("category")
    private String category;

    @Column("price")
    private Double price;

    @Transient
    @JsonIgnoreProperties(value = { "item" }, allowSetters = true)
    private Set<Image> images = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "reviewer", "item" }, allowSetters = true)
    private Set<ItemReview> itemReviews = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "item", "cart" }, allowSetters = true)
    private Set<CartBasket> cartBaskets = new HashSet<>();

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

    public Item id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Item name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Item description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return this.category;
    }

    public Item category(String category) {
        this.category = category;
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return this.price;
    }

    public Item price(Double price) {
        this.price = price;
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Set<Image> getImages() {
        return this.images;
    }

    public Item images(Set<Image> images) {
        this.setImages(images);
        return this;
    }

    public Item addImage(Image image) {
        this.images.add(image);
        image.setItem(this);
        return this;
    }

    public Item removeImage(Image image) {
        this.images.remove(image);
        image.setItem(null);
        return this;
    }

    public void setImages(Set<Image> images) {
        if (this.images != null) {
            this.images.forEach(i -> i.setItem(null));
        }
        if (images != null) {
            images.forEach(i -> i.setItem(this));
        }
        this.images = images;
    }

    public Set<ItemReview> getItemReviews() {
        return this.itemReviews;
    }

    public Item itemReviews(Set<ItemReview> itemReviews) {
        this.setItemReviews(itemReviews);
        return this;
    }

    public Item addItemReview(ItemReview itemReview) {
        this.itemReviews.add(itemReview);
        itemReview.setItem(this);
        return this;
    }

    public Item removeItemReview(ItemReview itemReview) {
        this.itemReviews.remove(itemReview);
        itemReview.setItem(null);
        return this;
    }

    public void setItemReviews(Set<ItemReview> itemReviews) {
        if (this.itemReviews != null) {
            this.itemReviews.forEach(i -> i.setItem(null));
        }
        if (itemReviews != null) {
            itemReviews.forEach(i -> i.setItem(this));
        }
        this.itemReviews = itemReviews;
    }

    public Set<CartBasket> getCartBaskets() {
        return this.cartBaskets;
    }

    public Item cartBaskets(Set<CartBasket> cartBaskets) {
        this.setCartBaskets(cartBaskets);
        return this;
    }

    public Item addCartBasket(CartBasket cartBasket) {
        this.cartBaskets.add(cartBasket);
        cartBasket.setItem(this);
        return this;
    }

    public Item removeCartBasket(CartBasket cartBasket) {
        this.cartBaskets.remove(cartBasket);
        cartBasket.setItem(null);
        return this;
    }

    public void setCartBaskets(Set<CartBasket> cartBaskets) {
        if (this.cartBaskets != null) {
            this.cartBaskets.forEach(i -> i.setItem(null));
        }
        if (cartBaskets != null) {
            cartBaskets.forEach(i -> i.setItem(this));
        }
        this.cartBaskets = cartBaskets;
    }

    public Shop getShop() {
        return this.shop;
    }

    public Item shop(Shop shop) {
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
        if (!(o instanceof Item)) {
            return false;
        }
        return id != null && id.equals(((Item) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Item{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", category='" + getCategory() + "'" +
            ", price=" + getPrice() +
            "}";
    }
}
