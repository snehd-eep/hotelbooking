package com.example.hotebooking.strategy.pricing;

import com.example.hotebooking.entity.BookingRequest;
import com.example.hotebooking.entity.hotel.HotelRoom;
import com.example.hotebooking.entity.hotel.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingStrategyTest {

    private BookingRequest buildRequest(int basePrice, LocalDate checkIn) {
        Room r = new Room(1, null, "Test", 2, basePrice);
        HotelRoom hr = new HotelRoom(1, r, null, null);
        return BookingRequest.builder()
                .checkIn(checkIn)
                .roomsToBook(Collections.singletonList(hr))
                .build();
    }

    @Test
    void testBasePricingStrategy() {
        BasePricingStrategy strategy = new BasePricingStrategy();
        BookingRequest req = buildRequest(1000, LocalDate.now());
        int price = strategy.calculatePrice(req);
        assertEquals(1000, price);
    }

    @Test
    void testDynamicPricingStrategy_HighSurge() {
        DynamicPricingStrategy strategy = new DynamicPricingStrategy();
        // Today or tomorrow = 30% surge
        BookingRequest req = buildRequest(1000, LocalDate.now().plusDays(1));
        int price = strategy.calculatePrice(req);
        assertEquals(1300, price);
    }

    @Test
    void testDynamicPricingStrategy_MediumSurge() {
        DynamicPricingStrategy strategy = new DynamicPricingStrategy();
        // 2-3 days = 20% surge
        BookingRequest req = buildRequest(1000, LocalDate.now().plusDays(2));
        int price = strategy.calculatePrice(req);
        assertEquals(1200, price);
    }

    @Test
    void testDynamicPricingStrategy_LowSurge() {
        DynamicPricingStrategy strategy = new DynamicPricingStrategy();
        // 4-7 days = 10% surge
        BookingRequest req = buildRequest(1000, LocalDate.now().plusDays(5));
        int price = strategy.calculatePrice(req);
        assertEquals(1100, price);
    }

    @Test
    void testDynamicPricingStrategy_NoSurge() {
        DynamicPricingStrategy strategy = new DynamicPricingStrategy();
        // > 7 days = no surge
        BookingRequest req = buildRequest(1000, LocalDate.now().plusDays(10));
        int price = strategy.calculatePrice(req);
        assertEquals(1000, price);
    }
}
