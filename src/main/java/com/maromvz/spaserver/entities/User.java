package com.maromvz.spaserver.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getRole().getName().toString()))
                .toList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<UserRole> roles = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<UserProduct> products = new ArrayList<>();

    public void addUserProduct(UserProduct userProduct) {
        userProduct.setUser(this);

        products.add(userProduct);
    }

    public void addUserProducts(List<UserProduct> userProducts) {
        products.addAll(userProducts);
    }

    public void addRole(Role role) {
        UserRole userRole = new UserRole();

        userRole.setRole(role);
        userRole.setUser(this);
        userRole.setId(new UserRoleId(this.getId(), role.getId()));

        roles.add(userRole);
    }
}
