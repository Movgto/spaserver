package com.maromvz.spaserver.controllers;

import com.maromvz.spaserver.dto.AddProductsDTO;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.repo.UserRepo;
import com.maromvz.spaserver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/products")
    public ResponseEntity<?> addProducts(
            @RequestBody AddProductsDTO addProductsDTO)
    {
        User user = userService.addProductsToUser(addProductsDTO);

        return ResponseEntity.ok("Products successfully added to " + user.getFirstName() + "!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUsersWithProducts(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProducts(id));
    }
}
