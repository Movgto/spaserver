package com.maromvz.spaserver.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"services", "roles"})
    private User customer;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties({"services", "roles"})
    private User employee = null;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal finalPrice;

    public enum Status {
        PENDING_PAYMENT,
        CONFIRMED,
        COMPLETED,
        CANCELLED
    }
}
