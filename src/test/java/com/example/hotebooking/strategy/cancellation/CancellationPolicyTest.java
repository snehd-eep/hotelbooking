package com.example.hotebooking.strategy.cancellation;

import com.example.hotebooking.entity.Booking;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CancellationPolicyTest {

    @Test
    void testFreeCancellation_BeforeCheckIn() {
        FreeCancellationPolicy policy = new FreeCancellationPolicy();
        Booking booking = Booking.builder().checkIn(LocalDate.now().plusDays(5)).build();
        
        Integer refund = policy.calculateRefund(booking, 1000);
        assertEquals(1000, refund);
    }

    @Test
    void testFreeCancellation_OnOrAfterCheckIn() {
        FreeCancellationPolicy policy = new FreeCancellationPolicy();
        Booking booking = Booking.builder().checkIn(LocalDate.now()).build();
        
        Integer refund = policy.calculateRefund(booking, 1000);
        assertEquals(0, refund);
    }

    @Test
    void testPartialRefund_MoreThanOneDay() {
        PartialRefundPolicy policy = new PartialRefundPolicy();
        Booking booking = Booking.builder().checkIn(LocalDate.now().plusDays(2)).build();
        
        Integer refund = policy.calculateRefund(booking, 1000);
        assertEquals(1000, refund);
    }

    @Test
    void testPartialRefund_ExactlyOneDay() {
        PartialRefundPolicy policy = new PartialRefundPolicy();
        Booking booking = Booking.builder().checkIn(LocalDate.now().plusDays(1)).build();
        
        Integer refund = policy.calculateRefund(booking, 1000);
        assertEquals(500, refund);
    }

    @Test
    void testPartialRefund_OnCheckInDay() {
        PartialRefundPolicy policy = new PartialRefundPolicy();
        Booking booking = Booking.builder().checkIn(LocalDate.now()).build();
        
        Integer refund = policy.calculateRefund(booking, 1000);
        assertEquals(0, refund);
    }
}


