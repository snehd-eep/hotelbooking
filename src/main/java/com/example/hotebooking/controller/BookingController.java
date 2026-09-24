package com.example.hotebooking.controller;

import com.example.hotebooking.dto.ApiResponse;
import com.example.hotebooking.dto.CreateBookingRequest;
import com.example.hotebooking.entity.Booking;
import com.example.hotebooking.entity.BookingRequest;
import com.example.hotebooking.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Booking> book(@RequestBody CreateBookingRequest request) throws InterruptedException {
        log.info("Received request to create booking for propertyId: {} with roomIds: {}",
                request.getPropertyId(), request.getRoomIds());

        BookingRequest bookingRequest = BookingRequest.builder()
                .propertyId(request.getPropertyId())
                .roomIds(request.getRoomIds())
                .guestName(request.getGuestName())
                .numberOfGuests(request.getNumberOfGuests())
                .checkIn(LocalDate.parse(request.getCheckIn()))
                .checkOut(LocalDate.parse(request.getCheckOut()))
                .paymentMethod(com.example.hotebooking.entity.payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .build();
        return ResponseEntity.ok(bookingService.book(bookingRequest));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBooking(@PathVariable String bookingId) {
        log.info("Received request to fetch bookingId: {}", bookingId);
        return ResponseEntity.ok(bookingService.getBooking(bookingId));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<ApiResponse> cancel(@PathVariable String bookingId) {
        log.info("Received request to cancel bookingId: {}", bookingId);
        bookingService.cancel(bookingId);
        return ResponseEntity.ok(new ApiResponse("Booking cancelled successfully"));
    }
}

