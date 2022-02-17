package com.example.ReservationTimings.Config;


import com.example.ReservationTimings.DTO.Reservation_Kafka_DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;


import java.util.HashMap;
import java.util.Map;


@Configuration
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaConsumerConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;
    @Value("${spring.kafka.consumer.client-id}")
    private String clientId;
    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeserializer;
    @Value("${spring.kafka.consumer.isolation-level}")
    private String isolationLevel;
    @Value("${spring.kafka.consumer.max-poll-records}")
    private String maxPollRecords;
    @Value("${spring.kafka.consumer.heartbeat-interval}")
    private String heartbeatIntervalMs;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;



    // placing order
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Reservation_Kafka_DTO> jsonKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Reservation_Kafka_DTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(jsonConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.getContainerProperties().setSyncCommits(true);
        return factory;

    }



    @Bean
    public DefaultKafkaConsumerFactory<String,Reservation_Kafka_DTO> jsonConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatIntervalMs);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolationLevel);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                props, new StringDeserializer(), new JsonDeserializer<>(Reservation_Kafka_DTO.class)
        );


    }










    //
//
//    //update order
//    @Bean
//    public DefaultKafkaConsumerFactory<String, K> jsonConsumerFactory_update() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatIntervalMs);
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
//        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolationLevel);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
//
//        return new DefaultKafkaConsumerFactory<>(
//                props, new StringDeserializer(), new JsonDeserializer<>(Order_Update.class)
//        );
//
//
//    }
//
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, Order_Update> jsonKafkaListenerContainerFactory_update() {
//        ConcurrentKafkaListenerContainerFactory<String, Order_Update> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(jsonConsumerFactory_update());
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
//        factory.getContainerProperties().setSyncCommits(true);
//        return factory;
//
//    }

}

