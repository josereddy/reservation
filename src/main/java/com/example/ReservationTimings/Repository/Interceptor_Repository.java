package com.example.ReservationTimings.Repository;



import com.example.ReservationTimings.Entity.Interceptor_Data_DB;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Interceptor_Repository extends JpaRepository<Interceptor_Data_DB,Integer> {


    Page<Interceptor_Data_DB> findByApiname(String name, Pageable page);
}
