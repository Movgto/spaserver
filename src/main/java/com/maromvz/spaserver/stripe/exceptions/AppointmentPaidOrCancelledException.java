package com.maromvz.spaserver.stripe.exceptions;

public class AppointmentPaidOrCancelledException extends RuntimeException {
    public AppointmentPaidOrCancelledException(String message) {
        super(message);
    }
}
