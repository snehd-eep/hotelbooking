package com.example.hotebooking.entity;

import com.example.hotebooking.entity.hotel.HotelRoom;
import com.example.hotebooking.entity.payment.PaymentMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class BookingRequest {
    private Integer propertyId;
    private List<Integer> roomIds;
    private String guestName;
    private Integer numberOfGuests;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private List<HotelRoom> roomsToBook;
    private PaymentMethod paymentMethod;
}

