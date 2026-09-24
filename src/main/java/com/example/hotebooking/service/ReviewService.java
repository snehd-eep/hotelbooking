package com.example.hotebooking.service;


import com.example.hotebooking.dto.AddReviewRequest;
import com.example.hotebooking.entity.hotel.Property;
import com.example.hotebooking.entity.hotel.Review;
import com.example.hotebooking.exception.PropertyNotFoundException;
import com.example.hotebooking.repository.hotel.PropertyDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReviewService {


    private final PropertyDao propertyDao;

    public ReviewService(PropertyDao propertyDao){
        this.propertyDao = propertyDao;
    }

    public void createReview(AddReviewRequest addReviewRequest){
        log.info("Creating review for propertyId: {}", addReviewRequest.getPropertyId());
        Optional<Property> optionalProperty = propertyDao.findById(addReviewRequest.getPropertyId());
        if(optionalProperty.isPresent()){
            Property property = optionalProperty.get();
            List<Review> reviewList = property.getReviews();
            if (reviewList == null) {
                reviewList = new java.util.ArrayList<>();
                property.setReviews(reviewList);
            }
            Review review = new Review(addReviewRequest.getRating(), addReviewRequest.getReviewDescription());
            reviewList.add(review);
            property.calculateRating();
            log.info("Review successfully created and rating updated for propertyId: {}", property.getPropertyId());
        }else{
            throw new PropertyNotFoundException(addReviewRequest.getPropertyId());
        }

    }
}

