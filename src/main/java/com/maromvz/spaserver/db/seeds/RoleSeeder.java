package com.maromvz.spaserver.db.seeds;

import com.maromvz.spaserver.entities.Role;
import com.maromvz.spaserver.repo.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleSeeder implements CommandLineRunner {
    @Autowired
    private RoleRepo roleRepo;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepo.count() == 0) {
            var roles = List.of(
                    new Role(null, Role.ERole.ROLE_ADMIN),
                    new Role(null, Role.ERole.ROLE_USER)
            );

            roleRepo.saveAll(roles);
        }
    }
}
