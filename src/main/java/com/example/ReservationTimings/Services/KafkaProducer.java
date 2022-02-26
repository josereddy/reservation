package com.example.ReservationTimings.Services;


import com.example.ReservationTimings.DTO.Reservation_Kafka_DTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.UUID;

@Service
public class KafkaProducer {
    private static final Logger log = LogManager.getLogger(KafkaProducer.class.getName());
    @Value("${kafka.topic.json-demo.name1}")
    private String JSON_TOPIC;


    @Autowired
    private KafkaTemplate<String, Reservation_Kafka_DTO> kafkaTemplate;


    public void sendObject(List<Reservation_Kafka_DTO> reservation_kafka_dto_list) {
        log.info("Inside the Kafka Producer Sending Order");

        for (Reservation_Kafka_DTO reservation_kafka_dto : reservation_kafka_dto_list) {
            kafkaTemplate.executeInTransaction(t -> {

                ListenableFuture<SendResult<String, Reservation_Kafka_DTO>> future;
                future = t.send(JSON_TOPIC, "" + UUID.randomUUID(), reservation_kafka_dto);
                future.addCallback(new ListenableFutureCallback<SendResult<String, Reservation_Kafka_DTO>>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        log.info("failure not Published on TOPIC");
                    }

                    @Override
                    public void onSuccess(SendResult<String, Reservation_Kafka_DTO> result) {

                        log.info("Successfully Published to TOPIC");
                    }
                });
                log.info("Data got Transmitted Successfully");
                return true;
            });
        }
    }


}