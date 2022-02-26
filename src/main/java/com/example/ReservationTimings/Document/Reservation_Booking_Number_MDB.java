package com.example.ReservationTimings.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Transient;

@Document(collection = "RESERVATION_BOOKING_NUMBER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation_Booking_Number_MDB {
    @Transient
    public static final String SEQUENCE_NAME = "Reservation_sequence";


    @Id
    private Long id;
    private String restaurantcode;
    private String restaurantstring;
    private Integer numberofbooking;


}
