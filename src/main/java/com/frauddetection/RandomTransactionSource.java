package com.frauddetection;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

public class RandomTransactionSource implements SourceFunction<Transaction> {
    private boolean isRunning = true;
    private Random rand = new Random();

    @Override
    public void run(SourceContext<Transaction> ctx) throws Exception {
        while (isRunning) {
            // Générer une transaction aléatoire
            String transactionId = "T" + rand.nextInt(10000);
            double amount = rand.nextDouble() * 5000; // Montant entre 0 et 5000

            // Créer une nouvelle transaction
            Transaction transaction = new Transaction(transactionId, amount);
            System.out.println("Transaction générée : " + transaction);
            // Émettr e la transaction
            ctx.collect(transaction);

            // Pause de 1 seconde entre les transactions
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}

