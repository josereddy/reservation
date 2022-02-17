package com.example.ReservationTimings.Services;

import com.example.ReservationTimings.DTO.Remote_Put_Reservation_Menus_DTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteRequest {


    private static final Logger log = LogManager.getLogger(RemoteRequest.class.getName());

    public Integer remote_check_put_reservation_menus(Remote_Put_Reservation_Menus_DTO reservation_data) {

        log.info("REMOTE REQUEST: ENTERED INTO THE CHECK_REMOTE_DATA");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://${MENUS_SERVER:localhost}:8082/menus/put/check_reservation_menus";
        HttpEntity<Remote_Put_Reservation_Menus_DTO> requestEntity = new HttpEntity<>(reservation_data,headers);
        ResponseEntity<Integer> response_entity=restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Integer.class);

        log.debug("REMOTE REQUEST: EXITED FROM THE CHECK_REMOTE_DATA");
        System.out.println("-------------------->"+ response_entity.getBody());
        return  response_entity.getBody();


    }
    private HttpHeaders getHeaders()
    {
        log.info("REMOTE REQUEST: Entered into the getHeaders");
        String  credentials ="jose:jose@";
        String encodeCredential = new String(Base64.encodeBase64(credentials.getBytes()));
        HttpHeaders header =new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.add("Authorization","Basic "+encodeCredential);
        log.debug("REMOTE REQUEST: EXITED FROM THE getHeaders");
        return header;
    }


}
