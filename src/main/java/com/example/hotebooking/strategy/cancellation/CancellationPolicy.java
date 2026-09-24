package com.example.hotebooking.strategy.cancellation;

import com.example.hotebooking.entity.Booking;

public interface CancellationPolicy {
    Integer calculateRefund(Booking booking, Integer paidAmount);
}

