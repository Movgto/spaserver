package com.maromvz.spaserver.services;

import com.maromvz.spaserver.dto.employees.CreateEmployeeDto;
import com.maromvz.spaserver.entities.Role;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.entities.WorkSchedule;
import com.maromvz.spaserver.repo.RoleRepo;
import com.maromvz.spaserver.repo.UserRepo;
import com.maromvz.spaserver.repo.WorkScheduleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final UserRepo userRepo;

    private final WorkScheduleRepo workScheduleRepo;

    private final RoleRepo roleRepo;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createEmployee(CreateEmployeeDto createEmployeeDto) {
        User employee = new User();

        Role role = roleRepo.findByName(Role.ERole.ROLE_THERAPIST).orElseThrow();

        employee.setFirstName(createEmployeeDto.firstName());
        employee.setLastName(createEmployeeDto.lastName());
        employee.setPassword(createEmployeeDto.password(), passwordEncoder);
        employee.addRole(role, new HashSet<>());
        employee.setEmail(createEmployeeDto.email());

        var workDays = createEmployeeDto.workingDays().stream()
                .map(wd -> {
                    var workSchedule = new WorkSchedule();

                    workSchedule.setEmployee(employee);
                    workSchedule.setWeekDay(wd.weekDay());
                    workSchedule.setActive(true);
                    workSchedule.setStartTime(wd.startTime());
                    workSchedule.setEndTime(wd.endTime());

                    return workSchedule;
                }).toList();

        userRepo.save(employee);

        workScheduleRepo.saveAll(workDays);

        return employee;
    }
}
