package com.example.hotebooking.strategy.pricing;

import com.example.hotebooking.entity.BookingRequest;
import com.example.hotebooking.entity.hotel.HotelRoom;

public class BasePricingStrategy implements PricingStrategy {
    @Override
    public int calculatePrice(BookingRequest request) {
        int total = 0;
        for (HotelRoom hr : request.getRoomsToBook()) {
            total += hr.getRoom().getPrice();
        }
        return total;
    }
}

