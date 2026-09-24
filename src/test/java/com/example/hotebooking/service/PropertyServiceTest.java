package com.example.hotebooking.service;

import com.example.hotebooking.strategy.cancellation.CancellationPolicyType;
import com.example.hotebooking.dto.RoomAvailabilityDto;
import com.example.hotebooking.entity.hotel.*;
import com.example.hotebooking.strategy.pricing.PricingStrategyType;
import com.example.hotebooking.repository.hotel.PropertyDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PropertyServiceTest {

    private PropertyDao propertyDao;
    private PropertyService propertyService;

    @BeforeEach
    void setUp() {
        propertyDao = mock(PropertyDao.class);
        propertyService = new PropertyService(propertyDao);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private HotelRoom buildRoom(int roomId, RoomBookedStatus status) {
        Room room = new Room(roomId, RoomType.SINGLE, "A single room", 1, 3000);
        return new HotelRoom(roomId, room, new ReentrantLock(), status);
    }

    /** Creates a property with one AVAILABLE SINGLE room pre-populated for `numNights` nights starting today. */
    private Property buildPropertyWithRoomForNights(int roomId, int numNights) {
        Map<LocalDate, List<HotelRoom>> hotelRooms = new HashMap<>();
        for (int i = 0; i < numNights; i++) {
            hotelRooms.put(LocalDate.now().plusDays(i),
                    new ArrayList<>(Collections.singletonList(buildRoom(roomId, RoomBookedStatus.AVAILABLE))));
        }
        Property property = new Property(1, "Test Hotel", 1, null, null, null, null,
                hotelRooms, PricingStrategyType.BASE, CancellationPolicyType.FREE);
        when(propertyDao.findById(1)).thenReturn(Optional.of(property));
        return property;
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    void availableRoomsReturnedForSingleNightStay() {
        buildPropertyWithRoomForNights(1, 1);

        List<RoomAvailabilityDto> result = propertyService.getAvailableRoomsByRange(
                1, LocalDate.now(), LocalDate.now().plusDays(1));

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getRoomId());
    }

    @Test
    void availableRoomsReturnedWhenOpenForAllNightsInRange() {
        buildPropertyWithRoomForNights(1, 3);

        List<RoomAvailabilityDto> result = propertyService.getAvailableRoomsByRange(
                1, LocalDate.now(), LocalDate.now().plusDays(3));

        assertEquals(1, result.size());
        assertEquals(RoomType.SINGLE, result.get(0).getRoomType());
        assertEquals(3000, result.get(0).getPrice());
    }

    @Test
    void roomIsExcludedWhenItIsBookedOnAnyNightInRange() {
        Property property = buildPropertyWithRoomForNights(1, 3);

        // Block the room on night 2 (index 1)
        property.getHotelRooms().get(LocalDate.now().plusDays(1))
                .get(0).setBookedStatus(RoomBookedStatus.BOOKED);

        List<RoomAvailabilityDto> result = propertyService.getAvailableRoomsByRange(
                1, LocalDate.now(), LocalDate.now().plusDays(3));

        assertTrue(result.isEmpty(), "Room blocked on night 2 should not appear in range results");
    }

    @Test
    void emptyListReturnedWhenPropertyHasNoRoomsOnCheckIn() {
        // Property has rooms on day 1 and 2 but NOT on checkIn day (today)
        Map<LocalDate, List<HotelRoom>> hotelRooms = new HashMap<>();
        hotelRooms.put(LocalDate.now().plusDays(1),
                new ArrayList<>(Collections.singletonList(buildRoom(1, RoomBookedStatus.AVAILABLE))));
        Property property = new Property(1, "Test Hotel", 1, null, null, null, null,
                hotelRooms, PricingStrategyType.BASE, CancellationPolicyType.FREE);
        when(propertyDao.findById(1)).thenReturn(Optional.of(property));

        List<RoomAvailabilityDto> result = propertyService.getAvailableRoomsByRange(
                1, LocalDate.now(), LocalDate.now().plusDays(2));

        assertTrue(result.isEmpty(), "No rooms on checkIn date → result must be empty");
    }

    @Test
    void onlyFreeRoomsShownWhenSomeAreBlockedMidStay() {
        // Two rooms: room 1 is free all nights, room 2 is booked on night 2
        Map<LocalDate, List<HotelRoom>> hotelRooms = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            List<HotelRoom> dayRooms = new ArrayList<>();
            dayRooms.add(buildRoom(1, RoomBookedStatus.AVAILABLE));  // always free
            dayRooms.add(buildRoom(2, RoomBookedStatus.AVAILABLE));  // will be blocked on night 2
            hotelRooms.put(LocalDate.now().plusDays(i), dayRooms);
        }
        // Block room 2 on night 2
        hotelRooms.get(LocalDate.now().plusDays(1)).get(1).setBookedStatus(RoomBookedStatus.BOOKED);

        Property property = new Property(1, "Test Hotel", 1, null, null, null, null,
                hotelRooms, PricingStrategyType.BASE, CancellationPolicyType.FREE);
        when(propertyDao.findById(1)).thenReturn(Optional.of(property));

        List<RoomAvailabilityDto> result = propertyService.getAvailableRoomsByRange(
                1, LocalDate.now(), LocalDate.now().plusDays(3));

        assertEquals(1, result.size(), "Only room 1 should appear; room 2 is blocked mid-stay");
        assertEquals(1, result.get(0).getRoomId());
    }
}


