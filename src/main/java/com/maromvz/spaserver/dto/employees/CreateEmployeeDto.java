package com.maromvz.spaserver.dto.employees;

import java.util.List;

public record CreateEmployeeDto (
        String email,
    String firstName,
    String lastName,
    String password,
    List<WorkDayDto> workingDays
) {}

