package com.example.ReservationTimings.Repository;


import com.example.ReservationTimings.Entity.User_Data_DB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface User_Data_Repository extends JpaRepository<User_Data_DB,Integer> {


    User_Data_DB findByUsername(String username);
}

