package com.maromvz.spaserver.controllers;

import com.maromvz.spaserver.dto.AddProductsDTO;
import com.maromvz.spaserver.dto.auth.AuthenticatedUserDto;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.repo.UserRepo;
import com.maromvz.spaserver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUser(Authentication auth) {
        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(new AuthenticatedUserDto(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail()
        ));
    }
}
