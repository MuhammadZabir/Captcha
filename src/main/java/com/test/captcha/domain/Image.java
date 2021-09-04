package com.test.captcha.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The Image entity.
 */
@Table("image")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "image")
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("image_dir")
    private String imageDir;

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

    public Image id(Long id) {
        this.id = id;
        return this;
    }

    public String getImageDir() {
        return this.imageDir;
    }

    public Image imageDir(String imageDir) {
        this.imageDir = imageDir;
        return this;
    }

    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

    public Item getItem() {
        return this.item;
    }

    public Image item(Item item) {
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
        if (!(o instanceof Image)) {
            return false;
        }
        return id != null && id.equals(((Image) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Image{" +
            "id=" + getId() +
            ", imageDir='" + getImageDir() + "'" +
            "}";
    }
}
