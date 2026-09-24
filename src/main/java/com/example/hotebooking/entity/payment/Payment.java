package com.example.hotebooking.entity.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Payment {

    private Integer paymentId;
    private String bookingId;       // refs Booking.bookingId which is UUID (String)
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private Integer amount;
    private String idempotencyKey;
}


