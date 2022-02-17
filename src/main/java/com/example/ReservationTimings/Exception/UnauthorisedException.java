package com.example.ReservationTimings.Exception;

public class UnauthorisedException extends  RuntimeException{

    public UnauthorisedException(String msg)
    {
        super(msg);
    }
}
