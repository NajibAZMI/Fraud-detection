package com.frauddetection.view;

import com.frauddetection.rules.GestionRule;
import com.frauddetection.util.RandomTransactionSource;
import com.frauddetection.controller.RuleManagementView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FraudDetectionUI extends Application {

    private static FraudDetectionUI instance;
    private MainView mainView;
    private GestionRule gestionRule;
    private Label dateLabel; // Label pour afficher la date actuelle
    private RandomTransactionSource rands = new RandomTransactionSource();

    public FraudDetectionUI() {
        instance = this;
        this.mainView = new MainView(); // Initialiser MainView
        this.gestionRule = new GestionRule();
    }

    public static FraudDetectionUI getInstance() {
        return instance;
    }

    public MainView getMainView() {
        return mainView; // Retourner l'instance de MainView
    }

    @Override
    public void start(Stage stage) {
        // Initialiser le label pour afficher la date actuelle
        dateLabel = new Label();
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");

        updateDate(); // Mettre à jour la date une première fois

        // Configurer un Timeline pour mettre à jour la date chaque seconde
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> updateDate())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Créer les boutons Start et Stop
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");

        // Configurer les styles des boutons
        startButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-size: 14px;");
        stopButton.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white; -fx-font-size: 14px;");

        // Action du bouton Start
        startButton.setOnAction(e -> {
            System.out.println("Start button clicked");
        });

        // Action du bouton Stop
        stopButton.setOnAction(e -> {
            System.out.println("Stop button clicked");
            rands.cancel();
        });

        // Créer une barre horizontale contenant le label et les boutons
        HBox topBar = new HBox(10, dateLabel, startButton, stopButton);
        topBar.setPrefHeight(80); // Fixer la hauteur de la barre
        topBar.setStyle("-fx-background-color: #007BFF; -fx-alignment: center-left; -fx-padding: 10px;");

        // Créer les vues
        StatisticsView statisticsView = new StatisticsView(mainView);
        RuleManagementView ruleManagementView = new RuleManagementView(gestionRule);

        // Créer le TabPane
        TabPane tabPane = new TabPane();
        Tab mainTab = new Tab("Transactions & Alerts", mainView.getView());
        Image mainTabImage = new Image(getClass().getResourceAsStream("/images/home.png"));
        ImageView mainTabImageView = new ImageView(mainTabImage);
        mainTabImageView.setFitHeight(40);
        mainTabImageView.setFitWidth(40);
        mainTab.setGraphic(mainTabImageView); // Ajouter l'image à l'onglet

        Tab statisticsTab = new Tab("Statistics", statisticsView.getView());
        Image statisticsTabImage = new Image(getClass().getResourceAsStream("/images/statistics.png"));
        ImageView statisticsTabImageView = new ImageView(statisticsTabImage);
        statisticsTabImageView.setFitHeight(40);
        statisticsTabImageView.setFitWidth(40);
        statisticsTab.setGraphic(statisticsTabImageView);

        Tab ruleManagementTab = new Tab("Rule Management", ruleManagementView.getView());
        Image ruleManagementTabImage = new Image(getClass().getResourceAsStream("/images/rules.png"));
        ImageView ruleManagementTabImageView = new ImageView(ruleManagementTabImage);
        ruleManagementTabImageView.setFitHeight(40);
        ruleManagementTabImageView.setFitWidth(40);
        ruleManagementTab.setGraphic(ruleManagementTabImageView);

        Tab historyTab = new Tab("Historical events", HistoricalView.getView());
        Image historyTabImage = new Image(getClass().getResourceAsStream("/images/historique.png"));
        ImageView historyTabImageView = new ImageView(historyTabImage);
        historyTabImageView.setFitHeight(40);
        historyTabImageView.setFitWidth(40);
        historyTab.setGraphic(historyTabImageView);

        tabPane.getTabs().addAll(mainTab, statisticsTab, ruleManagementTab, historyTab);

        // Appliquer un style minimaliste au TabPane avec hauteur ajustée
        tabPane.setStyle(
                "-fx-tab-min-width: 300px; " +
                        "-fx-tab-max-width: 1000px; " +
                        "-fx-background-color: #F9F9F9; " +
                        "-fx-border-color: #CCCCCC; " +
                        "-fx-tab-height: 200px;" // Hauteur des onglets
        );

        // Appliquer le style à chaque onglet
        for (Tab tab : tabPane.getTabs()) {
            // Appliquer un style de base
            tab.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-border-color: #CCCCCC;" +
                            "-fx-padding: 5px;" +
                            "-fx-font-size: 14px;"

            );

        }

        // Appliquer un style à l'onglet actif
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                newTab.setStyle(
                        "-fx-background-color: #007BFF;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-padding: 5px;"

                );
            }
            if (oldTab != null) {
                oldTab.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-border-color: #CCCCCC;" +
                                "-fx-padding: 5px;" +
                                "-fx-font-size: 14px;"

                );
            }
        });

        // Ajouter la barre horizontale et le TabPane à un VBox
        VBox root = new VBox(topBar, tabPane);

        // Configurer la scène
        Scene scene = new Scene(root, 1200, 1200);
        stage.setScene(scene);
        stage.setTitle("Fraud Detection System");
        stage.show();

        // Lancer la mise à jour des statistiques
        statisticsView.startStatisticsUpdater();
    }

    /**
     * Mettre à jour le label avec la date et l'heure actuelles.
     */
    private void updateDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dateLabel.setText("Current Date: " + LocalDateTime.now().format(formatter));
    }

    public static void main(String[] args) {
        launch(args); // Lancer l'interface JavaFX
    }
}
