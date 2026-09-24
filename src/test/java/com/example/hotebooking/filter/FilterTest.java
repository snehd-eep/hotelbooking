package com.example.hotebooking.filter;

import com.example.hotebooking.entity.hotel.Amenity;
import com.example.hotebooking.entity.hotel.HotelRoom;
import com.example.hotebooking.entity.hotel.Property;
import com.example.hotebooking.entity.hotel.Room;
import com.example.hotebooking.entity.hotel.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterTest {

    private Property p1;
    private Property p2;
    private Property p3;

    @BeforeEach
    void setUp() {
        p1 = new Property(1, "P1", 1, null, null, 4.5, Arrays.asList(Amenity.WIFI, Amenity.POOL), null, null, null);
        Room r1 = new Room(1, RoomType.SINGLE, "Single", 1, 1500);
        HotelRoom hr1 = new HotelRoom(1, r1, null, com.example.hotebooking.entity.hotel.RoomBookedStatus.AVAILABLE);
        p1.setHotelRooms(Collections.singletonMap(java.time.LocalDate.now(), Arrays.asList(hr1)));

        p2 = new Property(2, "P2", 1, null, null, 3.5, Collections.singletonList(Amenity.WIFI), null, null, null);
        Room r2 = new Room(2, RoomType.DOUBLE, "Double", 2, 2500);
        HotelRoom hr2 = new HotelRoom(2, r2, null, com.example.hotebooking.entity.hotel.RoomBookedStatus.AVAILABLE);
        p2.setHotelRooms(Collections.singletonMap(java.time.LocalDate.now(), Arrays.asList(hr2)));

        p3 = new Property(3, "P3", 1, null, null, 4.8, Arrays.asList(Amenity.POOL, Amenity.GYM), null, null, null);
        Room r3 = new Room(3, RoomType.SUITE, "Suite", 4, 6000);
        HotelRoom hr3 = new HotelRoom(3, r3, null, com.example.hotebooking.entity.hotel.RoomBookedStatus.AVAILABLE);
        p3.setHotelRooms(Collections.singletonMap(java.time.LocalDate.now(), Arrays.asList(hr3)));
    }

    @Test
    void testAmenityFilter() {
        AmenityFilter filter = new AmenityFilter();
        List<Property> filtered = filter.filter(Arrays.asList(p1, p2, p3), Collections.singletonList(Amenity.POOL));
        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(p1));
        assertTrue(filtered.contains(p3));
    }

    @Test
    void testRatingFilter() {
        RatingFilter filter = new RatingFilter();
        List<Property> filtered = filter.filter(Arrays.asList(p1, p2, p3), 4.0);
        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(p1));
        assertTrue(filtered.contains(p3));
    }

    @Test
    void testRoomTypeFilter() {
        RoomTypeFilter filter = new RoomTypeFilter();
        List<Property> filtered = filter.filter(Arrays.asList(p1, p2, p3), RoomType.DOUBLE);
        assertEquals(1, filtered.size());
        assertTrue(filtered.contains(p2));
    }

    @Test
    void testPriceRangeLowFilter() {
        PriceRangeLowFilter filter = new PriceRangeLowFilter();
        List<Property> filtered = filter.filter(Arrays.asList(p1, p2, p3), 2000);
        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(p2));
        assertTrue(filtered.contains(p3));
    }

    @Test
    void testPriceRangeHighFilter() {
        PriceRangeHighFilter filter = new PriceRangeHighFilter();
        List<Property> filtered = filter.filter(Arrays.asList(p1, p2, p3), 2000);
        assertEquals(1, filtered.size());
        assertTrue(filtered.contains(p1));
    }
}
