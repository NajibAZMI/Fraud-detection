package com.frauddetection.controller;

import com.frauddetection.kafka.KafkaAlertConsumer;
import com.frauddetection.kafka.KafkaTransactionProducer;
import com.frauddetection.model.Alert;
import com.frauddetection.model.Transaction;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;

import java.util.*;

public class FraudDetectionManager {

    // Transaction and Alert lists
    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private final ObservableList<Alert> alertList = FXCollections.observableArrayList();
    private Random rand = new Random();
    // Kafka components
    private KafkaTransactionProducer kafkaProducer;
    private KafkaAlertConsumer kafkaConsumer;

    private Timeline transactionGenerator;

    public FraudDetectionManager() {
        // Initialize Kafka components
        this.kafkaProducer = new KafkaTransactionProducer("localhost:9092");
        this.kafkaConsumer = new KafkaAlertConsumer("localhost:9092", "alerts");

        // Start listening for alerts from Kafka
        kafkaConsumer.startListening(this);
    }

    // Add an alert to the alert list

//    public void addAlert(Object alert) {
//        alertList.add(alert);
//        System.out.println("Alert added: " + alert);  // Ajout d'un log pour vérifier
//    }


    // Add a transaction to the transaction list
    public void addTransaction(Transaction value) {
        transactionList.add(value);
        System.out.println("Transaction added: " + value);
    }

    // Initialize the transaction generator
    private void initializeTransactionGenerator() {
        transactionGenerator = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // Generate a new transaction
            Transaction transaction = generateTransaction();

            // Add to the locaal transaction list
            addTransaction(transaction);

            // Send the transaction to Kafka
            kafkaProducer.sendTransaction(transaction);

            System.out.println("Transaction generated and sent to Kafka: " + transaction);
        }));
        transactionGenerator.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
    }

    // Generate a random transaction
    Transaction generateTransaction() {


        String transactionId = "TX" + rand.nextInt(100000); // Exemple : TX12345, TX98765, ...
        String payerId = "Payer" + rand.nextInt(100); // Exemple : Payer1, Payer2, ...
        double amount = rand.nextDouble() * 5000; // Montant entre 0 et 5000
        String beneficiaryId = "Beneficiary" + rand.nextInt(100); // Exemple : Beneficiary1, Beneficiary2, ...
        String transactionType = rand.nextBoolean() ? "Debit" : "Credit"; // Choisir aléatoirement entre Debit ou Credit

        // Créer une nouvelle transaction avec le transactionId unique
        Transaction transaction = new Transaction(transactionId, payerId, System.currentTimeMillis(),amount, beneficiaryId, transactionType );



        return transaction;
    }

    // Close Kafka producer and consumer
    public void closeKafka() {
        kafkaProducer.close();
        kafkaConsumer.close();
    }


    // Getters for the transaction and alert lists
    public ObservableList<Transaction> getTransactionList() {
        return transactionList;
    }

    public ObservableList<Alert> getAlertList() {
        return alertList;  // Retourne la liste des alertes
    }

    // Start generating transactions
    public void startTransactionGenerator() {
        initializeTransactionGenerator();
        transactionGenerator.play();
    }

    // Stop generating transactions
    public void stopTransactionGenerator() {
        if (transactionGenerator != null) {
            transactionGenerator.stop();
        }
    }

    // Get alerts as Calendar objects
    public List<Calendar> getAlertsTimestampsAsCalendars() {
        List<Calendar> calendars = new ArrayList<>();
        for (Alert alert : alertList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(alert.getTimestamp());
            calendars.add(calendar);
        }
        return calendars;
    }

    public Object addAlert(Object alert) {
        alertList.add((Alert) alert);
        System.out.println("Alert added: " + alert);
        return alert;
    }

    public ObservableList<Alert> getAlertsList() {
        return alertList;
    }

    public void addAlert(Alert alert) {
        alertList.add(alert);
        System.out.println("Alert added: " + alert);
    }


}