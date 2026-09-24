package com.example.hotebooking.entity;

import com.example.hotebooking.entity.hotel.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    private String bookingId;
    private Integer propertyId;
    private List<Integer> roomIds;      // all rooms booked in this booking
    private String guestName;
    private Integer numberOfGuests;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BookingStatus bookingStatus;
    private Integer totalPrice;
    @Setter
    private Integer paymentId;

    // ── State machine: enforce valid transitions ──────────────────────────────

    public void confirm() {
        if (this.bookingStatus != BookingStatus.PENDING) {
            throw new IllegalStateException(
                "Cannot confirm booking " + bookingId + " in state " + bookingStatus);
        }
        this.bookingStatus = BookingStatus.CONFIRMED;
    }

    public void fail() {
        if (this.bookingStatus != BookingStatus.PENDING) {
            throw new IllegalStateException(
                "Cannot fail booking " + bookingId + " in state " + bookingStatus);
        }
        this.bookingStatus = BookingStatus.FAILED;
    }

    public void cancel() {
        if (this.bookingStatus != BookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                "Cannot cancel booking " + bookingId + " in state " + bookingStatus);
        }
        this.bookingStatus = BookingStatus.CANCELLED;
    }

    public boolean isActive() {
        return bookingStatus == BookingStatus.PENDING
            || bookingStatus == BookingStatus.CONFIRMED;
    }
}


