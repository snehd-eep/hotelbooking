package com.example.hotebooking.strategy.pricing;

import com.example.hotebooking.entity.BookingRequest;

public interface PricingStrategy {
    int calculatePrice(BookingRequest request);
}

