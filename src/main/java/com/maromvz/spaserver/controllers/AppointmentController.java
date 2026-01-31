package com.maromvz.spaserver.controllers;

import com.maromvz.spaserver.dto.appointments.CreateAppointmentDto;
import com.maromvz.spaserver.entities.Appointment;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.exceptions.appointments.AppointmentOverlappingException;
import com.maromvz.spaserver.services.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}
