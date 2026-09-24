package com.example.hotebooking.strategy.payment;

import com.example.hotebooking.entity.payment.PaymentMethod;
import com.example.hotebooking.entity.payment.PaymentStatus;

public interface PaymentStrategy {
    PaymentStatus pay(Integer amount, String bookingId);
    PaymentStatus refund(Integer paymentId, Integer amount);
    PaymentMethod supportedMethod();
}

