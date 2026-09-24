package com.example.hotebooking.strategy.cancellation;

import com.example.hotebooking.entity.Booking;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PartialRefundPolicy implements CancellationPolicy {
    @Override
    public Integer calculateRefund(Booking booking, Integer paidAmount) {
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), booking.getCheckIn());
        if (daysLeft > 1)  return paidAmount;       // full refund
        if (daysLeft == 1) return paidAmount / 2;   // 50% refund
        return 0;                                   // no refund
    }
}

