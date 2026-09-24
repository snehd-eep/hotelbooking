package com.example.hotebooking.strategy.cancellation;

import com.example.hotebooking.entity.Booking;
import java.time.LocalDate;

public class FreeCancellationPolicy implements CancellationPolicy {
    @Override
    public Integer calculateRefund(Booking booking, Integer paidAmount) {
        if (LocalDate.now().isBefore(booking.getCheckIn())) {
            return paidAmount;
        }
        return 0;
    }
}

