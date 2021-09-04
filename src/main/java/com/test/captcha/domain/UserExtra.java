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
 * The UserExtra entity.
 */
@Table("user_extra")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "userextra")
public class UserExtra implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("billing_address")
    private String billingAddress;

    private Long userId;

    @Transient
    private User user;

    @Transient
    @JsonIgnoreProperties(value = { "cart", "buyer" }, allowSetters = true)
    private Set<PurchaseHistory> purchaseHistories = new HashSet<>();

    @JsonIgnoreProperties(value = { "userExtras" }, allowSetters = true)
    @Transient
    private UserType userType;

    @Column("user_type_id")
    private Long userTypeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserExtra id(Long id) {
        this.id = id;
        return this;
    }

    public String getBillingAddress() {
        return this.billingAddress;
    }

    public UserExtra billingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
        return this;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public User getUser() {
        return this.user;
    }

    public UserExtra user(User user) {
        this.setUser(user);
        this.userId = user != null ? user.getId() : null;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Set<PurchaseHistory> getPurchaseHistories() {
        return this.purchaseHistories;
    }

    public UserExtra purchaseHistories(Set<PurchaseHistory> purchaseHistories) {
        this.setPurchaseHistories(purchaseHistories);
        return this;
    }

    public UserExtra addPurchaseHistory(PurchaseHistory purchaseHistory) {
        this.purchaseHistories.add(purchaseHistory);
        purchaseHistory.setBuyer(this);
        return this;
    }

    public UserExtra removePurchaseHistory(PurchaseHistory purchaseHistory) {
        this.purchaseHistories.remove(purchaseHistory);
        purchaseHistory.setBuyer(null);
        return this;
    }

    public void setPurchaseHistories(Set<PurchaseHistory> purchaseHistories) {
        if (this.purchaseHistories != null) {
            this.purchaseHistories.forEach(i -> i.setBuyer(null));
        }
        if (purchaseHistories != null) {
            purchaseHistories.forEach(i -> i.setBuyer(this));
        }
        this.purchaseHistories = purchaseHistories;
    }

    public UserType getUserType() {
        return this.userType;
    }

    public UserExtra userType(UserType userType) {
        this.setUserType(userType);
        this.userTypeId = userType != null ? userType.getId() : null;
        return this;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
        this.userTypeId = userType != null ? userType.getId() : null;
    }

    public Long getUserTypeId() {
        return this.userTypeId;
    }

    public void setUserTypeId(Long userType) {
        this.userTypeId = userType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserExtra)) {
            return false;
        }
        return id != null && id.equals(((UserExtra) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserExtra{" +
            "id=" + getId() +
            ", billingAddress='" + getBillingAddress() + "'" +
            "}";
    }
}
