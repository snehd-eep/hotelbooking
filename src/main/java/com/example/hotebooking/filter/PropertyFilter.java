package com.example.hotebooking.filter;

import com.example.hotebooking.entity.hotel.Amenity;
import com.example.hotebooking.entity.hotel.Property;
import com.example.hotebooking.entity.hotel.RoomType;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable search criteria object built via Builder.
 * Chains all active filters — only filters with non-null values are applied.
 * To add a new filter: add a field + Builder setter + one 'if' block in filter().
 */
@Getter
@Builder()
public class PropertyFilter {

    private final Integer priceRangeLow;
    private final Integer priceRangeHigh;
    private final List<Amenity> amenities;
    private final Double rating;
    private final RoomType roomType;
    private final String city;



    /**
     * Applies all active filters sequentially.
     * Each filter receives the output of the previous one (pipeline / chain pattern).
     */
    public List<Property> filter(List<Property> propertyList) {
        // Defensive copy — never mutate the caller's list
        List<Property> result = new ArrayList<>(propertyList);

        if (city != null && !city.isBlank()) {
            List<Property> cityFiltered = new ArrayList<>();
            for (Property property : result) {
                if (city.equalsIgnoreCase(property.getAddress().getCity())) {
                    cityFiltered.add(property);
                }
            }
            result = cityFiltered;
        }
        if (priceRangeLow != null) {
            result = new PriceRangeLowFilter().filter(result, priceRangeLow);
        }
        if (priceRangeHigh != null) {
            result = new PriceRangeHighFilter().filter(result, priceRangeHigh);
        }
        if (rating != null) {
            result = new RatingFilter().filter(result, rating);
        }
        if (amenities != null && !amenities.isEmpty()) {
            result = new AmenityFilter().filter(result, amenities);
        }
        if (roomType != null) {
            result = new RoomTypeFilter().filter(result, roomType);
        }

        return result;
    }

}


