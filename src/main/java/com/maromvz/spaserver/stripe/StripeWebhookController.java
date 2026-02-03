package com.maromvz.spaserver.stripe;

import com.maromvz.spaserver.services.AppointmentService;
import com.maromvz.spaserver.stripe.exceptions.AppointmentNotFoundException;
import com.maromvz.spaserver.stripe.exceptions.AppointmentPaidOrCancelledException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final AppointmentService appointmentService;

    @PostMapping("/stripe")
    public ResponseEntity<?> handleStripeEvent(
        @RequestBody String payload,
        @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        log.info("Stripe webhook event received.");

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();

                String appointmentId = session.getMetadata().get("appointment_id");

                appointmentService.confirmAppointment(appointmentId);

                return ResponseEntity.ok("The appointment was confirmed!");
            }

            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Signature");
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The appointment to confirm was not found.");
        } catch (AppointmentPaidOrCancelledException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("The appointment was already paid or cancelled.");
        } catch (Exception e) {
            log.error("An unexpected error ocurred while trying to confirm a payment.");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }

    }
}
