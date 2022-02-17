package com.example.ReservationTimings.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation_Backup_DB {
    @Id
    private Long id;

    private String restaurantcode;
    private String restaurantname;
    private String reservationdate;
    private String reservationday;
    private String bookingtime;




}
