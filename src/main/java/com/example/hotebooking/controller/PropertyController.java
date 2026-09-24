package com.example.hotebooking.controller;

import com.example.hotebooking.strategy.cancellation.CancellationPolicyType;
import com.example.hotebooking.dto.AddPropertyRequest;
import com.example.hotebooking.dto.AddRoomRequest;
import com.example.hotebooking.dto.ApiResponse;
import com.example.hotebooking.dto.RoomAvailabilityDto;
import com.example.hotebooking.entity.hotel.Address;
import com.example.hotebooking.entity.hotel.Property;
import com.example.hotebooking.strategy.pricing.PricingStrategyType;
import com.example.hotebooking.filter.PropertyFilter;
import com.example.hotebooking.service.PropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/properties")
@Slf4j
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addProperty(@RequestBody AddPropertyRequest request) {
        log.info("Received request to add property: {}", request.getPropertyName());
        Address address = new Address(request.getCity(),request.getLocality(),request.getAddress());
        
        PricingStrategyType pricingType = PricingStrategyType.BASE;
        if (request.getPricingStrategyType() != null) {
            try {
                pricingType = PricingStrategyType.valueOf(request.getPricingStrategyType());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid pricing strategy: " + request.getPricingStrategyType() + 
                        ". Allowed values: " + java.util.Arrays.toString(PricingStrategyType.values()));
            }
        }

        CancellationPolicyType cancellationType = CancellationPolicyType.FREE;
        if (request.getCancellationPolicyType() != null) {
            try {
                cancellationType = CancellationPolicyType.valueOf(request.getCancellationPolicyType());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid cancellation policy: " + request.getCancellationPolicyType() + 
                        ". Allowed values: " + java.util.Arrays.toString(CancellationPolicyType.values()));
            }
        }

        Property property = new Property(
                null, // will be auto-generated in Dao
                request.getPropertyName(),
                request.getOwnerId(),// description
                address, // address
                new ArrayList<>(), // reviews
                0.0, // rating
                request.getAmenities(),
                new ConcurrentHashMap<>(), // hotelRooms
                pricingType,
                cancellationType
        );
        return ResponseEntity.ok(new ApiResponse(propertyService.addProperty(property)));
    }


    @PostMapping("/{propertyId}/rooms")
    public ResponseEntity<ApiResponse> addRoom(@PathVariable Integer propertyId, @RequestBody AddRoomRequest request) {
        log.info("Received request to add room to property {}", propertyId);
        return ResponseEntity.ok(new ApiResponse(propertyService.addRoom(propertyId, request)));
    }

    @GetMapping("/{propertyId}/availability")
    public ResponseEntity<List<RoomAvailabilityDto>> getAvailability(
            @PathVariable Integer propertyId,
            @RequestParam String checkIn,
            @RequestParam String checkOut) {
        log.info("Checking availability for property {} from {} to {}", propertyId, checkIn, checkOut);
        return ResponseEntity.ok(propertyService.getAvailableRoomsByRange(
                propertyId,
                LocalDate.parse(checkIn),
                LocalDate.parse(checkOut)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Property>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Double minRating) {

        PropertyFilter filter = PropertyFilter.builder()
                .city(city)
                .priceRangeLow(minPrice)
                .priceRangeHigh(maxPrice)
                .rating(minRating)
                .build();

        return ResponseEntity.ok(propertyService.searchProperties(filter));
    }
}

