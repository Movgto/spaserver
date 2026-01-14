package com.maromvz.spaserver.controllers;

import com.maromvz.spaserver.entities.Product;
import com.maromvz.spaserver.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductRepo productRepo;

    @GetMapping
    public ResponseEntity<?> getProducts() {
        List<Product> products = StreamSupport
                .stream(productRepo.findAll().spliterator(), false)
                .toList();

        return ResponseEntity.ok(products);
    }
}
