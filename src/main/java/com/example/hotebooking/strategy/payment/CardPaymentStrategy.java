package com.example.hotebooking.strategy.payment;

import com.example.hotebooking.entity.payment.PaymentMethod;
import com.example.hotebooking.entity.payment.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentStrategy implements PaymentStrategy {
    @Override
    public PaymentStatus pay(Integer amount, String bookingId) {
        System.out.println("Charging CARD Rs." + amount + " for booking " + bookingId);
        return PaymentStatus.CAPTURED;
    }

    @Override
    public PaymentStatus refund(Integer paymentId, Integer amount) {
        System.out.println("Refunding CARD Rs." + amount + " for payment " + paymentId);
        return PaymentStatus.REFUNDED;
    }

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.CARD;
    }
}

