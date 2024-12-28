package com.frauddetection.kafka;

import com.frauddetection.model.Transaction;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaTransactionProducer {

    private KafkaProducer<String, String> producer;
    private static final String TRANSACTION_TOPIC = "transactions";

    public KafkaTransactionProducer(String bootstrapServers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.producer = new KafkaProducer<>(props);
    }

    public void sendTransaction(Transaction transaction) {
        String transactionJson = transaction.toString(); // Convert to JSON if needed
        producer.send(new ProducerRecord<>(TRANSACTION_TOPIC, transaction.getTransactionId(), transactionJson));
        System.out.println("Transaction sent to Kafka: " + transactionJson);
    }

    public void close() {
        producer.close();
    }
}