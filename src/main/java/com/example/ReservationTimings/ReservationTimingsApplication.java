package com.example.ReservationTimings;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@SecurityScheme(name="check",scheme="basic",type= SecuritySchemeType.HTTP,in= SecuritySchemeIn.HEADER)
public class ReservationTimingsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReservationTimingsApplication.class, args);
	}

}
