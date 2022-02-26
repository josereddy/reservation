package com.example.ReservationTimings.Services;


import com.example.ReservationTimings.DTO.Remote_Put_Location_Menus_Reservation_DTO;
import com.example.ReservationTimings.DTO.Reservation_Kafka_DTO;
import com.example.ReservationTimings.DTO.Reservation_Post_DTO;
import com.example.ReservationTimings.DTO.Reservation_Put_DTO;
import com.example.ReservationTimings.Document.Reservation_MDB;
import com.example.ReservationTimings.Document.Reservation_Booking_Number_MDB;
import com.example.ReservationTimings.Entity.Interceptor_Data_DB;
import com.example.ReservationTimings.Entity.Reservation_Backup_DB;
import com.example.ReservationTimings.Exception.DateFormatException;
import com.example.ReservationTimings.Exception.UnauthorisedException;
import com.example.ReservationTimings.Exception.UserNotFoundException;
import com.example.ReservationTimings.Repository.Interceptor_Repository;
import com.example.ReservationTimings.Repository.Reservation_Backup_Repository;
import com.example.ReservationTimings.Repository.Reservation_Repository;
import com.example.ReservationTimings.Repository.Reservation_booking_Repository;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@NoArgsConstructor
@Data
@AllArgsConstructor
public class CrudServices {


    private static final Logger log = LogManager.getLogger(CrudServices.class.getName());

    ///repository
    @Autowired
    private Reservation_booking_Repository booking_repository;
    @Autowired
    private Interceptor_Repository interceptor_repository;
    @Autowired
    private Reservation_Repository reservation_repository;
    @Autowired
    private Reservation_Backup_Repository reservation_backup_repository;
    @Autowired
    private KafkaProducer kafkaProducer;


    ////services
    @Autowired
    private Check_ConvertService cc_service;
    @Autowired
    private RemoteRequest remote_request_service;


    private Reservation_MDB reservation_mdb;
    private static Long id_check_sum = 0l;


    ///SAVE SERVICE
    public boolean save_reservation(Reservation_Post_DTO reservation_post_dto) {

        log.info("CRUD_SERVICE: Entered the AddReservation to database");

        System.out.println("inside the save_reservation");

        Integer no_of_booking = 1;
        if (cc_service.check_reservation_post_dto(reservation_post_dto)) {
            reservation_mdb = cc_service.DtoDocument_convert(reservation_post_dto);

            if (cc_service.verify_before_add(reservation_mdb)) {
                reservation_repository.save(reservation_mdb);
                log.info("CRUD_SERVICE: Successfully Exited the AddReservation to database");

                return true;
            } else//////if  second check get failed
                return false;

        } else//////if  initial check get failed
            return false;

    }


    //    //PAGINATION SERVICE
    public MappingJacksonValue findReservationsWithPaginationSorting_filtering_reservation(int offset, int pageSize, Optional<String> sort_field, Optional<Set<String>> filter_field) {


        log.info("CRUD_SERVICE: Entered into the PaginationAndSorting and filtering");
        Page<Reservation_MDB> locations = reservation_repository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(sort_field.orElse("id"))));
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(filter_field.orElse(cc_service.getAllReservationFields()));
        FilterProvider filters = new SimpleFilterProvider().addFilter("ModelReservation", filter);
        MappingJacksonValue mapping = new MappingJacksonValue(locations);
        mapping.setFilters(filters);
        log.info("CRUD_SERVICE: Exited into the PaginationAndSorting and filtering");
        return mapping;

    }


    ///////////Get by feild

    public MappingJacksonValue find_value(String value) {

        log.info("CRUD_SERVICE: Entered into the GET BY ID SERVICE");

        Long id_val = Long.parseLong(value);
        Optional<Reservation_MDB> reservation_mbd_optional = reservation_repository.findById(id_val);
        if (!(reservation_mbd_optional.isPresent())) {
            throw new UserNotFoundException("Cannot find the requested data for the given value: " + value);
        }
        reservation_mdb = reservation_mbd_optional.get();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(cc_service.getAllReservationFields());
        FilterProvider filters = new SimpleFilterProvider().addFilter("ModelReservation", filter);
        MappingJacksonValue mapping = new MappingJacksonValue(reservation_mdb);
        mapping.setFilters(filters);
        log.info("CRUD_SERVICE: SUCCESSFULLY  EXITED FROM GET BY ID SERVICE");
        return mapping;

    }


    ////////////////////////////update Service
    public void update_service_reservation(Reservation_Put_DTO reservation_put_dto) {
        log.info("CRUD_SERVICE: Entered into UPDATE SERVICE");

        //////////////1)check for valid date
        Pattern pd = Pattern.compile("[0-9]{4}(\\-)[01][0-9](\\-)[0123][0-9]");
        Matcher m = pd.matcher(reservation_put_dto.getReservation_date());
        if ((m.find())) {

            LocalDate today = LocalDate.now(ZoneId.of("America/Montreal"));
            if (!((today.toString().compareTo(m.group())) <= 0))
                throw new UserNotFoundException("Please check the Date  cannot  be past date ");
        } else
            throw new UserNotFoundException("Please check the Date format must be YYYY-MM-DD");


        //2)only if time format is valid then move further ahead
        if (cc_service.check_booking_time(reservation_put_dto.getBooking_time())) {

            ////3)check if it is  a valid id
            if (!(reservation_repository.findById(reservation_put_dto.getId()).isPresent()))
                throw new UserNotFoundException("Reservation with ID " + reservation_put_dto.getId() + " not present");

            Optional<Reservation_MDB> reservation_mdb_old_optional = reservation_repository.findById(reservation_put_dto.getId());


            /////4)check if it is previous values return it cannot be updated
            Reservation_MDB reservation_mdb_old_data = new Reservation_MDB();
            reservation_mdb_old_data.setRestaurantcode(reservation_mdb_old_optional.get().getRestaurantcode());
            reservation_mdb_old_data.setRestaurantname(reservation_mdb_old_optional.get().getRestaurantname());
            reservation_mdb_old_data.setReservationdate(reservation_mdb_old_optional.get().getReservationdate());
            reservation_mdb_old_data.setBookingtime(reservation_mdb_old_optional.get().getBookingtime());
            reservation_mdb_old_data.setId(reservation_mdb_old_optional.get().getId());


            if ((reservation_mdb_old_optional.get().getRestaurantcode().equals(reservation_put_dto.getRestaurant_code())) &&
                    (reservation_mdb_old_optional.get().getRestaurantname().equals(reservation_put_dto.getRestaurant_name())) &&
                    (reservation_mdb_old_optional.get().getReservationdate().equals(reservation_put_dto.getReservation_date())) &&
                    (reservation_mdb_old_optional.get().getBookingtime().equals(reservation_put_dto.getBooking_time()))) {
                throw new UnauthorisedException("since all details are same as previous no need for update");
            }


            ////5)check if the updated details slot got filled return it cannot be booked that updated slot
            if (cc_service.findbookingnumber(reservation_put_dto)) {

                Reservation_MDB reservation_mdb_update = new Reservation_MDB();
                reservation_mdb_update.setRestaurantcode(reservation_put_dto.getRestaurant_code());
                reservation_mdb_update.setRestaurantname(reservation_put_dto.getRestaurant_name());
                reservation_mdb_update.setReservationdate(reservation_put_dto.getReservation_date());
                reservation_mdb_update.setBookingtime(reservation_put_dto.getBooking_time());
                reservation_mdb_update.setId(reservation_mdb_old_optional.get().getId());
                ////////////////////////// creating the day based on date
                LocalDate today = LocalDate.now(ZoneId.of("America/Montreal"));
                Pattern p = Pattern.compile("\\-");
                String[] s = p.split(reservation_put_dto.getReservation_date());
                java.time.DayOfWeek dayOfWeek;
                try {
                    LocalDate localDate = LocalDate.of(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                    dayOfWeek = localDate.getDayOfWeek();
                } catch (DateTimeException e) {
                    throw new DateFormatException("PLease enter a valid date");
                }
                reservation_mdb_update.setReservationday(dayOfWeek.toString());
                if (cc_service.verify_before_add(reservation_mdb_update)) {
                    reservation_repository.save(reservation_mdb_update);
                }
                ////do decrement finally
                cc_service.decrement_booking(reservation_mdb_old_data);
            } else {
                log.info("CRUD_SERVICE: EXITED FROM  UPDATE SERVICE");
                throw new UnauthorisedException("All bookings are filled for the requested updates");
            }
        } else {
            log.debug("CRUD_SERVICE: Unsuccessfully EXITED  FROM  UPDATE SERVICE");
            throw new UnauthorisedException("Time format must be of 24 hrs please check back");
        }

    }


    //    /////////////Deleted  service
    public Boolean delete_service_reservation(Long id) {
        log.info("CRUD_SERVICE: Entered into DELETED SERVICE");
        if (!(reservation_repository.findById(id).isPresent()))
            throw new UserNotFoundException("Reservation with ID " + id + " not present");
        else {

            reservation_mdb = (reservation_repository.findById(id).get());
            cc_service.decrement_booking(reservation_mdb);
            reservation_repository.deleteById(id);
            log.debug("CRUD_SERVICE: Exited into DELETED SERVICE");
            return true;
        }


    }


/////////////////Rest api timing service

    public void add_interceptor_data(List data) {
        log.info("CRUD SERVICES:Entered into the add_interceptor data ");
        Interceptor_Data_DB interceptor_data = new Interceptor_Data_DB();
        interceptor_data.setTimemillisec((Long) data.get(0));
        interceptor_data.setUrl((String) data.get(2));
        interceptor_data.setId(0);
        interceptor_data.setDate(new Date());
        interceptor_data.setApiname("RESERVATION");
        interceptor_data.setServicename((String) data.get(1));
        interceptor_repository.save(interceptor_data);
        log.debug("CRUD SERVICES:Exited from the add_interceptor data");

    }


    public Page<Interceptor_Data_DB> api_timing(int offset, int pageSize, String name) {
        log.info("CRUD_SERVICE: Entered into the api timing sender");
        Page<Interceptor_Data_DB> data = interceptor_repository.findByApiname(name, PageRequest.of(offset, pageSize));
        log.debug("CRUD_SERVICE: Exited from the api timing sender");
        return data;
    }


    /////////////remote       location-------->menu-->reservation api request update
    public boolean remote_update(Remote_Put_Location_Menus_Reservation_DTO remote_put_location_menus_reservation_dto) {

        log.info("CRUD SERVICE:Entered into the remote_update");
        List<Reservation_MDB> list_reservations = reservation_repository.findByRestaurantcode(remote_put_location_menus_reservation_dto.getOld_restaurant_code());
        for (Reservation_MDB reservation_mdb : list_reservations) {
            reservation_mdb.setRestaurantcode(remote_put_location_menus_reservation_dto.getUpdated_restaurant_code());
            reservation_mdb.setRestaurantname(remote_put_location_menus_reservation_dto.getUpdated_restaurant_name());
            reservation_repository.save(reservation_mdb);

        }
        List<Reservation_Booking_Number_MDB> list_bookings = booking_repository.findAllByRestaurantcode(remote_put_location_menus_reservation_dto.getOld_restaurant_code());
        for (Reservation_Booking_Number_MDB booking : list_bookings) {
            booking.setRestaurantcode(remote_put_location_menus_reservation_dto.getUpdated_restaurant_code());
            Pattern p = Pattern.compile(",");
            String[] s = p.split(booking.getRestaurantstring());
            String updated_resstring = remote_put_location_menus_reservation_dto.getUpdated_restaurant_code() + remote_put_location_menus_reservation_dto.getUpdated_restaurant_name() + ","
                    + s[1];

            booking.setRestaurantstring(updated_resstring);
            booking_repository.save(booking);
        }

        return true;
    }


    ////remote  delete from menus  ---->reservations///////////////and aslo location---menus-reservation (one for all)
    public boolean remote_delete(String code) {
        log.info("inside the delete remote request call");

        reservation_repository.deleteAllByRestaurantcode(code);
        booking_repository.deleteAllByRestaurantcode(code);

        log.info("Existed from the delete remote request call");
        return true;
    }


    ////////remote menus-reservation update
    public boolean remote_update_menus_reservation(String code) {
        log.info("CRUD SERVICE:Inside the remote_put_menus_reservation service");
        List<Reservation_MDB> list_reservation_mdb = null;
        if ((list_reservation_mdb = reservation_repository.findByRestaurantcode(code)) == null)
            return true;
        else {
            for (Reservation_MDB reservation_mdb : list_reservation_mdb) {
                LocalDate today = LocalDate.now(ZoneId.of("America/Montreal"));
                if (((today.toString().compareTo(reservation_mdb.getReservationdate())) <= 0)) {
                    Integer remote_response = remote_request_service.remote_check_put_reservation_menus(cc_service.convert_mdb_remote_put_dto(reservation_mdb));
                    if (remote_response == 3 || remote_response == 4) {
                        cc_service.decrement_booking(reservation_mdb);
                        reservation_repository.deleteById(reservation_mdb.getId());
                    }
                }
            }
        }
        log.info("CRUD SERVICE:Exited from the remote_delete_menus_reservation service");
        return true;
    }

    //////////////////////back up

    public boolean add_backup(List<Reservation_Kafka_DTO> reservation_kafka_dto_list) {

        List<Reservation_Backup_DB> reservation_backup_db_list = cc_service.convertdtoentity(reservation_kafka_dto_list);
        reservation_backup_repository.saveAll(reservation_backup_db_list);
        return true;

    }

    /////////////////////////////////////SCHEDULER
    @Scheduled(cron = "* */25 * * * *")
    public void backup_automation() {
        log.info("cron job started");
        //creating current date
        List<Reservation_MDB> reservation_mdbs_list;
        if (id_check_sum == 0) {
            reservation_mdbs_list = reservation_repository.findAll();
            if (!((reservation_mdbs_list == null) || reservation_mdbs_list.isEmpty())) {
                id_check_sum = reservation_mdbs_list.get((reservation_mdbs_list.size()) - 1).getId();
                System.out.println(reservation_mdbs_list);
                List<Reservation_Kafka_DTO> reservation_kafka_dto_list = cc_service.convertentitytodto(reservation_mdbs_list);
                kafkaProducer.sendObject(reservation_kafka_dto_list);

            }
        } else {
            reservation_mdbs_list = reservation_repository.getbyid(id_check_sum);
            if (!((reservation_mdbs_list == null) || reservation_mdbs_list.isEmpty())) {
                System.out.println(reservation_mdbs_list);
                List<Reservation_Kafka_DTO> reservation_kafka_dto_list = cc_service.convertentitytodto(reservation_mdbs_list);
                kafkaProducer.sendObject(reservation_kafka_dto_list);

            }

        }

    }


}
