package com.example.hotebooking.filter;

import com.example.hotebooking.entity.hotel.HotelRoom;
import com.example.hotebooking.entity.hotel.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Filters properties where price <= maxPrice (price ceiling / upper bound).
 */
public class PriceRangeHighFilter implements FilterStrategy<Integer> {

    @Override
    public List<Property> filter(List<Property> propertyList, Integer maxPrice) {
        List<Property> result = new ArrayList<>();
        for (Property property : propertyList) {
            boolean hasMatchingRoom = false;
            if (property.getHotelRooms() != null) {
                for (List<HotelRoom> rooms : property.getHotelRooms().values()) {
                    for (HotelRoom room : rooms) {
                        if (room.getRoom().getPrice() != null && room.getRoom().getPrice() <= maxPrice) {
                            hasMatchingRoom = true;
                            break;
                        }
                    }
                    if (hasMatchingRoom) break;
                }
            }
            if (hasMatchingRoom) {
                result.add(property);
            }
        }
        return result;
    }
}


