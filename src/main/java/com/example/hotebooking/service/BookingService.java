package com.example.hotebooking.service;

import lombok.extern.slf4j.Slf4j;
import com.example.hotebooking.strategy.cancellation.CancellationPolicy;
import com.example.hotebooking.strategy.cancellation.CancellationPolicyFactory;
import com.example.hotebooking.entity.Booking;
import com.example.hotebooking.entity.BookingRequest;
import com.example.hotebooking.entity.BookingStatus;
import com.example.hotebooking.entity.hotel.HotelRoom;
import com.example.hotebooking.entity.hotel.Property;
import com.example.hotebooking.entity.hotel.RoomBookedStatus;
import com.example.hotebooking.entity.payment.Payment;
import com.example.hotebooking.exception.BookingNotFoundException;
import com.example.hotebooking.exception.PropertyNotFoundException;
import com.example.hotebooking.exception.RoomNotAvailableException;
import com.example.hotebooking.strategy.pricing.PricingStrategyFactory;
import com.example.hotebooking.repository.booking.BookingDao;
import com.example.hotebooking.repository.hotel.PropertyDao;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BookingService {

    private final PropertyDao propertyDao;
    private final BookingDao bookingDao;
    private final PaymentService paymentService;

    public BookingService(PropertyDao propertyDao, BookingDao bookingDao, PaymentService paymentService) {
        this.propertyDao = propertyDao;
        this.bookingDao = bookingDao;
        this.paymentService = paymentService;
    }

    public Booking book(BookingRequest request) throws InterruptedException {
        Property property = propertyDao.findById(request.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(request.getPropertyId()));

        List<HotelRoom> resolvedRooms = resolveAndValidateRooms(property, request);

        request.setRoomsToBook(resolvedRooms);

        // Sort by roomId to establish consistent lock ordering and avoid deadlocks
        List<HotelRoom> rooms = request.getRoomsToBook();
        rooms.sort((a, b) -> Integer.compare(a.getRoomId(), b.getRoomId()));
        List<HotelRoom> lockedRooms = new ArrayList<>();

        try {
            for (HotelRoom room : rooms) {
                log.info("Attempting to acquire lock for roomId: {}", room.getRoomId());
                if (room.getLock().tryLock(20, TimeUnit.MILLISECONDS)) {
                    // Double-check status after acquiring lock to prevent TOCTOU race condition
                    if (room.getBookedStatus() != RoomBookedStatus.AVAILABLE) {
                        room.getLock().unlock();
                        rollBackLocks(lockedRooms);
                        throw new RoomNotAvailableException("Room " + room.getRoomId() + " was just booked by another transaction.");
                    }
                    room.setBookedStatus(RoomBookedStatus.PROCESSING);
                    lockedRooms.add(room);
                    log.info("Successfully acquired lock for roomId: {}", room.getRoomId());
                } else {
                    log.warn("Timeout acquiring lock for roomId: {}", room.getRoomId());
                    rollBackLocks(lockedRooms);
                    throw new RoomNotAvailableException("Timeout: " + room.getRoomId() + " is currently busy");
                }
            }

            Booking booking = createBooking(request, property);

            Payment payment = paymentService.createPaymentIntent(
                    booking.getBookingId(),
                    booking.getTotalPrice(),
                    request.getPaymentMethod(),
                    UUID.randomUUID().toString()
            );

            booking.setPaymentId(payment.getPaymentId());
            bookingDao.save(booking);
            log.info("Booking initialized successfully with bookingId: {}", booking.getBookingId());
            return booking;

        } finally {
            for (HotelRoom room : lockedRooms) {
                room.getLock().unlock();
            }
        }
    }

    public void confirmBooking(String bookingId) {
        log.info("Confirming bookingId: {}", bookingId);
        Booking booking = getBooking(bookingId);
        booking.confirm();
        bookingDao.save(booking);

        Property property = propertyDao.findById(booking.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(booking.getPropertyId()));

        updateRoomStatus(property, booking, RoomBookedStatus.BOOKED);
        log.info("BookingId: {} successfully confirmed and rooms marked as BOOKED", bookingId);
    }

    public void failBooking(String bookingId) {
        log.info("Failing bookingId: {}", bookingId);
        Booking booking = getBooking(bookingId);
        booking.fail();
        bookingDao.save(booking);

        Property property = propertyDao.findById(booking.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(booking.getPropertyId()));

        updateRoomStatus(property, booking, RoomBookedStatus.AVAILABLE);
        log.info("BookingId: {} marked as FAILED and rooms reverted to AVAILABLE", bookingId);
    }

    public void cancel(String bookingId) {
        log.info("Processing cancellation for bookingId: {}", bookingId);
        Booking booking = getBooking(bookingId);
        booking.cancel();

        Property property = propertyDao.findById(booking.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(booking.getPropertyId()));

        CancellationPolicy policy = CancellationPolicyFactory.getPolicy(property.getCancellationPolicyType());

        if (booking.getPaymentId() != null) {
            Integer refundAmount = policy.calculateRefund(booking, booking.getTotalPrice());
            if (refundAmount > 0) {
                log.info("Triggering refund for paymentId: {} amount: {}", booking.getPaymentId(), refundAmount);
                paymentService.refund(booking.getPaymentId(), refundAmount);
            }
        }

        updateRoomStatus(property, booking, RoomBookedStatus.AVAILABLE);
        bookingDao.save(booking);
        log.info("BookingId: {} successfully cancelled", bookingId);
    }

    public Booking getBooking(String bookingId) {
        return bookingDao.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
    }

    /**
     * Resolves user-supplied roomIds against the property's availability map.
     * Each roomId must be AVAILABLE on every date in [checkIn, checkOut).
     * Throws RoomNotAvailableException if any roomId fails validation on any date.
     */
    private List<HotelRoom> resolveAndValidateRooms(Property property, BookingRequest request) {
        List<HotelRoom> resolved = new ArrayList<>();
        Map<LocalDate, List<HotelRoom>> hotelRooms = property.getHotelRooms();

        for (Integer roomId : request.getRoomIds()) {
            LocalDate date = request.getCheckIn();
            while (date.isBefore(request.getCheckOut())) {
                HotelRoom found = null;
                List<HotelRoom> roomsOnDate = hotelRooms.get(date);
                if (roomsOnDate != null) {
                    for (HotelRoom hr : roomsOnDate) {
                        if (hr.getRoomId().equals(roomId)
                                && hr.getBookedStatus() == RoomBookedStatus.AVAILABLE) {
                            found = hr;
                            break;
                        }
                    }
                }
                if (found == null) {
                    throw new RoomNotAvailableException(
                            "Room " + roomId + " is not available on " + date);
                }
                resolved.add(found);
                date = date.plusDays(1);
            }
        }
        return resolved;
    }

    private Booking createBooking(BookingRequest request, Property property) {
        int totalPrice = PricingStrategyFactory.getStrategy(property.getPricingStrategyType()).calculatePrice(request);

        // Collect distinct roomIds in order
        List<Integer> roomIds = new ArrayList<>();
        for (HotelRoom hr : request.getRoomsToBook()) {
            if (!roomIds.contains(hr.getRoomId())) {
                roomIds.add(hr.getRoomId());
            }
        }

        return Booking.builder()
                .bookingId(UUID.randomUUID().toString())
                .propertyId(request.getPropertyId())
                .roomIds(roomIds)
                .guestName(request.getGuestName())
                .numberOfGuests(request.getNumberOfGuests())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .bookingStatus(BookingStatus.PENDING)
                .totalPrice(totalPrice)
                .build();
    }

    private void rollBackLocks(List<HotelRoom> lockedRooms) {
        for (HotelRoom room : lockedRooms) {
            room.setBookedStatus(RoomBookedStatus.AVAILABLE);
        }
    }

    private void updateRoomStatus(Property property, Booking booking, RoomBookedStatus newStatus) {
        Map<LocalDate, List<HotelRoom>> hotelRooms = property.getHotelRooms();
        LocalDate date = booking.getCheckIn();

        while (date.isBefore(booking.getCheckOut())) {
            List<HotelRoom> rooms = hotelRooms.get(date);
            if (rooms != null) {
                for (HotelRoom room : rooms) {
                    if (booking.getRoomIds().contains(room.getRoomId())) {
                        room.setBookedStatus(newStatus);
                    }
                }
            }
            date = date.plusDays(1);
        }
    }
}

