package com.maromvz.spaserver.controllers;

import com.maromvz.spaserver.dto.auth.RegisterUserDto;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.exceptions.auth.PasswordsDontMatchException;
import com.maromvz.spaserver.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterUserDto userDto
            ) {
        try {
            User newUser = authService.registerUser(userDto);

            return ResponseEntity.ok(new Object() {
                public final String message = "The user was created successfully";
                public final User user = newUser;
            });

        } catch (PasswordsDontMatchException exception) {
            log.error("Passwords don't match");
            return ResponseEntity.status(400).build();
        } catch (Exception e) {
            log.error("An internal server error ocurred while trying to register a new User.");
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
}
