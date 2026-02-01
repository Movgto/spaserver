package com.maromvz.spaserver.dto.appointments;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentDto {
    private final LocalDateTime startTime;
    private final Long serviceId;
    private final Long userId;
}
