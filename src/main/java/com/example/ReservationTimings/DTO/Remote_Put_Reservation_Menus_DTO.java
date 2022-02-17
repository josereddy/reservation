package com.example.ReservationTimings.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Remote_Put_Reservation_Menus_DTO {

    private String restaurant_code;
    private String restaurant_name;
    private String reservation_day;
    private String booking_time;



}
