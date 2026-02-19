package com.maromvz.spaserver.repo;

import com.maromvz.spaserver.entities.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepo extends CrudRepository<Service, Long> {
    @Query("""
            SELECT a.service FROM Appointment a
            GROUP BY a.service
            ORDER BY COUNT(a) DESC
            """)
    public List<Service> findServicesBySales(Pageable pageable);
}
