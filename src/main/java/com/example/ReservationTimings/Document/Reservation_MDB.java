package com.example.ReservationTimings.Document;


import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Transient;

@Document(collection = "RESERVATION")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("ModelReservation")
public class Reservation_MDB {
    @Transient
    public static final String SEQUENCE_NAME = "Reservation_sequence";


    @Id
    private Long id;

    private String restaurantcode;
    private String restaurantname;
    private String reservationday;
    private String reservationdate;
    private String bookingtime;

}
