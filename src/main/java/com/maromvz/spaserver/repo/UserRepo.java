package com.maromvz.spaserver.repo;

import com.maromvz.spaserver.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN FETCH u.services us JOIN FETCH us.service WHERE u.id = :userId")
    Optional<User> findUserWithServices(Long userId);

    Optional<User> findByEmail(String email);
}
