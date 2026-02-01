package com.maromvz.spaserver.db.seeds;

import com.maromvz.spaserver.entities.Service;
import com.maromvz.spaserver.repo.ServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductSeeder implements CommandLineRunner {

    @Autowired
    private ServiceRepo serviceRepo;

    @Override
    public void run(String... args) throws Exception {
        if (serviceRepo.count() > 0) return;

        Service service1 = new Service();

        service1.setName("Masaje Reductivo");
        service1.setPrice(new BigDecimal(300));
        service1.setDurationMinutes(60);

        Service service2 = new Service();

        service2.setName("Masaje completo");
        service2.setPrice(new BigDecimal(400));
        service2.setDurationMinutes(60);

        serviceRepo.saveAll(
                List.of(service1, service2)
        );
    }
}
