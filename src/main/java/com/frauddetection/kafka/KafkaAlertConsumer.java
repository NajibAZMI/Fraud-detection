package com.frauddetection.kafka;

import com.frauddetection.controller.FraudDetectionManager;
import com.frauddetection.model.Alert;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaAlertConsumer {

    private KafkaConsumer<String, String> consumer;
    private static final String ALERTS_TOPIC = "alerts";

    public KafkaAlertConsumer(String bootstrapServers, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new KafkaConsumer<>(props);
    }

    public void startListening(FraudDetectionManager manager) {
        new Thread(() -> {
            consumer.subscribe(Collections.singletonList(ALERTS_TOPIC));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    String alertJson = record.value();
                    Alert alert = Alert.fromJson(alertJson); // Deserialize JSON to Alert object
                    manager.addAlert(alert);
                    System.out.println("Alert received from Kafka: " + alert);
                }
            }
        }).start();
    }

    public void close() {

        consumer.close();
    }
}
