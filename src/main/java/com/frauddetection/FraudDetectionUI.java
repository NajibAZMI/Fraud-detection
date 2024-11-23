package com.frauddetection;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FraudDetectionUI extends Application {

    // Listes pour stocker les transactions et les alertes
    private static ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private static ObservableList<Alert> alertsList = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        // TableView pour afficher les transactions
        TableView<Transaction> transactionTable = new TableView<>();
        TableColumn<Transaction, String> transactionIdCol = new TableColumn<>("Transaction ID");
        transactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Ajouter les colonnes à la table des transactions
        transactionTable.getColumns().addAll(transactionIdCol, amountCol);

        // TableView pour afficher les alertes
        TableView<Alert> alertsTable = new TableView<>();
        TableColumn<Alert, String> alertTransactionIdCol = new TableColumn<>("Transaction ID");
        alertTransactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        TableColumn<Alert, String> messageCol = new TableColumn<>("Message");
        messageCol.setCellValueFactory(new PropertyValueFactory<>("message"));

        // Ajouter les colonnes à la table des alertes
        alertsTable.getColumns().addAll(alertTransactionIdCol, messageCol);

        // Associer les listes aux tables
        transactionTable.setItems(transactionList);
        alertsTable.setItems(alertsList);

        // Mise en page
        VBox vbox = new VBox(transactionTable, alertsTable);

        // Créer une scène et l'afficher
        Scene scene = new Scene(vbox, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Fraud Detection System");
        stage.show();
    }

    // Méthodes statiques pour ajouter des transactions et des alertes
    public static void addTransaction(Transaction transaction) {
        transactionList.add(transaction);
    }

    public static void addAlert(Alert alert) {
        alertsList.add(alert);
    }

    // Méthode main pour lancer l'application JavaFX
    public static void main(String[] args) {
        launch(args);
    }
}