package com.example.hotebooking.entity.hotel;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter

public class Address {


    private static AtomicInteger id = new AtomicInteger(1);
    private Integer addressId;
    private String city;
    private String locality;
    private String address;

    public Address(String city, String locality, String address){
        this.addressId = id.getAndIncrement();
        this.city = city;
        this.locality = locality;
        this.address = address;

    }

}

