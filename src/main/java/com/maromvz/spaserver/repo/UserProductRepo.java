package com.maromvz.spaserver.repo;

import com.maromvz.spaserver.entities.Product;
import com.maromvz.spaserver.entities.UserProduct;
import com.maromvz.spaserver.entities.UserProductId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProductRepo extends CrudRepository<UserProduct, UserProductId> {
    List<UserProduct> findByIdUserId(Long userId);
}
