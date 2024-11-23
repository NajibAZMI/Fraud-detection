package com.frauddetection;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class TransactionSink implements SinkFunction<Transaction> {

    @Override
    public void invoke(Transaction value, Context context) {
        // Ajouter la transaction à l'interface utilisateur JavaFX
        FraudDetectionUI.addTransaction(value);
    }
}

