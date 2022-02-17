package com.example.ReservationTimings.Services;

import com.example.ReservationTimings.DTO.Reservation_Kafka_DTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaConsumer {
    private static final Logger log = LogManager.getLogger(KafkaConsumer.class.getName());
    @Autowired
    private CrudServices cr_service;

    @Qualifier("jsonKafkaListenerContainerFactory")
    @KafkaListener(containerFactory = "jsonKafkaListenerContainerFactory",topics = "${kafka.topic.json-demo.name1}", groupId = "${kafka.topic.json-demo.groupId1}")
    public void getMessage(List<Reservation_Kafka_DTO> reservation_kafka_dto_list) {

        log.info("Inside the kafka Listener ADDING ORDER");
        if (cr_service.add_backup(reservation_kafka_dto_list)) {
            log.info("All objects got successfully  Transmitted to Database");

        }
        else
            log.info("Data not saved Successfully");
    }
}

//    @Qualifier("jsonKafkaListenerContainerFactory_update")
//    @KafkaListener(containerFactory = "jsonKafkaListenerContainerFactory_update",topics = "${kafka.topic.json-demo.name2}", groupId = "${kafka.topic.json-demo.groupId2}")
//    public void getMessage_Update(Order_Update order) {
//
//        log.info("Inside the kafka Listener UPDATING ORDER");
//        service.updateorder_status(order);
//        log.info("All objects got successfully  Transmitted to Database");
//    }

