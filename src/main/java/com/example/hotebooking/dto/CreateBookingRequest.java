package com.example.hotebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    private Integer propertyId;
    private List<Integer> roomIds;
    private String guestName;
    private Integer numberOfGuests;
    private String checkIn;
    private String checkOut;
    private String paymentMethod;
}


