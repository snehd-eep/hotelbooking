package com.example.hotebooking.exception;

public class RoomNotAvailableException extends RuntimeException {

    public RoomNotAvailableException(String roomId) {
        super("Room " + roomId + " is not available — it may be booked or currently being processed");
    }

    public RoomNotAvailableException(String message, boolean isCustomMessage) {
        super(message);
    }
}

