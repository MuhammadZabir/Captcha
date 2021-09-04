package com.test.captcha.service.dto;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.test.captcha.domain.Shop} entity.
 */
@ApiModel(description = "The Shop entity.")
public class ShopDTO implements Serializable {

    private Long id;

    private String name;

    private String description;

    private LocalDate createdDate;

    private UserExtraDTO owner;

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

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public UserExtraDTO getOwner() {
        return owner;
    }

    public void setOwner(UserExtraDTO owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShopDTO)) {
            return false;
        }

        ShopDTO shopDTO = (ShopDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shopDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShopDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", owner=" + getOwner() +
            "}";
    }
}
