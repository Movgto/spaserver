package com.maromvz.spaserver.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserProductId implements Serializable {
    private final Long userId;
    private final Long productId;

    public UserProductId(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || this.getClass() != o.getClass()) return false;

        UserProductId that = (UserProductId) o;

        return (Objects.equals(this.productId, that.productId) && Objects.equals(this.userId, that.userId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, productId);
    }
}
