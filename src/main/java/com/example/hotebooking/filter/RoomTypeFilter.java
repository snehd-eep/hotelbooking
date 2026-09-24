package com.example.hotebooking.filter;

import com.example.hotebooking.entity.hotel.HotelRoom;
import com.example.hotebooking.entity.hotel.Property;
import com.example.hotebooking.entity.hotel.RoomBookedStatus;
import com.example.hotebooking.entity.hotel.RoomType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Filters properties that have at least one AVAILABLE room of the requested type.
 */
public class RoomTypeFilter implements FilterStrategy<RoomType> {

    @Override
    public List<Property> filter(List<Property> propertyList, RoomType roomType) {
        List<Property> result = new ArrayList<>();
        for (Property property : propertyList) {
            if (hasAvailableRoomOfType(property, roomType)) {
                result.add(property);
            }
        }
        return result;
    }

    private boolean hasAvailableRoomOfType(Property property, RoomType roomType) {
        if (property.getHotelRooms() == null || property.getHotelRooms().isEmpty()) {
            return false;
        }
        // Iterate over every date's room list
        for (Map.Entry<LocalDate, List<HotelRoom>> entry : property.getHotelRooms().entrySet()) {
            List<HotelRoom> rooms = entry.getValue();
            for (HotelRoom hotelRoom : rooms) {
                if (hotelRoom.getRoom() != null
                        && hotelRoom.getRoom().getRoomType() == roomType
                        && hotelRoom.getBookedStatus() == RoomBookedStatus.AVAILABLE) {
                    return true;  // found at least one available room of this type
                }
            }
        }
        return false;
    }
}


