package com.example.hotebooking.strategy.pricing;

import com.example.hotebooking.entity.BookingRequest;
import com.example.hotebooking.entity.hotel.HotelRoom;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Dynamic pricing strategy that applies a surge multiplier based on how close
 * the booking date is to the check-in date.
 *
 * Surge tiers (calculated as days between today and each target night):
 *   0-1 days  : +30%
 *   2-3 days  : +20%
 *   4-7 days  : +10%
 *   > 7 days  : base price (no surge)
 */
public class DynamicPricingStrategy implements PricingStrategy {

    @Override
    public int calculatePrice(BookingRequest request) {
        int total = 0;
        LocalDate bookingDate = LocalDate.now();
        LocalDate targetDate = request.getCheckIn();

        for (HotelRoom hr : request.getRoomsToBook()) {
            int price = hr.getRoom().getPrice();
            long daysInAdvance = ChronoUnit.DAYS.between(bookingDate, targetDate);

            if (daysInAdvance <= 1) {
                price = (int) (price * 1.3);
            } else if (daysInAdvance <= 3) {
                price = (int) (price * 1.2);
            } else if (daysInAdvance <= 7) {
                price = (int) (price * 1.1);
            }

            total += price;
            targetDate = targetDate.plusDays(1);
        }
        return total;
    }
}

