package com.maromvz.spaserver.repo;

import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.entities.WorkSchedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface WorkScheduleRepo extends CrudRepository<WorkSchedule, Long> {

    @Query("""
            SELECT ws.employee FROM WorkSchedule ws
            WHERE ws.weekDay = :weekDay
            AND ws.active = true
            AND ws.startTime <= :startTime
            AND ws.endTime >= :endTime
            """)
    List<User> findAvailableEmployeeeByShift(LocalTime startTime, LocalTime endTime, WorkSchedule.WeekDay weekDay);
}
