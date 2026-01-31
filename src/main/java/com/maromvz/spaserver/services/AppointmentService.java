package com.maromvz.spaserver.services;

import com.maromvz.spaserver.dto.appointments.CreateAppointmentDto;
import com.maromvz.spaserver.entities.Appointment;
import com.maromvz.spaserver.entities.Product;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.exceptions.appointments.AppointmentOverlappingException;
import com.maromvz.spaserver.exceptions.products.ProductNotFoundException;
import com.maromvz.spaserver.exceptions.users.UserNotFoundException;
import com.maromvz.spaserver.repo.AppointmentRepo;
import com.maromvz.spaserver.repo.ProductRepo;
import com.maromvz.spaserver.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepo appointmentRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    public boolean isSlotAvailable(LocalDateTime start, LocalDateTime end) {
        return appointmentRepo.existsTimeOverlap(start, end);
    }

    @Transactional
    public Appointment createAppointment(CreateAppointmentDto appointmentDto) throws AppointmentOverlappingException, ProductNotFoundException, UserNotFoundException {
        Appointment newAppointment = new Appointment();

        User user = userRepo.findById(appointmentDto.getUserId()).orElseThrow(UserNotFoundException::new);

        Product product = productRepo.findById(appointmentDto.getProductId()).orElseThrow(ProductNotFoundException::new);

        LocalDateTime endTime = appointmentDto.getStartTime().plusMinutes(product.getDurationMinutes());
        boolean overlaps = appointmentRepo.existsTimeOverlap(appointmentDto.getStartTime(), endTime);

        if (overlaps) throw new AppointmentOverlappingException();

        newAppointment.setUser(user);
        newAppointment.setProduct(product);

        newAppointment.setStartTime(appointmentDto.getStartTime());
        newAppointment.setEndTime(endTime);

        newAppointment.setStatus(Appointment.Status.PENDING_PAYMENT);

        newAppointment.setFinalPrice(product.getPrice());

        return appointmentRepo.save(newAppointment);
    }

    public List<Appointment> getUserAppointments(Long userId) {
        return appointmentRepo.findByUserId(userId);
    }
}
