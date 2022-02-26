package com.example.ReservationTimings.Config;


import com.example.ReservationTimings.Pojo.Generalinterceptor;
import com.example.ReservationTimings.Services.CrudServices;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class Web_mvc_config implements WebMvcConfigurer {

    final Logger log = LogManager.getLogger(Web_mvc_config.class.getName());


    public Web_mvc_config() {

        log.info("WEB_MVC_CONFIG: Inside the WEB_MVC_CONFIG");
    }

    @Autowired
    private CrudServices cr;


    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        log.info("WEB MVC CONFIG:Inside the addInterceptor method");
        registry.addInterceptor(new Generalinterceptor(cr));
        log.debug("WEB MVC CONFIG:Exited the addInterceptor method");

    }


    @Bean
    public OpenAPI openapi() {


        Contact email;
        return new OpenAPI()
                .info(new Info()
                        .title("TEXAS_HAMBURGER")
                        .version("1.0.0")
                        .description("Adds Location Details")
                        .contact(new io.swagger.v3.oas.models.info.Contact().name("Josephvijaykumarreddy Avula").email("jose737vijay@gmail.com"))


                );

    }


}
