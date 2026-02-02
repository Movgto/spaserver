package com.maromvz.spaserver.services;

import com.maromvz.spaserver.dto.customers.CreateCustomerDto;
import com.maromvz.spaserver.entities.Role;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.repo.RoleRepo;
import com.maromvz.spaserver.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    private final PasswordEncoder passwordEncoder;

    public User createCustomer(CreateCustomerDto customerDto) {
        if (!customerDto.password().equals(customerDto.passwordConfirmation())) {
            throw new RuntimeException("Passwords don't match.");
        }

        Role role = roleRepo.findByName(Role.ERole.ROLE_CUSTOMER).orElseThrow();

        User customer = new User();

        customer.setFirstName(customerDto.firstName());
        customer.setLastName(customerDto.lastName());
        customer.setEmail(customerDto.email());
        customer.setPassword(customerDto.password(), passwordEncoder);

        customer.addRole(role, new HashSet<>());

        return userRepo.save(customer);
    }
}
