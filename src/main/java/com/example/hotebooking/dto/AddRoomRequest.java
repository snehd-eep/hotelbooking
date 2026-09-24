package com.example.hotebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddRoomRequest {
    private String roomType;
    private String description;
    private Integer roomCapacity;
    private Integer price;
}

