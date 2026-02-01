package com.maromvz.spaserver.repo;

import com.maromvz.spaserver.entities.Service;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepo extends CrudRepository<Service, Long> {
}
