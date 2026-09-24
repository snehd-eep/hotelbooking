package com.example.hotebooking.filter;

import com.example.hotebooking.entity.hotel.Amenity;
import com.example.hotebooking.entity.hotel.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Filters properties that have ALL of the requested amenities.
 * Works correctly because Amenity is an enum — no custom equals() needed.
 */
public class AmenityFilter implements FilterStrategy<List<Amenity>> {

    @Override
    public List<Property> filter(List<Property> propertyList, List<Amenity> requiredAmenities) {
        if (requiredAmenities == null || requiredAmenities.isEmpty()) {
            return propertyList;
        }

        List<Property> result = new ArrayList<>();
        for (Property property : propertyList) {
            if (property.getAmenities() != null && hasAllAmenities(property.getAmenities(), requiredAmenities)) {
                result.add(property);
            }
        }
        return result;
    }

    private boolean hasAllAmenities(List<Amenity> propertyAmenities, List<Amenity> requiredAmenities) {
        for (Amenity required : requiredAmenities) {
            if (!propertyAmenities.contains(required)) {
                return false;  // missing at least one required amenity
            }
        }
        return true;
    }
}


