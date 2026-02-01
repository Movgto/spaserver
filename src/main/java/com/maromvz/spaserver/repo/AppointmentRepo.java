package com.maromvz.spaserver.repo;

import com.maromvz.spaserver.entities.Appointment;
import com.maromvz.spaserver.entities.AppointmentId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepo extends CrudRepository<Appointment, AppointmentId> {
    List<Appointment> findByCustomerId(Long customerId);

    @Query("""
            SELECT COUNT(a) > 0 FROM Appointment a
            WHERE a.status != 'CANCELLED'
            AND a.startTime < :newEnd
            AND a.endTime > :newStart
            """)
    boolean existsTimeOverlap(LocalDateTime newStart, LocalDateTime newEnd);

    @Query("""
            SELECT COUNT(ap) > 0 FROM Appointment ap
            WHERE ap.employee.id = :employeeId
            AND ap.starTime < :endTime
            AND ap.endTime > :startTime
            """)
    boolean checkOverlapingByEmployeeIdAndRange(Long employeeId, LocalDateTime startTime, LocalDateTime endTime);
}
