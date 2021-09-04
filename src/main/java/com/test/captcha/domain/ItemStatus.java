package com.test.captcha.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.test.captcha.domain.enumeration.AvailabilityStatus;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The ItemStatus entity.
 */
@Table("item_status")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "itemstatus")
public class ItemStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("amount_available")
    private Integer amountAvailable;

    @Column("amount_sold")
    private Integer amountSold;

    @Column("availability_status")
    private AvailabilityStatus availabilityStatus;

    private Long itemId;

    @Transient
    private Item item;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemStatus id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getAmountAvailable() {
        return this.amountAvailable;
    }

    public ItemStatus amountAvailable(Integer amountAvailable) {
        this.amountAvailable = amountAvailable;
        return this;
    }

    public void setAmountAvailable(Integer amountAvailable) {
        this.amountAvailable = amountAvailable;
    }

    public Integer getAmountSold() {
        return this.amountSold;
    }

    public ItemStatus amountSold(Integer amountSold) {
        this.amountSold = amountSold;
        return this;
    }

    public void setAmountSold(Integer amountSold) {
        this.amountSold = amountSold;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return this.availabilityStatus;
    }

    public ItemStatus availabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
        return this;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Item getItem() {
        return this.item;
    }

    public ItemStatus item(Item item) {
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
        if (!(o instanceof ItemStatus)) {
            return false;
        }
        return id != null && id.equals(((ItemStatus) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemStatus{" +
            "id=" + getId() +
            ", amountAvailable=" + getAmountAvailable() +
            ", amountSold=" + getAmountSold() +
            ", availabilityStatus='" + getAvailabilityStatus() + "'" +
            "}";
    }
}
