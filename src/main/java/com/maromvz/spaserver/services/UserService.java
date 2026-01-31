package com.maromvz.spaserver.services;

import com.maromvz.spaserver.dto.AddProductsDTO;
import com.maromvz.spaserver.entities.Appointment;
import com.maromvz.spaserver.entities.Product;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.repo.ProductRepo;
import com.maromvz.spaserver.repo.AppointmentRepo;
import com.maromvz.spaserver.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private AppointmentRepo appointmentRepo;

    public List<Appointment> getUserProducts(Long userId) {
        return appointmentRepo.findByUserId(userId);
    }

    public User addProductsToUser(AddProductsDTO addProductsDTO) {
        User user = userRepo.findById(addProductsDTO.getUserId()).orElseThrow();

        List<Product> products = StreamSupport.stream(productRepo.findAllById(addProductsDTO.getProductIds()).spliterator(), false).toList();

        List<Appointment> userProducts = products.stream().map(p -> {
            Appointment userProduct = new Appointment();

            userProduct.setProduct(p);
            userProduct.setUser(user);

            return userProduct;
        }).toList();

        user.addUserProducts(userProducts);

        userRepo.save(user);

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
