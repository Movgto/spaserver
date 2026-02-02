package com.maromvz.spaserver.model;

import java.time.LocalDateTime;

public record TimeSlot(
        LocalDateTime start,
        LocalDateTime end
) {}
