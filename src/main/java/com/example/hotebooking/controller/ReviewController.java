package com.example.hotebooking.controller;

import lombok.extern.slf4j.Slf4j;
import com.example.hotebooking.dto.AddPropertyRequest;
import com.example.hotebooking.dto.AddReviewRequest;
import com.example.hotebooking.dto.ApiResponse;
import com.example.hotebooking.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createReview(@RequestBody AddReviewRequest addReviewRequest)  {
        log.info("Received request to create review for propertyId: {}", addReviewRequest.getPropertyId());
        reviewService.createReview(addReviewRequest);
        return ResponseEntity.ok(new com.example.hotebooking.dto.ApiResponse("Review Added Successfully"));

    }
}

