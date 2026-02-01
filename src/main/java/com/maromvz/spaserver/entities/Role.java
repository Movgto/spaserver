package com.maromvz.spaserver.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    private ERole name;

    public enum ERole {
        ROLE_ADMIN, ROLE_CUSTOMER, ROLE_THERAPIST, ROLE_SUPERADMIN
    }

    public Role(ERole role) {
        name = role;
    }
}
