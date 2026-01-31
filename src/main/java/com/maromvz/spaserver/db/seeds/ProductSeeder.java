package com.maromvz.spaserver.db.seeds;

import com.maromvz.spaserver.entities.Product;
import com.maromvz.spaserver.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductSeeder implements CommandLineRunner {

    @Autowired
    private ProductRepo productRepo;

    @Override
    public void run(String... args) throws Exception {
        if (productRepo.count() > 0) return;

        Product product1 = new Product();

        product1.setName("Masaje Reductivo");
        product1.setPrice(new BigDecimal(300));
        product1.setDurationMinutes(60);

        Product product2 = new Product();

        product2.setName("Masaje completo");
        product2.setPrice(new BigDecimal(400));
        product2.setDurationMinutes(60);

        productRepo.saveAll(
                List.of(product1, product2)
        );
    }
}
