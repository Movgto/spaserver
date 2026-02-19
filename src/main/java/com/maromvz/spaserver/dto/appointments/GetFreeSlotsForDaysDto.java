package com.maromvz.spaserver.dto.appointments;

import java.time.LocalDateTime;
import java.util.List;

public record GetFreeSlotsForDaysDto(
        List<LocalDateTime> dates,
        Long serviceId
) {
}
