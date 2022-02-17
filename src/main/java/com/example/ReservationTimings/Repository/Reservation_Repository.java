package com.example.ReservationTimings.Repository;

import com.example.ReservationTimings.Document.Reservation_MDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface Reservation_Repository extends MongoRepository<Reservation_MDB,Long> {




    void deleteAllByRestaurantcode(String code);

    List<Reservation_MDB> findByRestaurantcode(String code);

    @Query(value = "{'id':{$gt:?0}}")
    List<Reservation_MDB> getbyid(Long id_check_sum);
    //
}
