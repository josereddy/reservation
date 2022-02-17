package com.example.ReservationTimings.Services;



import com.example.ReservationTimings.DTO.Remote_Put_Reservation_Menus_DTO;
import com.example.ReservationTimings.DTO.Reservation_Kafka_DTO;
import com.example.ReservationTimings.DTO.Reservation_Post_DTO;
import com.example.ReservationTimings.DTO.Reservation_Put_DTO;
import com.example.ReservationTimings.Document.Reservation_MDB;
import com.example.ReservationTimings.Document.Reservation_Booking_Number_MDB;
import com.example.ReservationTimings.Entity.Reservation_Backup_DB;
import com.example.ReservationTimings.Exception.BookingExceedException;
import com.example.ReservationTimings.Exception.DateFormatException;
import com.example.ReservationTimings.Exception.InvalidTimeFormatException;
import com.example.ReservationTimings.Exception.UserNotFoundException;
import com.example.ReservationTimings.Repository.Reservation_booking_Repository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Check_ConvertService {


    private static final Logger log = LogManager.getLogger(Check_ConvertService.class.getName());


    @Autowired
    private SequenceGeneratorService seq_service;
    @Autowired
    private Reservation_booking_Repository booking_repository;


    @Autowired
    private RemoteRequest remote_request_service;


    private Reservation_Post_DTO reservation_post_dto;
    private Reservation_Booking_Number_MDB booking_reservation_mdb;
    private Reservation_MDB reservation;
    private Reservation_Backup_DB reservation_backup_db;


    ///check for valid entry
    public boolean check_reservation_post_dto(Reservation_Post_DTO reservation_post_dto) {
        log.info("CHECK_CONVERT_SERVICE: Entered Reservation Data check Service");


        if (reservation_post_dto.getRestaurant_code() == null || reservation_post_dto.getRestaurant_name() == null ||
                reservation_post_dto.getReservation_date() == null || reservation_post_dto.getBooking_time() == null)
            return false;

        ////checking for date pattern
        Pattern p = Pattern.compile("[0-9]{4}(\\-)[01][0-9](\\-)[0123][0-9]");
        Matcher m=p.matcher(reservation_post_dto.getReservation_date());
        if((m.find()))
        {

            LocalDate today = LocalDate.now(ZoneId.of("America/Montreal"));
            if(!((today.toString().compareTo(m.group()))<=0))
                throw new UserNotFoundException("Please check the Date must be cannot  be past date ");
        }
        else
            throw new UserNotFoundException("Please check the Date format must be YYYY-MM-DD");

        ///checking for  reservation 24 hour pattern
        if (check_booking_time(reservation_post_dto.getBooking_time())) {
            log.info("CHECK_CONVERT_SERVICE: Successfully Exited Reservation Data check Service");
            return true;
        } else {
            return false;
        }
    }





    public boolean check_booking_time(String bookingtime) {
        log.info("CHECK_CONVERT_SERVICE: Entered CHECK_time_pattern");
        String pattern = "[0-2][0-9](\\:)[03][0](\\-)[0-2][0-9](\\:)[03][0]";

        Pattern p1 = Pattern.compile(pattern);
        Matcher m1 = p1.matcher(bookingtime);
        if (m1.find()) {
            Pattern p2 = Pattern.compile("\\-");
            String[] timers = p2.split(m1.group());
            if ((timers[0].compareTo(timers[1])) < 0) {
                Pattern p3 = Pattern.compile("[01][0-9]:[03][0]");
                Pattern p4 = Pattern.compile("[2][0-3]:[03][0]");
                Matcher m2 = p3.matcher(timers[0]);
                Matcher m3 = p3.matcher(timers[1]);
                if (!(m2.find())) {
                    Matcher m4 = p4.matcher(timers[0]);
                    if (!(m4.find()))
                        throw new InvalidTimeFormatException("Please check all time formats are 24hrs ex:00:00-23:30");
                }
                if (!(m3.find())) {

                    Matcher m5 = p4.matcher(timers[1]);
                    if (!(m5.find()))
                        throw new InvalidTimeFormatException("Please check all time formats are 24hrs ex:00:00-23:30 invalid time format exception");
                }
            } else
                throw new InvalidTimeFormatException("Please check all time formats are 24hrs ex:00:00-23:30 and must be in a half hour time interval");
        } else
            throw new InvalidTimeFormatException("Please check all time formats are 24hrs ex:00:00-23:30 and must be in a half hour time interval");

        log.info("CHECK_CONVERT_SERVICE: Exited CHECK_time_pattern");
        return true;
    }




    ///check for already existed data

    public Boolean check_existed_timings(Reservation_MDB reservation_mdb) {
        log.info("CHECK_CONVERT_SERVICE: Entered CHECK_EXISTED_TIMING Service");
           Integer no_of_booking;


        String booking_data = reservation_mdb.getRestaurantcode()+reservation_mdb.getRestaurantname()+","+
                reservation_mdb.getReservationdate()+reservation_mdb.getBookingtime();
        //trying to get the booking object

        booking_reservation_mdb=booking_repository.findByRestaurantstring(booking_data);

        ////if exists get the update the no of record and get back the record number
        log.info("CHECK_CONVERT_SERVICE: Exited CHECK_EXISTED_TIMING Service");
             if(booking_reservation_mdb!=null)
                 return true;
             return false;
    }






    //////verify before addd
    public Boolean verify_before_add( Reservation_MDB reservation_mdb ) {
        ////check for already existed timing

        log.info("CHECK AND CONVERT:ENTERED INTO THE verify _before_Add");
        if((check_existed_timings(reservation_mdb))) {
            increase_no_ofbookings(reservation_mdb);
        }

        else {
            ///remote request
            Integer remote_response = remote_request_service.remote_check_put_reservation_menus(convert_mdb_remote_put_dto(reservation_mdb));
            switch (remote_response) {
                case 1:
                    throw new UserNotFoundException("USAGE:Restaurant code NOT FOUND look back MENU API");
                case 2:
                    throw new UserNotFoundException("USAGE:Restaurant NAME NOT FOUND look back MENU API");
                case 3:
                    throw new UserNotFoundException("USAGE:Restaurant Operation Timings invalid look back MENU API");
                case 4:
                    throw new UserNotFoundException("USAGE:Restaurant Closed ON "+reservation_mdb.getReservationday());

                case 0:
                    increase_no_ofbookings(reservation_mdb);
            }
        }
//
//        reservation_repo.save(reservation_data);
        log.info("Check and convert: Successfully Exited the verifyReservation to database");
        return true;

    }







    //////////incremental   decremental service

    public Integer increase_no_ofbookings(Reservation_MDB reservation_mdb) {
        log.info("CHECK_CONVERT_SERVICE: Entered Increase_no_of_Booking Service");
           Integer no_of_booking;


           String booking_data = reservation_mdb.getRestaurantcode()+reservation_mdb.getRestaurantname()+","+
                   reservation_mdb.getReservationdate()+reservation_mdb.getBookingtime();
           //trying to get the booking object

             booking_reservation_mdb=booking_repository.findByRestaurantstring(booking_data);




           ////if exists get the update the no of record and get back the record number
             if(booking_reservation_mdb!=null) {

                  no_of_booking = booking_reservation_mdb.getNumberofbooking() + 1;
                if((no_of_booking)>=4) {
                     throw new BookingExceedException("Restaurant Booking full for " + reservation_mdb.getReservationday() + "  : " + reservation_mdb.getReservationdate() + "   :  " + reservation_mdb.getBookingtime());
                 }
                 booking_reservation_mdb.setNumberofbooking(no_of_booking);
                 booking_repository.save(booking_reservation_mdb);
             }

             //if not just add the object to booking database
             else {
                 booking_reservation_mdb = new Reservation_Booking_Number_MDB();
                 no_of_booking = 1;
                 booking_reservation_mdb.setId(seq_service.generateSequence(Reservation_Booking_Number_MDB.SEQUENCE_NAME));
                booking_reservation_mdb.setRestaurantcode(reservation_mdb.getRestaurantcode());
                 booking_reservation_mdb.setRestaurantstring(booking_data);
                 booking_reservation_mdb.setNumberofbooking(no_of_booking);
                 booking_repository.save(booking_reservation_mdb);
             }

        log.info("CHECK_CONVERT_SERVICE: Exited Increase_no_of_Booking Service");
             return no_of_booking;
    }


    public boolean decrement_booking(Reservation_MDB reservation_mdb) {

        log.info("CHECK_CONVERT_SERVICE: Entered Decrease_no_of_Booking Service");



        String booking_data = reservation_mdb.getRestaurantcode()+reservation_mdb.getRestaurantname()+","+
                reservation_mdb.getReservationdate()+reservation_mdb.getBookingtime();
        //trying to get the booking object

        booking_reservation_mdb=booking_repository.findByRestaurantstring(booking_data);
        if(booking_reservation_mdb.getNumberofbooking()==1) {
            booking_repository.deleteById(booking_reservation_mdb.getId());
            log.info("CHECK_CONVERT_SERVICE: SUCCESSFULLY Exited Decrease_no_of_Booking Service");
            return true;

        }

        //if not just add the object to booking database
        else if(booking_reservation_mdb.getNumberofbooking()>1)
        {
            booking_reservation_mdb.setNumberofbooking((booking_reservation_mdb.getNumberofbooking())-1);
            booking_repository.save(booking_reservation_mdb);
            log.info("CHECK_CONVERT_SERVICE:SUCCESSFULLY Exited Decrease_no_of_Booking Service");
            return true;

        }
        log.info("CHECK_CONVERT_SERVICE:Failed Exited Decrease_no_of_Booking Service");
        return false;



    }






    public Boolean findbookingnumber(Reservation_Put_DTO reservation_put_dto) {

        log.info("CHECK_CONVERT_SERVICE: Entered findBooking number Service");
        Integer no_of_booking;


        String booking_data = reservation_put_dto.getRestaurant_code()+reservation_put_dto.getRestaurant_name()+","+
                reservation_put_dto.getReservation_date()+reservation_put_dto.getBooking_time();
        //trying to get the booking object

        booking_reservation_mdb=booking_repository.findByRestaurantstring(booking_data);




        ////if exists get the update the no of record and get back the record number
        if(booking_reservation_mdb!=null) {
            if (booking_reservation_mdb.getNumberofbooking() > 3) {

                log.info("CHECK_CONVERT_SERVICE: Failed Exited findBooking number Service");
                return false;
            }

        }

        log.info("CHECK_CONVERT_SERVICE: Successfully Exited findBooking number Service");
        return true;

    }








    //////////////////////////////////////////DTO Document
    public Reservation_MDB DtoDocument_convert(Reservation_Post_DTO reserve_post_dto) {
        log.info("CHECK_CONVERT_SERVICE: Entered INTO Reservation DATA TO ENTITY Conversion SERVICE");
        reservation = new Reservation_MDB();

        ///finding the day
        LocalDate today = LocalDate.now(ZoneId.of("America/Montreal"));
        Pattern p = Pattern.compile("\\-");
        String[] s = p.split(reserve_post_dto.getReservation_date());
        java.time.DayOfWeek dayOfWeek;
        try {
            LocalDate localDate = LocalDate.of(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
            dayOfWeek = localDate.getDayOfWeek();
        }
        catch(DateTimeException e)
        {
            throw new DateFormatException("PLease enter a valid date");
        }


        ///actual coverstion to d to document
        reservation.setId(seq_service.generateSequence(Reservation_MDB.SEQUENCE_NAME));
        reservation.setRestaurantcode(reserve_post_dto.getRestaurant_code());
        reservation.setRestaurantname(reserve_post_dto.getRestaurant_name());
        reservation.setReservationdate(reserve_post_dto.getReservation_date());
        reservation.setReservationday(dayOfWeek.toString());
        reservation.setBookingtime(reserve_post_dto.getBooking_time());


        log.info("CHECK_CONVERT_SERVICE: EXITED FROM Reservation DATA to Entity Conversion Service");
        return reservation;

    }

    public Remote_Put_Reservation_Menus_DTO convert_mdb_remote_put_dto(Reservation_MDB reservation_mdb) {
        log.info("CHECK_CONVERT_SERVICE: Entered Get remote_dto exception");

        Remote_Put_Reservation_Menus_DTO remote_put_reservation_menus_dto = new Remote_Put_Reservation_Menus_DTO();
        remote_put_reservation_menus_dto.setRestaurant_code(reservation_mdb.getRestaurantcode());
        remote_put_reservation_menus_dto.setReservation_day(reservation_mdb.getReservationday());
        remote_put_reservation_menus_dto.setBooking_time(reservation_mdb.getBookingtime());
        remote_put_reservation_menus_dto.setRestaurant_name(reservation_mdb.getRestaurantname());
        log.info("CHECK_CONVERT_SERVICE: Exited Get remote_dto exception");
        return remote_put_reservation_menus_dto;

    }







    /////////////////get all fields in set
    public Set<String> getAllReservationFeilds() {
        log.info("CHECK_CONVERT_SERVICE: Entered into get All fields Service");

        Set<String> fields = new HashSet<>();
        fields.add("id");
        fields.add("restaurantcode");
        fields.add("restaurantsname");
        fields.add("reservationday");
        fields.add("reservationdate");
        fields.add("bookingtime");
        fields.add("numberofBooking");
        log.info("CHECK_CONVERT_SERVICE: Exited from get All fields Service");
        return fields;



    }


    public List<Reservation_Backup_DB> convertdtoentity(List<Reservation_Kafka_DTO> reservation_kafka_dto_list)
    {
        List<Reservation_Backup_DB> reservation_backup_db_list = new ArrayList<>();
        for(Reservation_Kafka_DTO reservation_kafka_dto:reservation_kafka_dto_list)
        {
            reservation_backup_db = new Reservation_Backup_DB();
            reservation_backup_db.setId(reservation_kafka_dto.getId());
            reservation_backup_db.setRestaurantcode(reservation_kafka_dto.getRestaurant_code());
            reservation_backup_db.setRestaurantname(reservation_kafka_dto.getRestaurant_name());
            reservation_backup_db.setReservationdate(reservation_kafka_dto.getReservation_date());
            reservation_backup_db.setReservationday(reservation_kafka_dto.getReservation_day());
            reservation_backup_db.setBookingtime(reservation_kafka_dto.getBooking_time());
            reservation_backup_db_list.add(reservation_backup_db);

        }

        return  reservation_backup_db_list;
    }

    public List<Reservation_Kafka_DTO> convertentitytodto(List<Reservation_MDB> reservation_mdbs_list) {
        List<Reservation_Kafka_DTO> reservation_kafka_dto_list=new ArrayList<>();
        Reservation_Kafka_DTO reservation_kafka_dto;
        for(Reservation_MDB reservation_mdb:reservation_mdbs_list)
        {
            reservation_kafka_dto =new Reservation_Kafka_DTO();
            reservation_kafka_dto.setId(reservation_mdb.getId());
            reservation_kafka_dto.setRestaurant_code(reservation_mdb.getRestaurantcode());
            reservation_kafka_dto.setRestaurant_name(reservation_mdb.getRestaurantname());
            reservation_kafka_dto.setReservation_day(reservation_mdb.getReservationday());
            reservation_kafka_dto.setReservation_date(reservation_mdb.getReservationdate());
            reservation_kafka_dto.setBooking_time(reservation_mdb.getBookingtime());
            reservation_kafka_dto_list.add(reservation_kafka_dto);

        }
        return reservation_kafka_dto_list;
    }
}