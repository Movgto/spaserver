package com.maromvz.spaserver.controllers;

import com.maromvz.spaserver.dto.customers.CreateCustomerDto;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.security.annotations.CanCreateCustomers;
import com.maromvz.spaserver.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/create")
    @CanCreateCustomers
    public ResponseEntity<?> createCustomer(
            @RequestBody CreateCustomerDto customerDto
            ) {
        try {
            User customer = customerService.createCustomer(customerDto);

            return ResponseEntity.status(401).body(customer);
        } catch(Exception e) {
            log.info("An unexpected error ocurred while trying to create a Customer.");
            e.printStackTrace();

            return ResponseEntity.internalServerError().build();
        }
    }
}
