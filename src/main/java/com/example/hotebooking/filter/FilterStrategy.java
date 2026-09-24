package com.example.hotebooking.filter;

import com.example.hotebooking.entity.hotel.Property;

import java.util.List;

public interface FilterStrategy<T> {

    List<Property> filter(List<Property> propertyList, T constraint);
}


