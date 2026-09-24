package com.example.hotebooking.service;

import com.example.hotebooking.strategy.cancellation.CancellationPolicyType;
import com.example.hotebooking.entity.Booking;
import com.example.hotebooking.entity.BookingRequest;
import com.example.hotebooking.entity.payment.Payment;
import com.example.hotebooking.entity.payment.PaymentMethod;
import com.example.hotebooking.entity.hotel.HotelRoom;
import com.example.hotebooking.entity.hotel.Property;
import com.example.hotebooking.entity.hotel.Room;
import com.example.hotebooking.entity.hotel.RoomBookedStatus;
import com.example.hotebooking.entity.hotel.RoomType;
import com.example.hotebooking.exception.RoomNotAvailableException;
import com.example.hotebooking.strategy.pricing.PricingStrategyType;
import com.example.hotebooking.repository.booking.BookingDao;
import com.example.hotebooking.repository.hotel.PropertyDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private PropertyDao propertyDao;
    private BookingDao bookingDao;
    private BookingService bookingService;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        propertyDao = mock(PropertyDao.class);
        bookingDao = mock(BookingDao.class);
        paymentService = mock(PaymentService.class);
        bookingService = new BookingService(propertyDao, bookingDao, paymentService);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Property buildProperty(Map<LocalDate, List<HotelRoom>> hotelRooms) {
        Property property = new Property(1, "Test", 1, null, null, null, null,
                new HashMap<>(), PricingStrategyType.BASE, CancellationPolicyType.FREE);
        property.setHotelRooms(hotelRooms);
        return property;
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    void bookingFailsWhenPropertyHasNoRooms() {
        Property property = buildProperty(new HashMap<>());
        when(propertyDao.findById(1)).thenReturn(Optional.of(property));

        BookingRequest request = BookingRequest.builder()
                .propertyId(1)
                .roomIds(List.of(101))
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().plusDays(2))
                .build();

        assertThrows(RoomNotAvailableException.class, () -> bookingService.book(request));
    }

    @Test
    void bookingSucceedsAndLocksAreReleasedAfterwards() throws InterruptedException {
        Room roomData = new Room(101, RoomType.SINGLE, "Nice room", 1, 2000);
        HotelRoom hotelRoom = new HotelRoom(101, roomData, new ReentrantLock(), RoomBookedStatus.AVAILABLE);

        Map<LocalDate, List<HotelRoom>> availabilityMap = new HashMap<>();
        availabilityMap.put(LocalDate.now(), new ArrayList<>(List.of(hotelRoom)));
        Property property = buildProperty(availabilityMap);
        when(propertyDao.findById(1)).thenReturn(Optional.of(property));

        BookingRequest request = BookingRequest.builder()
                .propertyId(1)
                .roomIds(List.of(101))
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().plusDays(1))
                .paymentMethod(PaymentMethod.CARD)
                .build();

        Payment mockPayment = Payment.builder().paymentId(99).build();
        when(paymentService.createPaymentIntent(any(), any(), any(), any())).thenReturn(mockPayment);

        Booking booking = bookingService.book(request);

        assertEquals(99, booking.getPaymentId());
        assertTrue(booking.getRoomIds().contains(101));
        assertEquals(RoomBookedStatus.PROCESSING, hotelRoom.getBookedStatus());
        assertFalse(hotelRoom.getLock().isLocked()); // lock must be fully released
    }

    @Test
    void bookingFailsWhenRequestedRoomIsAlreadyBooked() {
        Room roomData = new Room(101, RoomType.SINGLE, "Nice room", 1, 2000);
        HotelRoom bookedRoom = new HotelRoom(101, roomData, new ReentrantLock(), RoomBookedStatus.BOOKED);

        Map<LocalDate, List<HotelRoom>> availabilityMap = new HashMap<>();
        availabilityMap.put(LocalDate.now(), new ArrayList<>(List.of(bookedRoom)));
        Property property = buildProperty(availabilityMap);
        when(propertyDao.findById(1)).thenReturn(Optional.of(property));

        BookingRequest request = BookingRequest.builder()
                .propertyId(1)
                .roomIds(List.of(101))
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().plusDays(1))
                .paymentMethod(PaymentMethod.CARD)
                .build();

        assertThrows(RoomNotAvailableException.class, () -> bookingService.book(request));
    }

    @Test
    void bookingFailsWhenRequestedRoomIdDoesNotExist() {
        Room roomData = new Room(999, RoomType.SINGLE, "Other room", 1, 2000);
        HotelRoom otherRoom = new HotelRoom(999, roomData, new ReentrantLock(), RoomBookedStatus.AVAILABLE);

        Map<LocalDate, List<HotelRoom>> availabilityMap = new HashMap<>();
        availabilityMap.put(LocalDate.now(), new ArrayList<>(Collections.singletonList(otherRoom)));
        Property property = buildProperty(availabilityMap);
        when(propertyDao.findById(1)).thenReturn(Optional.of(property));

        // Request roomId 101 but only 999 exists
        BookingRequest request = BookingRequest.builder()
                .propertyId(1)
                .roomIds(List.of(101))
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().plusDays(1))
                .paymentMethod(PaymentMethod.CARD)
                .build();

        assertThrows(RoomNotAvailableException.class, () -> bookingService.book(request));
    }
}


