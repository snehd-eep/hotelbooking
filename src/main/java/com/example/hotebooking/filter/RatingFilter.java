package com.example.hotebooking.filter;

import com.example.hotebooking.entity.hotel.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Filters properties where currentRating >= minimumRating.
 * Properties with no reviews yet (null rating) are excluded.
 */
public class RatingFilter implements FilterStrategy<Double> {

    @Override
    public List<Property> filter(List<Property> propertyList, Double minimumRating) {
        List<Property> result = new ArrayList<>();
        for (Property property : propertyList) {
            if (property.getCurrentRating() != null && property.getCurrentRating() >= minimumRating) {
                result.add(property);
            }
        }
        return result;
    }
}


