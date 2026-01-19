package com.maromvz.spaserver.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProductId implements Serializable {
    private Long userId;
    private Long productId;

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
