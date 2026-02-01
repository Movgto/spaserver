package com.maromvz.spaserver.controllers;

import com.maromvz.spaserver.dto.employees.CreateEmployeeDto;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.security.annotations.CanCreateEmployees;
import com.maromvz.spaserver.services.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/create")
    @CanCreateEmployees
    public ResponseEntity<?> createEmployee(
            @RequestBody CreateEmployeeDto createEmployeeDto,
            Authentication auth
            ) {
        try {
            User employee = employeeService.createEmployee(createEmployeeDto);

            User loggedUser = (User) auth.getPrincipal();

            log.info("A new employee has been created " + employee.getFirstName()  + " by the admin " + loggedUser.getFirstName());

            return ResponseEntity.status(201).body(employee);
        } catch (Exception e) {
            log.error("An unhandled error ocurred while trying to create a new Employee");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Internal Server Error");
        }
    }
}
