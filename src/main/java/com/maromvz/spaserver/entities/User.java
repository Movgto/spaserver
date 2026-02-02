package com.maromvz.spaserver.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Audited
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        for (UserRole role : getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getRole().getName().toString()));

            role.getPrivileges().stream()
                    .map(p -> new SimpleGrantedAuthority(p.name()))
                    .forEach(authorities::add);
        }

        return authorities;
    }

    public void setPassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
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

    @Setter(AccessLevel.NONE)
    private String password;

    @NotAudited
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonIgnoreProperties("user")
    private List<UserRole> roles = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    public void addRole(Role role, Set<UserRole.Privilege> privileges) {
        UserRole userRole = new UserRole();

        userRole.setRole(role);
        userRole.setUser(this);
        userRole.setId(new UserRoleId(this.getId(), role.getId()));
        userRole.setPrivileges(privileges);

        roles.add(userRole);
    }
}
