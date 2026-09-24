package com.example.hotebooking.entity.hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Room {

    private Integer roomId;
    private RoomType roomType;
    private String roomTypeDescription;
    private Integer roomCapacity;
    private Integer price;
}


