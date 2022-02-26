package com.example.ReservationTimings.Services;


import com.example.ReservationTimings.Entity.User_Data_DB;
import com.example.ReservationTimings.Repository.User_Data_Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {
    private static final Logger log = LogManager.getLogger(UserService.class.getName());
    @Autowired
    private User_Data_Repository user_data_repository;
    private User_Data_DB user_data_db;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("USER SERVICE:Inside the load function to load the user name for security check");

        user_data_db = user_data_repository.findByUsername(username);
        UserDetailsPrinciple user_details_principal = new UserDetailsPrinciple(user_data_db);

        log.debug("USER SERVICE:Exiting the load function to load the user name for security check");

        return user_details_principal;
    }
}
