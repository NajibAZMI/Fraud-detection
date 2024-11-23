package com.frauddetection;

public class Alert {
    private String transactionId;
    private String message;

    public Alert(String transactionId, String message) {
        this.transactionId = transactionId;
        this.message = message;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Alert{transactionId=" + transactionId + ", message=" + message + "}";
    }
}

