package com.maromvz.spaserver.controllers;

import com.maromvz.spaserver.dto.appointments.CreateAppointmentDto;
import com.maromvz.spaserver.dto.appointments.GetFreeSlotsDto;
import com.maromvz.spaserver.entities.Appointment;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.exceptions.appointments.AppointmentOverlappingException;
import com.maromvz.spaserver.model.TimeSlot;
import com.maromvz.spaserver.security.annotations.CanCreateAppointments;
import com.maromvz.spaserver.services.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<?> createAppointment(
            @RequestBody CreateAppointmentDto appointmentDto,
            Authentication auth
            ) {

        User user = (User) auth.getPrincipal();

        if (!user.getId().equals(appointmentDto.getUserId())) return ResponseEntity.status(401).body("Unauthorized request");

        try {
            Appointment appointment = appointmentService.createAppointment(appointmentDto);

            return ResponseEntity.ok(appointment);
        } catch (AppointmentOverlappingException exception) {
            log.error("A time overlapping ocurred in appointment creation.");

            exception.printStackTrace();

            return ResponseEntity.status(422).body("Another appointment overlaps with the one you tried to create.");
        } catch (Exception exception) {
            log.error("An unhandled exception ocurred while trying to create a new Appointment.");

            exception.printStackTrace();

            return ResponseEntity.internalServerError().body("Internal Server Error");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAppointments(Authentication auth) {
        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(appointmentService.getUserAppointments(user.getId()));
    }

    @PostMapping("/free-slots")
    public ResponseEntity<?> getFreeSlotsForAppointment(
            @RequestBody GetFreeSlotsDto freeSlotsDto
            ) {
        try {
            List<TimeSlot> freeSlots = appointmentService.getTimeSlotsByDate(freeSlotsDto.dayStart(), freeSlotsDto.dayEnd(), freeSlotsDto.serviceId());

            return ResponseEntity.ok(freeSlots);
        } catch(Exception e) {
            log.error("An exception ocurred while trying to get the free slots.");

            e.printStackTrace();

            return ResponseEntity.internalServerError().build();
        }
    }
}
