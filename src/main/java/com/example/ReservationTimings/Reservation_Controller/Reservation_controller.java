package com.example.ReservationTimings.Reservation_Controller;

import com.example.ReservationTimings.DTO.Remote_Put_Location_Menus_Reservation_DTO;
import com.example.ReservationTimings.DTO.Reservation_Post_DTO;
import com.example.ReservationTimings.DTO.Reservation_Put_DTO;
import com.example.ReservationTimings.Document.Reservation_MDB;
import com.example.ReservationTimings.Entity.Interceptor_Data_DB;
import com.example.ReservationTimings.Entity.User_Data_DB;
import com.example.ReservationTimings.Exception.UserDataIncorrectFormatException;
import com.example.ReservationTimings.Services.Check_ConvertService;
import com.example.ReservationTimings.Services.CrudServices;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/reservation")
@Tag(name = "RESERVATION", description = "Manages ALL Reservation related data about restaurant")
@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Operation Success",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "400", description = "CHECK FOR SCHEMA BEFORE SENDING",
                content = @Content(schema = @Schema(implementation = Reservation_MDB.class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "401", description = "Login credentials mismatch",
                content = @Content(schema = @Schema(implementation = User_Data_DB.class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "403", description = "Login USER LEVEL mismatch",
                content = @Content(schema = @Schema(implementation = User_Data_DB.class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "Mismatch with RULES While entering details",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
})
public class Reservation_controller {

    private static final Logger log = LogManager.getLogger(Reservation_controller.class.getName());
    @Autowired
    private CrudServices cr_service;
    @Autowired
    private Check_ConvertService cc_service;


    ///Add Reservation  API
    @Operation(summary = "[POST Reservation DATA TO MONGO DB]", description = "New record wil be Added into the database Reservation")
    @SecurityRequirement(name = "check")
    @PostMapping("/post/add_reservation")
    public String add_reservation(@RequestBody Reservation_Post_DTO reservation_post_dto) {

        log.info("REST CALL: ENTERED ADD MENU DATA ");
        if (cr_service.save_reservation(reservation_post_dto)) {
            log.debug("REST CALL: ADD MENU DATA Successfully EXITED ");
            return "Reservation Data Successfully Added to DataBase";
        } else
            throw new UserDataIncorrectFormatException("Location_Data is incorrect");


    }


    ///Pagination and Sorting And FILTERING  API
    @Operation(summary = "[GET PAGINATED Reservation DATA with sorted by and filtering]", description = "MENU data from Location table in pagination with sorting,filtering is obtained ")
    @Parameter(name = "offset", example = "0", required = true, description = "PAGE OFFSET", in = ParameterIn.PATH)
    @Parameter(name = "pageSize", example = "5", required = true, description = "PAGE SIZE", in = ParameterIn.PATH)
    @Parameter(name = "sort_field", example = "restaurantcode", required = false, description = "SORTING FIELD/   default=id/    ex:id,restaurantcode,restaurantname.....", in = ParameterIn.QUERY)
    @Parameter(name = "filter_fields", required = false, description = "Filtering FIELDS/    default=all fields available/     ex:id,restaurantcode,restaurantname.....", in = ParameterIn.QUERY)
    @SecurityRequirement(name = "check")
    @GetMapping("/get/pagination_sort_filtering/{offset}/{pageSize}")
    private MappingJacksonValue getReservationsWithPagination_SortAndFiltering_reservation(@PathVariable("offset") int offset, @PathVariable("pageSize") int pageSize,
                                                                                           @RequestParam("sort_field") Optional<String> sort_field, @RequestParam Optional<Set<String>> filter_fields) {
        log.info("REST CALL: Entered Pagination and Sorting and filtering");
        MappingJacksonValue value = cr_service.findReservationsWithPaginationSorting_filtering_reservation(offset, pageSize, sort_field, filter_fields);
        log.debug("REST CALL:Exited Pagination AND Sorting AND Filtering");
        return value;

    }


    //////Search by field API
    @SecurityRequirement(name = "check")
    @Operation(summary = "[GET the Reservation record by field and value]", description = "Get the single record for given field and value ")
    @Parameter(name = "id_value", example = "1", required = true, description = "Field Value", in = ParameterIn.PATH)
    @GetMapping("/get/search_reservation/{id_value}")
    public MappingJacksonValue search_value(@PathVariable("id_value") String value) {
        log.info("REST API: Entered SEARCH Service");
        MappingJacksonValue json_reservation = cr_service.find_value(value);
        log.debug("REST API: Exited Search Service");
        return json_reservation;
    }


    /////////   Update location API
    @SecurityRequirement(name = "check")
    @Operation(summary = "[Update the record based on id]", description = "A new value for the particular value is appeared in Database ")
    @PutMapping("/put/update_reservation")
    public String update_location(@RequestBody Reservation_Put_DTO reservation_put_dto) {
        log.info("REST API: Entered  Update Service");
        cr_service.update_service_reservation(reservation_put_dto);
        return "Data updated Successfully";
    }


    ///////////////////////   Delete Location Api
    @SecurityRequirement(name = "check")
    @Operation(summary = "[Delete the Reservation record based on id]", description = "No more data available in the database with the chosen id")
    @Parameter(name = "id", example = "1", required = true, description = "Id VALUE", in = ParameterIn.PATH)
    @DeleteMapping("/delete/delete_reservation/{id}")
    public String delete_location(@PathVariable("id") Long id) {
        log.info("REST API: Entered Deleted Location ");
        if (cr_service.delete_service_reservation(id)) {
            log.debug("REST API: Exited Deleted Location");
            return "Record with id -->" + id + " deleted";
        } else
            return "Record with id--->" + id + " not deleted";

    }


/////////////////////// Interceptor API TIMINGS

    @GetMapping("/get/api_timing/{offset}/{pageSize}/{Microservice}")
    @SecurityRequirement(name = "check")
    @Operation(summary = "[GET The All Reservation Api timings]", description = "Retrieve data in pagination format from db")
    @Parameter(name = "offset", example = "0", required = true, description = " PAGE OFFSET", in = ParameterIn.PATH)
    @Parameter(name = "pageSize", example = "5", required = true, description = "PAGE SIZE", in = ParameterIn.PATH)
    @Parameter(name = "Microservice", example = "RESERVATION", required = true, description = "Microservice name", in = ParameterIn.PATH)

    public Page<Interceptor_Data_DB> get_api_timing(@PathVariable("offset") int offset, @PathVariable("pageSize") int pageSize, @PathVariable("Microservice") String name) {
        log.info("REST API:Entered into Api TIME sender");
        Page<Interceptor_Data_DB> data = cr_service.api_timing(offset, pageSize, name);
        log.debug("REST API:Exited FROM API TIME SENDER");
        return data;
    }


/////////////  Remote service API

    /////////////////////remote_put_location_menus_reservation
    @Hidden
    @SecurityRequirement(name = "check")
    @PutMapping("/put/remote_update_location_menus_reservation")
    public boolean remote_update_location_menus_reservation(@RequestBody Remote_Put_Location_Menus_Reservation_DTO remote_put_location_menus_reservation_dto) {

        log.info("REST API:Inside the check Rest Template Service");
        return cr_service.remote_update(remote_put_location_menus_reservation_dto);
    }


    ////////////////////////////////////remote_delete_location_menus_reservation
    @Hidden
    @SecurityRequirement(name = "check")
    @DeleteMapping("/delete/remote_delete_location_menus_reservation/{code}")
    public boolean remote_delete_location_menus_reservation(@PathVariable("code") String code) {

        log.info("REST API:Inside the check Rest Template Service");
        return cr_service.remote_delete(code);
    }


    ////////////////////////////////////////remote_put_menus_location
    @Hidden
    @SecurityRequirement(name = "check")
    @PutMapping("/put/remote_update_menus_reservation/{code}")
    public boolean remote_update_menus_reservation(@PathVariable String code) {

        log.info("REST API:Inside the Update menus_reservation api");
        return cr_service.remote_update_menus_reservation(code);
    }


    @Hidden
    @SecurityRequirement(name = "check")
    @DeleteMapping("/delete/remote_delete_menus_reservation/{code}")
    public boolean remote_delete_menus_reservation(@PathVariable String code) {

        log.info("REST API:Inside the delete menus_reservation Api");
        return cr_service.remote_delete(code);
    }


}


