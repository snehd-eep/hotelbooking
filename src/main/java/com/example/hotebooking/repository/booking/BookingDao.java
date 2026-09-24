package com.example.hotebooking.repository.booking;

import com.example.hotebooking.entity.Booking;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BookingDao {

    private final Map<String, Booking> bookingStore = new ConcurrentHashMap<>();

    public void save(Booking booking) {
        bookingStore.put(booking.getBookingId(), booking);
    }

    public Optional<Booking> findById(String bookingId) {
        return Optional.ofNullable(bookingStore.get(bookingId));
    }

    public List<Booking> findAll() {
        return new ArrayList<>(bookingStore.values());
    }
}

