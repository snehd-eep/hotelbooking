package com.example.hotebooking.controller;


import com.example.hotebooking.entity.payment.Payment;
import com.example.hotebooking.entity.payment.PaymentMethod;
import com.example.hotebooking.service.PaymentService;
import com.example.hotebooking.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;

    public PaymentController(PaymentService paymentService, BookingService bookingService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
    }

    @PostMapping("/callback")
    public ResponseEntity<Payment> processCallback(
            @RequestBody com.example.hotebooking.dto.PaymentCallbackDto request) {

        log.info("Received payment callback for paymentId: {} with status: {}", request.getPaymentId(), request.getStatus());
        Payment payment = paymentService.completePayment(
                request.getPaymentId(),
                request.getStatus());

        if (payment.getPaymentStatus() == com.example.hotebooking.entity.payment.PaymentStatus.CAPTURED) {
            bookingService.confirmBooking(payment.getBookingId());
        } else {
            bookingService.failBooking(payment.getBookingId());
        }

        return ResponseEntity.ok(payment);
    }
}

