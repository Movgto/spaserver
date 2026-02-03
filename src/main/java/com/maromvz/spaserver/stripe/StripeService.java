package com.maromvz.spaserver.stripe;

import com.maromvz.spaserver.entities.Appointment;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StripeService {
    @Value("${stripe.api-key}")
    private String apiKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @PostConstruct
    private void init() {
        log.info("Setting stripe api key: {}", apiKey);
        Stripe.apiKey = apiKey;
    }

    public String createCheckoutSession(Appointment appointment) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("mxn")
                                .setUnitAmount(appointment.getFinalPrice().longValue() * 100)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(appointment.getService().getName())
                                        .setDescription("Cita en el SPA. Recordatorio: el servicio necesita una descripción.")
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .putMetadata("appointment_id", appointment.getId())
                .build();

        Session session = Session.create(params);

        return session.getUrl();
    }
}
