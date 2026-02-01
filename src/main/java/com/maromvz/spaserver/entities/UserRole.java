package com.maromvz.spaserver.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class UserRole {
    @EmbeddedId
    private UserRoleId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("roles")
    private User user;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    private Set<Privilege> privileges = new HashSet<>();

    public enum Privilege {
        CAN_CREATE_ADMINS, CAN_DELETE_ADMINS, CAN_UPDATE_ADMINS,
        CAN_CREATE_EMPLOYEES, CAN_DELETE_EMPLOYEES, CAN_UPDATE_EMPLOYEES,
        CAN_CREATE_APPOINTMENTS, CAN_UPDATE_APPOINTMENTS, CAN_DELETE_APPOINTMENTS,
        CAN_CREATE_SERVICES, CAN_DELETE_SERVICES, CAN_UPDATE_SERVICES
    }

    public void revokePrivilege(Privilege privilege) {
        privileges.remove(privilege);
    }
}
