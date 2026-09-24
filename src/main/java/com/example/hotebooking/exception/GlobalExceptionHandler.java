package com.example.hotebooking.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<com.example.hotebooking.dto.ApiResponse> handleBookingNotFound(BookingNotFoundException ex) {
        log.warn("BookingNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new com.example.hotebooking.dto.ApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(PropertyNotFoundException.class)
    public ResponseEntity<com.example.hotebooking.dto.ApiResponse> handlePropertyNotFound(PropertyNotFoundException ex) {
        log.warn("PropertyNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new com.example.hotebooking.dto.ApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(RoomNotAvailableException.class)
    public ResponseEntity<com.example.hotebooking.dto.ApiResponse> handleRoomNotAvailable(RoomNotAvailableException ex) {
        log.warn("RoomNotAvailableException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new com.example.hotebooking.dto.ApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<com.example.hotebooking.dto.ApiResponse> handleInvalidState(IllegalStateException ex) {
        log.warn("IllegalStateException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new com.example.hotebooking.dto.ApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<com.example.hotebooking.dto.ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new com.example.hotebooking.dto.ApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<com.example.hotebooking.dto.ApiResponse> handleGeneric(Exception ex) {
        log.error("Unhandled Exception caught: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new com.example.hotebooking.dto.ApiResponse("Something went wrong: " + ex.getMessage()));
    }
}

