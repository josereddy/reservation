package com.example.ReservationTimings.Repository;

import com.example.ReservationTimings.Entity.Reservation_Backup_DB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Reservation_Backup_Repository extends JpaRepository<Reservation_Backup_DB,Long> {
}
