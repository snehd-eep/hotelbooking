package com.example.hotebooking.entity.hotel;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Review {

    @Max(5)
    @Min(1)
    Integer rating;
    String review;
}

