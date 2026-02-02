package com.maromvz.spaserver.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('SUPERADMIN') or (hasRole('ADMIN') and hasAuthority('CAN_CREATE_CUSTOMERS'))")
public @interface CanCreateCustomers {
}
