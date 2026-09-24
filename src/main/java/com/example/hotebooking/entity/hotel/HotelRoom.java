package com.example.hotebooking.entity.hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.locks.ReentrantLock;

@Getter
@AllArgsConstructor
@Setter
public class HotelRoom {

    private Integer roomId;
    private Room room;
    private ReentrantLock lock;
    private RoomBookedStatus bookedStatus;


}


