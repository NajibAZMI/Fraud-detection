package com.frauddetection;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FraudDetectionApp {

    public static void main(String[] args) throws Exception {
        // Lancer l'interface utilisateur JavaFX dans un thread séparé
        new Thread(() -> FraudDetectionUI.main(args)).start();

        // Créer l'environnement d'exécution Flink
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Créer un flux de transactions aléatoires
        DataStream<Transaction> transactionStream = env.addSource(new RandomTransactionSource());

        // Créer un flux d'alertes basé sur les transactions suspectes
        DataStream<Alert> alerts = transactionStream
                .filter(transaction -> transaction.getAmount() > 4000) // Seuil de fraude
                .map(transaction -> new Alert(transaction.getTransactionId(), "Fraud detected!"));

        // Ajouter des sinks pour envoyer les transactions et alertes vers l'interface JavaFX
        transactionStream.addSink(new TransactionSink());
        alerts.addSink(new AlertSink());

        // Lancer l'application Flink
        env.execute("Fraud Detection Application");
    }
}