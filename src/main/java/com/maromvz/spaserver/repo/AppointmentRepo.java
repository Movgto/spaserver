package com.maromvz.spaserver.repo;

import com.maromvz.spaserver.entities.Appointment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepo extends CrudRepository<Appointment, String> {
    List<Appointment> findByCustomerId(Long customerId);

    @Query("""
            SELECT COUNT(a) > 0 FROM Appointment a
            WHERE a.status != 'CANCELLED'
            AND a.startTime < :newEnd
            AND a.endTime > :newStart
            """)
    boolean existsTimeOverlap(LocalDateTime newStart, LocalDateTime newEnd);

    @Query("""
            SELECT ap FROM Appointment ap
            WHERE ap.employee.id = :employeeId
            AND ap.startTime < :endTime
            AND ap.endTime > :startTime
            """)
    List<Appointment> checkOverlapingByEmployeeIdAndRange(Long employeeId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("""
            SELECT ap FROM Appointment ap
            WHERE ap.status != 'CANCELLED'
            AND ap.startTime >= :startTime
            AND ap.endTime <= :endTime
            """)
    List<Appointment> findAllByRange(LocalDateTime startTime, LocalDateTime endTime);

    @Query("""
            SELECT ap FROM Appointment ap
            WHERE ap.status != 'CANCELLED'
            AND ap.employee = null
            AND ap.startTime < :end
            AND ap.endTime > :start
            """)
    List<Appointment> findOverlappingAndUnassaigned(LocalDateTime start, LocalDateTime end);
}
