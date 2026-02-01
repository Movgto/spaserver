package com.maromvz.spaserver.services;

import com.maromvz.spaserver.dto.appointments.CreateAppointmentDto;
import com.maromvz.spaserver.entities.*;
import com.maromvz.spaserver.exceptions.appointments.AppointmentOverlappingException;
import com.maromvz.spaserver.exceptions.products.ProductNotFoundException;
import com.maromvz.spaserver.exceptions.users.UserNotFoundException;
import com.maromvz.spaserver.repo.AppointmentRepo;
import com.maromvz.spaserver.repo.ServiceRepo;
import com.maromvz.spaserver.repo.UserRepo;
import com.maromvz.spaserver.repo.WorkScheduleRepo;
import com.maromvz.spaserver.utils.GeneralUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepo appointmentRepo;

    private final UserRepo userRepo;

    private final ServiceRepo serviceRepo;

    private final WorkScheduleRepo workScheduleRepo;

    public boolean isSlotAvailable(LocalDateTime start, LocalDateTime end) {
        return appointmentRepo.existsTimeOverlap(start, end);
    }

    @Transactional
    public Appointment createAppointment(CreateAppointmentDto appointmentDto) throws AppointmentOverlappingException, ProductNotFoundException, UserNotFoundException {
        Appointment newAppointment = new Appointment();

        User user = userRepo.findById(appointmentDto.getUserId()).orElseThrow(UserNotFoundException::new);

        boolean isCustomer = user.getRoles().stream().anyMatch(r -> r.getRole().getName() == Role.ERole.ROLE_CUSTOMER);

        if (!isCustomer) throw new RuntimeException("The user needs to be a customer to create an appointment.");

        Service service = serviceRepo.findById(appointmentDto.getServiceId()).orElseThrow(ProductNotFoundException::new);

        WorkSchedule.WeekDay weekDay = GeneralUtils.getDayOfWeek(appointmentDto.getStartTime().getDayOfWeek().getValue());

        LocalDateTime endTime = appointmentDto.getStartTime().plusMinutes(service.getDurationMinutes());
        List<User> availableEmployees = workScheduleRepo.findAvailableEmployeeeByShift(appointmentDto.getStartTime().toLocalTime(), endTime.toLocalTime(), weekDay);

        if (availableEmployees.isEmpty()) throw new RuntimeException("There are no available employees to assign this appointment to for this date.");

        var notOverlappingEmployees = availableEmployees.stream().filter(em -> appointmentRepo.checkOverlapingByEmployeeIdAndRange(em.getId(), appointmentDto.getStartTime(), endTime)).toList();

        if (notOverlappingEmployees.isEmpty()) throw new RuntimeException("There are overlapping appointments with the available employees");

        var randomEmployeeIndex = new Random().nextInt(0, notOverlappingEmployees.size());

        var randomEmployee = notOverlappingEmployees.get(randomEmployeeIndex);

        log.info("Random available employee selected: " + randomEmployee.getFirstName() + " " + randomEmployee.getLastName());

        newAppointment.setCustomer(user);
        newAppointment.setService(service);
        newAppointment.setEmployee(randomEmployee);

        newAppointment.setStartTime(appointmentDto.getStartTime());
        newAppointment.setEndTime(endTime);

        newAppointment.setStatus(Appointment.Status.PENDING_PAYMENT);

        newAppointment.setFinalPrice(service.getPrice());

        return appointmentRepo.save(newAppointment);
    }

    public List<Appointment> getUserAppointments(Long userId) {
        return appointmentRepo.findByCustomerId(userId);
    }


}
