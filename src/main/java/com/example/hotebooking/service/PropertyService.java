package com.example.hotebooking.service;

import com.example.hotebooking.dto.AddRoomRequest;
import com.example.hotebooking.dto.RoomAvailabilityDto;
import com.example.hotebooking.entity.hotel.*;
import com.example.hotebooking.exception.PropertyNotFoundException;
import com.example.hotebooking.repository.hotel.PropertyDao;
import com.example.hotebooking.filter.PropertyFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class PropertyService {

    private static final AtomicInteger roomIdCounter = new AtomicInteger(1);

    private final PropertyDao propertyDao;

    public PropertyService(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }

    public String addProperty(Property property) {
        log.info("Adding new property: {}", property.getPropertyName());
        String result = propertyDao.addProperty(property);
        log.info("Successfully added property: {}", property.getPropertyName());
        return result;
    }

    public String addRoom(Integer propertyId, AddRoomRequest request) {
        Property property = getProperty(propertyId);
        RoomType roomType;
        try {
            roomType = RoomType.valueOf(request.getRoomType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid room type: " + request.getRoomType() + ". Allowed values: " + Arrays.toString(RoomType.values()));
        }

        Room room = new Room(
                roomIdCounter.getAndIncrement(),
                roomType,
                request.getDescription(),
                request.getRoomCapacity(),
                request.getPrice()
        );

        // Pre-populate availability for the next 30 days
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 30; i++) {
            LocalDate date = today.plusDays(i);
            property.getHotelRooms().putIfAbsent(date, new ArrayList<>());
            HotelRoom hotelRoom = new HotelRoom(
                    room.getRoomId(),
                    room,
                    new ReentrantLock(),
                    RoomBookedStatus.AVAILABLE
            );
            property.getHotelRooms().get(date).add(hotelRoom);
        }
        log.info("Successfully added room {} to property {}", room.getRoomId(), propertyId);
        return "Room added successfully with ID: " + room.getRoomId();
    }

    public List<RoomAvailabilityDto> getAvailableRoomsByRange(Integer propertyId,
                                                               LocalDate checkIn,
                                                               LocalDate checkOut) {
        Property property = getProperty(propertyId);
        List<HotelRoom> candidates = property.getHotelRooms().get(checkIn);
        if (candidates == null) return new ArrayList<>();

        List<RoomAvailabilityDto> result = new ArrayList<>();
        for (HotelRoom hr : candidates) {
            if (hr.getBookedStatus() != RoomBookedStatus.AVAILABLE) continue;

            // Room must be AVAILABLE on every night in [checkIn, checkOut)
            boolean availableAllDates = true;
            LocalDate date = checkIn.plusDays(1);
            while (date.isBefore(checkOut)) {
                if (!containsAvailableRoom(property.getHotelRooms().get(date), hr.getRoomId())) {
                    availableAllDates = false;
                    break;
                }
                date = date.plusDays(1);
            }

            if (availableAllDates) {
                result.add(new RoomAvailabilityDto(
                        hr.getRoomId(),
                        hr.getRoom().getRoomType(),
                        hr.getRoom().getRoomTypeDescription(),
                        hr.getRoom().getRoomCapacity(),
                        hr.getRoom().getPrice()
                ));
            }
        }
        return result;
    }

    private boolean containsAvailableRoom(List<HotelRoom> rooms, Integer roomId) {
        if (rooms == null) return false;
        for (HotelRoom hr : rooms) {
            if (hr.getRoomId().equals(roomId) && hr.getBookedStatus() == RoomBookedStatus.AVAILABLE) {
                return true;
            }
        }
        return false;
    }

    public List<Property> searchProperties(PropertyFilter filter) {
        log.info("Searching properties with filter: {}", filter);
        return propertyDao.getProperties(filter);
    }

    public Property getProperty(Integer propertyId) {
        return propertyDao.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));
    }
}

