package com.maromvz.spaserver.controllers;

import com.maromvz.spaserver.entities.Service;
import com.maromvz.spaserver.repo.ServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/services")
public class ProductController {
    @Autowired
    private ServiceRepo serviceRepo;

    @GetMapping
    public ResponseEntity<?> getProducts() {
        List<Service> services = StreamSupport
                .stream(serviceRepo.findAll().spliterator(), false)
                .toList();

        return ResponseEntity.ok(services);
    }

    @GetMapping("/top3")
    public ResponseEntity<?> getTop3Services() {
        var services = this.serviceRepo.findServicesBySales(PageRequest.of(0,3));

        var response = new HashMap<>();

        response.put("services", services);

        return ResponseEntity.ok(response);
    }
}
