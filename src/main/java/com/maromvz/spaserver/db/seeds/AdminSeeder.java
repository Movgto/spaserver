package com.maromvz.spaserver.db.seeds;

import com.maromvz.spaserver.config.AdminProperties;
import com.maromvz.spaserver.entities.Role;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.repo.RoleRepo;
import com.maromvz.spaserver.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepo userRepo;

    private final RoleRepo roleRepo;

    private final AdminProperties adminProperties;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (adminProperties == null) {
            throw new RuntimeException("The admin properties are not defined.");
        }

        Role role = roleRepo.findByName(Role.ERole.ROLE_SUPERADMIN).orElseThrow();

        if (userRepo.findByEmail(adminProperties.getEmail()).isEmpty()) {
            User admin = new User();

            admin.setEmail(adminProperties.getEmail());
            admin.addRole(role, new HashSet<>());
            admin.setFirstName(adminProperties.getFirstName());
            admin.setLastName(adminProperties.getLastName());
            admin.setPassword(adminProperties.getPassword(), passwordEncoder);

            userRepo.save(admin);

            log.info("The superadmin has been added.");
        }
    }
}
