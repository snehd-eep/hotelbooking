package com.example.hotebooking.repository.hotel;

import com.example.hotebooking.entity.hotel.Property;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.example.hotebooking.filter.PropertyFilter;


@Repository
public class PropertyDao {

    // ConcurrentHashMap handles thread-safe reads/writes without external locking
    private final Map<Integer, Property> propertyStore = new ConcurrentHashMap<>();
    private static final AtomicInteger propertyIdCounter = new AtomicInteger(1);

    public String addProperty(Property property) {
        for (Property p : propertyStore.values()) {
            if (p.getPropertyName().equalsIgnoreCase(property.getPropertyName())) {
                throw new IllegalStateException("Property with name '" + property.getPropertyName() + "' already exists");
            }
        }
        property.setPropertyId(propertyIdCounter.getAndIncrement());
        propertyStore.put(property.getPropertyId(), property);
        return "Property added successfully with ID: " + property.getPropertyId();
    }

    public Optional<Property> findById(Integer propertyId) {
        return Optional.ofNullable(propertyStore.get(propertyId));
    }


    public List<Property> getProperties(PropertyFilter filter) {
        List<Property> all = new ArrayList<>(propertyStore.values());
        return filter.filter(all);
    }

    public List<Property> getAllProperties() {
        return new ArrayList<>(propertyStore.values());
    }
}


