package com.example.hotebooking.entity.hotel;


import com.example.hotebooking.strategy.cancellation.CancellationPolicyType;
import com.example.hotebooking.strategy.pricing.PricingStrategyType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class Property {

    private Integer propertyId;
    private String propertyName;
    private Integer ownerId;
    private Address address;
    private List<Review> reviews;
    private Double currentRating;
    private List<Amenity> amenities;
    @JsonIgnore
    private Map<LocalDate,List<HotelRoom>> hotelRooms;
    private PricingStrategyType pricingStrategyType;
    private CancellationPolicyType cancellationPolicyType;

    @JsonProperty("availableRoomTypes")
    public Set<RoomType> getAvailableRoomTypes() {
        Set<RoomType> types = new HashSet<>();
        if (hotelRooms != null && !hotelRooms.isEmpty()) {
            for (List<HotelRoom> rooms : hotelRooms.values()) {
                for (HotelRoom hr : rooms) {
                    types.add(hr.getRoom().getRoomType());
                }
                break; // One day is enough to see the configured rooms
            }
        }
        return types;
    }



    public void calculateRating(){

        // Taking the copy of the list to avoid concurrent modifications
        List<Review> currentReviewList = this.reviews;
        double rating = 0.0;
        int count = 0;
        for(Review review : currentReviewList){
            rating += review.rating;
            count += 1;
        }
        this.currentRating = rating/count;
    }

}

