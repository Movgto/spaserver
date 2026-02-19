package com.maromvz.spaserver.services;

import com.maromvz.spaserver.dto.appointments.CreateAppointmentDto;
import com.maromvz.spaserver.entities.*;
import com.maromvz.spaserver.exceptions.appointments.AppointmentOverlappingException;
import com.maromvz.spaserver.exceptions.products.ProductNotFoundException;
import com.maromvz.spaserver.exceptions.users.UserNotFoundException;
import com.maromvz.spaserver.model.TimeSlot;
import com.maromvz.spaserver.repo.AppointmentRepo;
import com.maromvz.spaserver.repo.ServiceRepo;
import com.maromvz.spaserver.repo.UserRepo;
import com.maromvz.spaserver.repo.WorkScheduleRepo;
import com.maromvz.spaserver.stripe.exceptions.AppointmentNotFoundException;
import com.maromvz.spaserver.stripe.exceptions.AppointmentPaidOrCancelledException;
import com.maromvz.spaserver.utils.GeneralUtils;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

        List<User> availableEmployeesList = workScheduleRepo.findAvailableEmployeesByShift(appointmentDto.getStartTime().toLocalTime(), endTime.toLocalTime(), weekDay)
                .stream().map(WorkSchedule::getEmployee).toList();

        Set<User> availableEmployees = new HashSet<>(availableEmployeesList);

        Set<Long> employeeIds = availableEmployees.stream().map(User::getId).collect(Collectors.toSet());

        if (availableEmployees.isEmpty()) throw new RuntimeException("There are no available employees to assign this appointment to for this date.");

        List<Appointment> allAppointments = appointmentRepo.findAllByRange(appointmentDto.getStartTime(), endTime);

        Set<User> overlappingEmployees = allAppointments.stream()
                .filter(ap ->
                    employeeIds.contains(ap.getEmployee().getId())
                )
                .filter(ap ->
                    appointmentDto.getStartTime().isBefore(ap.getEndTime()) && endTime.isAfter(ap.getStartTime())
                )
                .map(Appointment::getEmployee).collect(Collectors.toSet());

        availableEmployees.removeAll(overlappingEmployees);

        if (availableEmployees.isEmpty()) throw new RuntimeException("There are overlapping appointments with the available employees");

        var randomEmployeeIndex = new Random().nextInt(0, availableEmployees.size());

        var randomEmployee = availableEmployees.stream().toList().get(randomEmployeeIndex);

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

    public List<TimeSlot> getTimeSlotsByDate(LocalDateTime dayStart, LocalDateTime dayEnd, Long serviceId) {
        Service service = serviceRepo.findById(serviceId).orElseThrow();

        var serviceDurationMinutes = service.getDurationMinutes();

        int slotDurationMinutes = (int) Math.ceil(serviceDurationMinutes / 60.0) * 60;

        log.info("The slot duration to book and appointment is: {} minutes.", slotDurationMinutes);

        WorkSchedule.WeekDay weekDay = GeneralUtils.getDayOfWeek(dayStart.getDayOfWeek().getValue());

        log.info("Day of week for the appointment is {}", weekDay);

        log.info("Looking for employees available between {} and {}", dayStart, dayEnd);

        List<WorkSchedule> availableEmployees = workScheduleRepo.findAvailableEmployeesByShift(dayStart.toLocalTime(), dayEnd.toLocalTime(), weekDay);

        List<Long> employeeIds = availableEmployees.stream().map(ws -> ws.getEmployee().getId()).toList();

        log.info("Available employees:");

        availableEmployees.forEach(e -> log.info(e.getEmployee().getEmail()));

        List<Appointment> allAppointments = appointmentRepo.findAllByRange(dayStart, dayEnd);

        Map<Long, List<Appointment>> availableEmployeeAppointments = allAppointments.stream()
                .filter(a -> a.getEmployee() != null && employeeIds.contains(a.getEmployee().getId()))
                .collect(Collectors.groupingBy(a -> a.getEmployee().getId()));

        log.info("Available employees appointments:");

        log.info(availableEmployeeAppointments.keySet().toString());

        List<Appointment> unassignedAppointments = allAppointments.stream().filter(a -> a.getEmployee() == null).toList();

        log.info("Unassigned appointments:");
        log.info(unassignedAppointments.toString());

        List<TimeSlot> freeSlotsForService = new ArrayList<>();

        var currentSlotStart = dayStart;
        var currentSlotEnd = dayStart.plusMinutes(serviceDurationMinutes);

        log.info("Starting the search for free slots...");

        while (currentSlotStart.isBefore(dayEnd)) {
            log.info("Current slot start " + currentSlotStart);
            log.info("Current slot end " + currentSlotEnd);

            LocalDateTime slotStart = currentSlotStart;
            LocalDateTime slotEnd = currentSlotEnd;

            int overlappingUnassignedAppointments = unassignedAppointments.stream().filter(a ->
                    a.getStartTime().isBefore(slotEnd) && a.getEndTime().isAfter(slotStart)
            ).toList().size();

            boolean enoughEmployeesToWork = overlappingUnassignedAppointments < availableEmployees.size();

            log.info("There are enough employees to assign the appointment: {}", enoughEmployeesToWork);

            boolean slotIsInsideEmployeeShift = availableEmployees.stream().anyMatch(ws -> {
                return (ws.getStartTime().isBefore(slotStart.toLocalTime())
                    || ws.getStartTime().equals(slotStart.toLocalTime())
                ) && (ws.getEndTime().isAfter(slotEnd.toLocalTime())
                    || ws.getEndTime().equals(slotEnd.toLocalTime())
                );
            });

            if (!slotIsInsideEmployeeShift) {
                currentSlotStart = currentSlotStart.plusMinutes(serviceDurationMinutes);
                currentSlotEnd = currentSlotEnd.plusMinutes(serviceDurationMinutes);
                continue;
            }

            if (availableEmployeeAppointments.isEmpty() && enoughEmployeesToWork) {
                freeSlotsForService.add(new TimeSlot(slotStart, slotEnd));

                currentSlotStart = currentSlotStart.plusMinutes(serviceDurationMinutes);
                currentSlotEnd = currentSlotEnd.plusMinutes(serviceDurationMinutes);
                continue;
            }

            for (Long employeeId : employeeIds) {

                List<Appointment> appointmentsByAvailableEmployee = availableEmployeeAppointments.get(employeeId);

                boolean overlapsWithAppointment = appointmentsByAvailableEmployee.stream().anyMatch(a ->
                   a.getStartTime().isBefore(slotEnd) && a.getEndTime().isAfter(slotStart)
                );

                if (!overlapsWithAppointment && enoughEmployeesToWork) {
                    log.info("Adding slot...");

                    freeSlotsForService.add(new TimeSlot(slotStart, slotEnd));

                    log.info("Current slots:");

                    log.info(freeSlotsForService.toString());

                    break;
                }
            }

            currentSlotStart = currentSlotStart.plusMinutes(serviceDurationMinutes);
            currentSlotEnd = currentSlotEnd.plusMinutes(serviceDurationMinutes);
        }

        return freeSlotsForService;
    }

    public Appointment confirmAppointment(String appointmentId) throws AppointmentPaidOrCancelledException, AppointmentNotFoundException {
        Appointment appointment = appointmentRepo.findById(appointmentId).orElseThrow(() -> new AppointmentNotFoundException("Appointment not found."));

        if (appointment.getStatus() != Appointment.Status.PENDING_PAYMENT) {
            throw new AppointmentPaidOrCancelledException("The appointment has been already paid or cancelled");
        }

        appointment.setStatus(Appointment.Status.CONFIRMED);

        return appointmentRepo.save(appointment);
    }
}
