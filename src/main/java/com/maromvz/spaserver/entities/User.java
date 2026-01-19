package com.maromvz.spaserver.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    public static enum Role {
        ADMIN, USER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProduct> products;

    public void addUserProduct(UserProduct userProduct) {
        userProduct.setUser(this);

        products.add(userProduct);
    }

    public void addUserProducts(List<UserProduct> userProducts) {
        products.addAll(userProducts);
    }
}
