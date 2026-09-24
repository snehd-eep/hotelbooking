package com.example.hotebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddReviewRequest {

    private Integer propertyId;
    private String reviewDescription;
    private Integer rating;
}

