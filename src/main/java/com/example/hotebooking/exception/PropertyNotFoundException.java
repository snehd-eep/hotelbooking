package com.example.hotebooking.exception;

public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException(Integer propertyId) {
        super("Property not found with id: " + propertyId);
    }
}

