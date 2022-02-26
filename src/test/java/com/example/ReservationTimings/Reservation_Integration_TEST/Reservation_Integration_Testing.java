package com.example.ReservationTimings.Reservation_Integration_TEST;


import com.example.ReservationTimings.DTO.Remote_Put_Reservation_Menus_DTO;
import com.example.ReservationTimings.DTO.Reservation_Post_DTO;
import com.example.ReservationTimings.DTO.Reservation_Put_DTO;
import com.example.ReservationTimings.Document.Reservation_MDB;
import com.example.ReservationTimings.Entity.User_Data_DB;
import com.example.ReservationTimings.Repository.Interceptor_Repository;
import com.example.ReservationTimings.Repository.Reservation_Repository;
import com.example.ReservationTimings.Repository.Reservation_booking_Repository;
import com.example.ReservationTimings.Repository.User_Data_Repository;
import com.example.ReservationTimings.Services.CrudServices;
import com.example.ReservationTimings.Services.RemoteRequest;
import com.example.ReservationTimings.Services.SequenceGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Reservation_Integration_Testing {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    ///repositories
    @Autowired
    private Reservation_Repository reservation_repository;
    @Autowired
    private Reservation_booking_Repository reservation_booking_repository;
    @Autowired
    private User_Data_Repository user_data_repository;
    @Autowired
    private Interceptor_Repository interceptor_repository;
    @Autowired
    private SequenceGeneratorService seq_service;

///////services

    @MockBean
    private RemoteRequest remoteRequest;
    @Autowired
    private CrudServices crudServices;


    private User_Data_DB user_data_db = new User_Data_DB();
    private Reservation_Post_DTO reservation_post_dto = new Reservation_Post_DTO();
    private Reservation_MDB reservation_mdb = new Reservation_MDB();


    @BeforeEach
    public void setup() {
        reservation_booking_repository.deleteAll();
        reservation_repository.deleteAll();
        user_data_repository.deleteAll();
        //user_details
        user_data_db.setUserpassword((new BCryptPasswordEncoder().encode("jose@")));
        user_data_db.setUsername("jose");
        user_data_db.setUserroll("ADMIN");
        user_data_repository.save(user_data_db);

        //////creating the post dto
        reservation_post_dto.setRestaurant_code("usa-test-1000");
        reservation_post_dto.setRestaurant_name("usa-test-chicken");
        reservation_post_dto.setBooking_time("10:00-12:00");
        LocalDate today = LocalDate.now(ZoneId.of("America/Montreal"));
        reservation_post_dto.setReservation_date(today.toString());

        ////////DB object
        reservation_mdb.setRestaurantcode("usa-test-1000");
        reservation_mdb.setRestaurantname("usa-test-chicken");
        reservation_mdb.setBookingtime("10:00-12:00");
        reservation_mdb.setReservationdate(today.toString());
        reservation_mdb.setReservationday("Mondaydd");
        reservation_mdb.setId(seq_service.generateSequence(Reservation_MDB.SEQUENCE_NAME));
    }


    /////////Reservation_post

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Post_Location_TEST:1")
    @Test
    @Order(1)
    public void Reservation_Post_Positive() throws Exception {

        // given - precondition or setup
        String Expected = "Reservation Data Successfully Added to DataBase";

        given(remoteRequest.remote_check_put_reservation_menus(any(Remote_Put_Reservation_Menus_DTO.class))).willReturn(0);

//         when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post(URI.create("/reservation/post/add_reservation"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation_post_dto)));

        // then - verify the result or output using assert statements
        response.andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(Expected));

    }


    ///////////////// get All
    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_Reservation TEST:1 default")
    @Test
    @Order(2)
    public void Reservation_get_BY_PAGINATION_TEST1() throws Exception {

        // given - precondition or setup
        List<Reservation_MDB> list_menus_mdb = new ArrayList();
        list_menus_mdb.add(reservation_mdb);
        Reservation_MDB reservation_mdb1 = new Reservation_MDB();
        reservation_mdb1.setRestaurantcode("usa-test-1000");
        reservation_mdb1.setRestaurantname("usa-test-chicken");
        reservation_mdb1.setBookingtime("10:00-12:00");
        reservation_mdb1.setReservationdate("02/02/2022");
        reservation_mdb1.setReservationday("Mondaydd");
        reservation_mdb1.setId(seq_service.generateSequence(Reservation_MDB.SEQUENCE_NAME));
        reservation_mdb1.setId(seq_service.generateSequence(Reservation_MDB.SEQUENCE_NAME));
        list_menus_mdb.add(reservation_mdb1);
        reservation_repository.saveAll(list_menus_mdb);
        Integer size = 2, offset = 0, pageSize = 2;

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/reservation/get/pagination_sort_filtering/{offset}/{pageSize}", offset, pageSize));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.size", CoreMatchers.is(size)))
                .andExpect(jsonPath("$.content[0].restaurantcode", CoreMatchers.is("usa-test-1000")))
                .andExpect(jsonPath("$.content[1].bookingtime", CoreMatchers.is("10:00-12:00")));
    }

//

    ////////////////////get by field

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_Reservation_By_ID TEST:1 Default")
    @Test
    @Order(3)
    public void Reservation_get_BY_Field_TEST1() throws Exception {

        // given - precondition or setup
        reservation_repository.save(reservation_mdb);
        String id_value = String.valueOf(reservation_mdb.getId());

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/reservation/get/search_reservation/{id_value}", id_value));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantcode", CoreMatchers.is("usa-test-1000")));
    }

//    ///////////////update

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("PUT_RESERVATION_TEST:1 default")
    @Test
    @Order(4)
    public void Reservation_update_test() throws Exception {

        // given - precondition or setup
        given(remoteRequest.remote_check_put_reservation_menus(any(Remote_Put_Reservation_Menus_DTO.class))).willReturn(0);
        crudServices.save_reservation(reservation_post_dto);
        List<Reservation_MDB> list_reservation_mdb = reservation_repository.findByRestaurantcode(reservation_post_dto.getRestaurant_code());

        Reservation_Put_DTO reservation_put_dto = new Reservation_Put_DTO();
        reservation_put_dto.setId(list_reservation_mdb.get(0).getId());
        reservation_put_dto.setReservation_date(reservation_post_dto.getReservation_date());
        reservation_put_dto.setRestaurant_code(reservation_post_dto.getRestaurant_code());
        reservation_put_dto.setRestaurant_name("helloworld");
        reservation_put_dto.setBooking_time("12:00-12:30");

        String expectedresult = "Data updated Successfully";

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/reservation/put/update_reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation_put_dto)));
        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(expectedresult));
    }


////    //////////////delete

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("DELETE_Reservation_RECORD_TEST:1 default")
    @Test
    @Order(5)
    public void Reservation_delete_test() throws Exception {

        // given - precondition or setup
        given(remoteRequest.remote_check_put_reservation_menus(any(Remote_Put_Reservation_Menus_DTO.class))).willReturn(0);
        crudServices.save_reservation(reservation_post_dto);
        List<Reservation_MDB> list_reservation_mdb = reservation_repository.findByRestaurantcode(reservation_post_dto.getRestaurant_code());
        Long id = list_reservation_mdb.get(0).getId();


        String expectedresult = "Record with id -->" + id + " deleted";


        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(delete("/reservation/delete/delete_reservation/{id}", id));


        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(expectedresult));
    }

}
