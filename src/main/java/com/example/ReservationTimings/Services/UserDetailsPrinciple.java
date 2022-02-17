package com.example.ReservationTimings.Services;


import com.example.ReservationTimings.Entity.User_Data_DB;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Data
//@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsPrinciple implements UserDetails {

    private static final Logger log = LogManager.getLogger(UserDetailsPrinciple.class.getName());

    private User_Data_DB user_data_db;
    UserDetailsPrinciple(User_Data_DB user_data_db)
    {

        log.info("UserDETAILPrinciple: Entered into the USER DETAILS principal");
        this.user_data_db = user_data_db;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_"+user_data_db.getUserroll()));
        System.out.println(roles);
        return roles;
    }

    @Override
    public String getPassword() {
        return user_data_db.getUserpassword();

    }

    @Override
    public String getUsername() {
        return user_data_db.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
