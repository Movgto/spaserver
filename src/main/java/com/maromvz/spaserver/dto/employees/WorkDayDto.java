package com.maromvz.spaserver.dto.employees;

import com.maromvz.spaserver.entities.WorkSchedule;

import java.time.LocalTime;

public record WorkDayDto(
        WorkSchedule.WeekDay weekDay,
        LocalTime startTime,
        LocalTime endTime
) {}
