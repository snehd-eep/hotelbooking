package com.example.hotebooking.service;

import com.example.hotebooking.entity.payment.Payment;
import com.example.hotebooking.entity.payment.PaymentMethod;
import com.example.hotebooking.entity.payment.PaymentStatus;
import com.example.hotebooking.strategy.payment.PaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class PaymentService {

    private final List<PaymentStrategy> strategies;
    
    // Idempotency check happens at creation time now (to prevent duplicate intent creation)
    private final Map<String, Payment> idempotencyStore = new ConcurrentHashMap<>();
    private final Map<Integer, Payment> paymentStore = new ConcurrentHashMap<>();
    
    private static final AtomicInteger paymentIdCounter = new AtomicInteger(1);

    public PaymentService(List<PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    public Payment createPaymentIntent(String bookingId, Integer amount, PaymentMethod method, String idempotencyKey) {
        log.info("Creating payment intent for bookingId: {}, amount: {}", bookingId, amount);
        if (idempotencyKey != null && idempotencyStore.containsKey(idempotencyKey)) {
            log.info("Returning cached payment intent for idempotency key: {}", idempotencyKey);
            return idempotencyStore.get(idempotencyKey);
        }

        Payment payment = Payment.builder()
                .paymentId(paymentIdCounter.getAndIncrement())
                .bookingId(bookingId)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(method)
                .amount(amount)
                .idempotencyKey(idempotencyKey)
                .build();

        if (idempotencyKey != null) {
            idempotencyStore.put(idempotencyKey, payment);
        }
        paymentStore.put(payment.getPaymentId(), payment);
        
        log.info("Successfully created Payment Intent: ID {} for Booking {}", payment.getPaymentId(), bookingId);
        return payment;
    }
    
    public Payment completePayment(Integer paymentId, String gatewayStatus) {
        log.info("Completing payment for paymentId: {} with status: {}", paymentId, gatewayStatus);
        Payment payment = paymentStore.get(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Invalid payment ID: " + paymentId);
        }
        
        PaymentStatus status = gatewayStatus.equalsIgnoreCase("SUCCESS") ? PaymentStatus.CAPTURED : PaymentStatus.FAILED;
        payment.setPaymentStatus(status);
        
        // Simulating the strategy execution (printing logs as if interacting with gateway)
        PaymentStrategy strategy = resolveStrategy(payment.getPaymentMethod());
        strategy.pay(payment.getAmount(), payment.getBookingId());
        
        log.info("Payment ID {} successfully completed with status {}", paymentId, status);
        return payment;
    }

    public void refund(Integer paymentId, Integer amount) {
        log.info("Initiating refund for paymentId: {} amount: {}", paymentId, amount);
        Payment payment = paymentStore.get(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Invalid payment ID: " + paymentId);
        }
        
        PaymentStrategy strategy = resolveStrategy(payment.getPaymentMethod());
        strategy.refund(paymentId, amount);
        log.info("Refund successful for paymentId: {}", paymentId);
    }

    private PaymentStrategy resolveStrategy(PaymentMethod method) {
        for (PaymentStrategy strategy : strategies) {
            if (strategy.supportedMethod() == method) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("No strategy found for payment method: " + method);
    }
}

