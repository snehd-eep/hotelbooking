package com.example.hotebooking.dto;

import com.example.hotebooking.entity.hotel.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityDto {
    private Integer roomId;
    private RoomType roomType;
    private String description;
    private Integer roomCapacity;
    private Integer price;
}

