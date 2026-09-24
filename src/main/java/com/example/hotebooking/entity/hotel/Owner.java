package com.example.hotebooking.entity.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class Owner {

    private Integer ownerId;
    private String ownerName;
    private List<Property> propertyList;
}


