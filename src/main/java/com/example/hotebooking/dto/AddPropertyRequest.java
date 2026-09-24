package com.example.hotebooking.dto;

import com.example.hotebooking.strategy.cancellation.CancellationPolicyType;
import com.example.hotebooking.entity.hotel.Amenity;
import com.example.hotebooking.strategy.pricing.PricingStrategyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddPropertyRequest {
    private String propertyName;
    private String locality;
    private Integer ownerId;
    private String address;
    private String city;
    private List<Amenity> amenities;
    private String pricingStrategyType;
    private String cancellationPolicyType;
}


