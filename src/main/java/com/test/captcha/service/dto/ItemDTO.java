package com.test.captcha.service.dto;

import com.test.captcha.domain.Image;
import com.test.captcha.domain.Item;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.test.captcha.domain.Item} entity.
 */
@ApiModel(description = "The Item entity.")
public class ItemDTO implements Serializable {

    private Long id;

    private String name;

    private String description;

    private String category;

    private Double price;

    private ShopDTO shop;

    private Set<ImageDTO> images;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public ShopDTO getShop() {
        return shop;
    }

    public void setShop(ShopDTO shop) {
        this.shop = shop;
    }

    public Set<ImageDTO> getImages() {
        return images;
    }

    public void setImages(Set<ImageDTO> images) {
        if (this.images != null) {
            this.images.forEach(i -> i.setItem(null));
        }
        if (images != null) {
            images.forEach(i -> i.setItem(this));
        }
        this.images = images;
    }

    public ItemDTO images(Set<ImageDTO> images) {
        this.setImages(images);
        return this;
    }

    public ItemDTO addImage(ImageDTO image) {
        this.images.add(image);
        image.setItem(this);
        return this;
    }

    public ItemDTO removeImage(ImageDTO image) {
        this.images.remove(image);
        image.setItem(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemDTO)) {
            return false;
        }

        ItemDTO itemDTO = (ItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, itemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", category='" + getCategory() + "'" +
            ", price=" + getPrice() +
            ", shop=" + getShop() +
            "}";
    }
}
