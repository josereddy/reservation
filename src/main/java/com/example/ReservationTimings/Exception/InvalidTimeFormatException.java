package com.example.ReservationTimings.Exception;

public class InvalidTimeFormatException extends RuntimeException{
    public InvalidTimeFormatException( String msg)
    {
        super(msg);
    }
}
