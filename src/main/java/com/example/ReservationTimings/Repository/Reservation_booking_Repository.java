package com.example.ReservationTimings.Repository;

import com.example.ReservationTimings.Document.Reservation_Booking_Number_MDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Reservation_booking_Repository extends MongoRepository<Reservation_Booking_Number_MDB,Long> {

    Reservation_Booking_Number_MDB findByRestaurantstring(String rest_string);

    List<Reservation_Booking_Number_MDB> findAllByRestaurantcode(String oldrestauarntcode);


    void deleteAllByRestaurantcode(String code);
}
