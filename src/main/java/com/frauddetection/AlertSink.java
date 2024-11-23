package com.frauddetection;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class AlertSink implements SinkFunction<Alert> {

    @Override
    public void invoke(Alert value, Context context) {
        // Ajouter l'alerte à l'interface utilisateur JavaFX
        FraudDetectionUI.addAlert(value);
    }
}

