package com.test.captcha.service.dto;

import com.test.captcha.domain.enumeration.AvailabilityStatus;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.test.captcha.domain.ItemStatus} entity.
 */
@ApiModel(description = "The ItemStatus entity.")
public class ItemStatusDTO implements Serializable {

    private Long id;

    private Integer amountAvailable;

    private Integer amountSold;

    private AvailabilityStatus availabilityStatus;

    private ItemDTO item;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmountAvailable() {
        return amountAvailable;
    }

    public void setAmountAvailable(Integer amountAvailable) {
        this.amountAvailable = amountAvailable;
    }

    public Integer getAmountSold() {
        return amountSold;
    }

    public void setAmountSold(Integer amountSold) {
        this.amountSold = amountSold;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public ItemDTO getItem() {
        return item;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemStatusDTO)) {
            return false;
        }

        ItemStatusDTO itemStatusDTO = (ItemStatusDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, itemStatusDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemStatusDTO{" +
            "id=" + getId() +
            ", amountAvailable=" + getAmountAvailable() +
            ", amountSold=" + getAmountSold() +
            ", availabilityStatus='" + getAvailabilityStatus() + "'" +
            ", item=" + getItem() +
            "}";
    }
}
