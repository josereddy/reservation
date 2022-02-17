package com.example.ReservationTimings.Exception;

public class DuplicateLocationCodeFoundException extends RuntimeException {

    public DuplicateLocationCodeFoundException(String msg) {
        super(msg);
    }
}
