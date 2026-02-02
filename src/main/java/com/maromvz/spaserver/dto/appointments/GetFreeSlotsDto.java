package com.maromvz.spaserver.dto.appointments;

import java.time.LocalDateTime;

public record GetFreeSlotsDto(
        LocalDateTime dayStart,
        LocalDateTime dayEnd,
        Long serviceId
){}
