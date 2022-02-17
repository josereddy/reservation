package com.example.ReservationTimings.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation_Put_DTO {

    private Long id;
    private String restaurant_code;
    private String restaurant_name;
    private String reservation_date;
    private String booking_time;

}
