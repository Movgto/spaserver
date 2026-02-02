package com.maromvz.spaserver.dto.customers;

public record CreateCustomerDto(
   String firstName,
   String lastName,
   String email,
   String password,
   String passwordConfirmation
) {}
