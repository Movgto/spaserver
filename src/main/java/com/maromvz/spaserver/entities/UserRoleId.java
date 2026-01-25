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
public class UserRoleId implements Serializable {
    private Long userId;
    private Long roleId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || this.getClass() != obj.getClass()) return false;

        UserRoleId other = (UserRoleId) obj;

        return (Objects.equals(other.roleId, this.roleId) && Objects.equals(other.userId, this.userId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }
}
