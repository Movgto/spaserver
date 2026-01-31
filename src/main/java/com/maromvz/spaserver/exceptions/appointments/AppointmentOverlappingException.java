package com.maromvz.spaserver.exceptions.appointments;

public class AppointmentOverlappingException extends RuntimeException {
    public AppointmentOverlappingException(String message) {
        super(message);
    }

    public AppointmentOverlappingException() {
        super("The appointment overlaps with an existent one");
    }
}
