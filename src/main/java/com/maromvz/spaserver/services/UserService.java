package com.maromvz.spaserver.services;

import com.maromvz.spaserver.dto.AddProductsDTO;
import com.maromvz.spaserver.entities.Product;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.entities.UserProduct;
import com.maromvz.spaserver.entities.UserProductId;
import com.maromvz.spaserver.repo.ProductRepo;
import com.maromvz.spaserver.repo.UserProductRepo;
import com.maromvz.spaserver.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserProductRepo userProductRepo;

    public List<UserProduct> getUserProducts(Long userId) {
        return userProductRepo.findByIdUserId(userId);
    }

    public User addProductsToUser(AddProductsDTO addProductsDTO) {
        User user = userRepo.findById(addProductsDTO.getUserId()).orElseThrow();

        List<Product> products = StreamSupport.stream(productRepo.findAllById(addProductsDTO.getProductIds()).spliterator(), false).toList();

        List<UserProduct> userProducts = products.stream().map(p -> {
            UserProduct userProduct = new UserProduct();

            userProduct.setProduct(p);
            userProduct.setUser(user);
            userProduct.setScheduledDate(LocalDateTime.now().plusDays(2));
            userProduct.setId(new UserProductId(user.getId(), p.getId()));

            return userProduct;
        }).toList();

        user.addUserProducts(userProducts);

        userRepo.save(user);

        return user;
    }
}
