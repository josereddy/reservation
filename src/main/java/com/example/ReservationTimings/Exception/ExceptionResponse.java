package com.example.ReservationTimings.Exception;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ExceptionResponse {
    private Date Timestamp;
    private String message;
    private String details;
    public ExceptionResponse(Date timestamp,String message,String details){
        super();
        this.Timestamp = timestamp;
        this.message = message;
        this.details = details;
    }



}