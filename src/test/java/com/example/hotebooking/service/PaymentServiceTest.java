package com.example.hotebooking.service;

import com.example.hotebooking.entity.payment.Payment;
import com.example.hotebooking.entity.payment.PaymentMethod;
import com.example.hotebooking.entity.payment.PaymentStatus;
import com.example.hotebooking.strategy.payment.CardPaymentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentService paymentService;
    private CardPaymentStrategy cardStrategy;

    @BeforeEach
    void setUp() {
        cardStrategy = mock(CardPaymentStrategy.class);
        when(cardStrategy.supportedMethod()).thenReturn(PaymentMethod.CARD);
        
        paymentService = new PaymentService(Collections.singletonList(cardStrategy));
    }

    @Test
    void testCreatePaymentIntent() {
        Payment payment = paymentService.createPaymentIntent("b1", 2000, PaymentMethod.CARD, "idem-key");
        assertNotNull(payment);
        assertEquals(PaymentStatus.PENDING, payment.getPaymentStatus());
        assertEquals("b1", payment.getBookingId());
    }

    @Test
    void testCompletePayment_Success() {
        Payment payment = paymentService.createPaymentIntent("b1", 2000, PaymentMethod.CARD, "idem-key");
        
        when(cardStrategy.pay(2000, "b1")).thenReturn(PaymentStatus.CAPTURED);

        Payment completed = paymentService.completePayment(payment.getPaymentId(), "SUCCESS");

        assertEquals(PaymentStatus.CAPTURED, completed.getPaymentStatus());
    }

    @Test
    void testRefund_Success() {
        Payment payment = paymentService.createPaymentIntent("b2", 2000, PaymentMethod.CARD, "idem-key-2");
        when(cardStrategy.pay(2000, "b2")).thenReturn(PaymentStatus.CAPTURED);
        paymentService.completePayment(payment.getPaymentId(), "SUCCESS");
        
        paymentService.refund(payment.getPaymentId(), 1000);
        
        verify(cardStrategy, times(1)).refund(payment.getPaymentId(), 1000);
    }
    
    @Test
    void testRefund_InvalidPaymentId() {
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.refund(999, 1000);
        });
    }

    @Test
    void testCreatePaymentIntent_Idempotency() {
        String idempotencyKey = "unique-key-123";
        
        // First call creates a new payment intent
        Payment payment1 = paymentService.createPaymentIntent("b1", 2000, PaymentMethod.CARD, idempotencyKey);
        
        // Second call with the same idempotency key should return the exact same payment intent
        Payment payment2 = paymentService.createPaymentIntent("b1", 2000, PaymentMethod.CARD, idempotencyKey);
        
        // Assert that they are the exact same object in memory
        assertNotNull(payment1);
        assertSame(payment1, payment2, "Idempotency failed: A new payment intent was created!");
        assertEquals(payment1.getPaymentId(), payment2.getPaymentId());
    }

}


