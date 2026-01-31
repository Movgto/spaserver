package com.maromvz.spaserver.dto.appointments;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentDto {
    private final LocalDateTime startTime;
    private final Long productId;
    private final Long userId;
}
