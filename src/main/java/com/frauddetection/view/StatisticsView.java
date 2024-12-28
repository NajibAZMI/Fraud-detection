package com.frauddetection.view;

import com.frauddetection.model.Alert;
import com.frauddetection.model.RuleAlertStats;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsView {

    private XYChart.Series<Number, Number> transactionsSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> alertsSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> fraudPercentageSeries = new XYChart.Series<>();
    private int currentTime = 0;

    private static final int INTERVAL = 5; // Intervalle de 5 minutes
    private MainView mainView;
    private TableView<RuleAlertStats> ruleAlertTable = new TableView<>();
    private PieChart ruleAlertPieChart = new PieChart();

    public StatisticsView(MainView mainView) {
        this.mainView = mainView;
        createRuleAlertTable(); // Initialise la TableView
        startPieChartUpdater();
    }

    public VBox getView() {
        // Créer les trois onglets
        TabPane tabPane = new TabPane();

        // Transactions & Alerts Tab
        Tab transactionsTab = new Tab("Transactions & Alerts", createTransactionsAlertsChart());
        Image transactionsTabImage = new Image(getClass().getResourceAsStream("/images/home.png"));
        ImageView transactionsTabImageView = new ImageView(transactionsTabImage);
        transactionsTabImageView.setFitHeight(40);
        transactionsTabImageView.setFitWidth(40);
        transactionsTab.setGraphic(transactionsTabImageView);

        // Fraud Percentage Tab
        Tab fraudPercentageTab = new Tab("Fraud Percentage", createFraudPercentageChart());
        Image fraudPercentageTabImage = new Image(getClass().getResourceAsStream("/images/statistics.png"));
        ImageView fraudPercentageTabImageView = new ImageView(fraudPercentageTabImage);
        fraudPercentageTabImageView.setFitHeight(40);
        fraudPercentageTabImageView.setFitWidth(40);
        fraudPercentageTab.setGraphic(fraudPercentageTabImageView);

        // Rule Alert Distribution Tab
        Tab ruleAlertTab = new Tab("Rule Alert Distribution", createRuleAlertPieChart());
        Image ruleAlertTabImage = new Image(getClass().getResourceAsStream("/images/rules.png"));
        ImageView ruleAlertTabImageView = new ImageView(ruleAlertTabImage);
        ruleAlertTabImageView.setFitHeight(40);
        ruleAlertTabImageView.setFitWidth(40);
        ruleAlertTab.setGraphic(ruleAlertTabImageView);

        // Rule Alert Table Tab
        Tab ruleAlertTableTab = new Tab("Rule Alert Table", ruleAlertTable);
        Image ruleAlertTableTabImage = new Image(getClass().getResourceAsStream("/images/historique.png"));
        ImageView ruleAlertTableTabImageView = new ImageView(ruleAlertTableTabImage);
        ruleAlertTableTabImageView.setFitHeight(40);
        ruleAlertTableTabImageView.setFitWidth(40);
        ruleAlertTableTab.setGraphic(ruleAlertTableTabImageView);

        // Empêcher la fermeture des onglets
        transactionsTab.setClosable(false);
        fraudPercentageTab.setClosable(false);
        ruleAlertTab.setClosable(false);
        ruleAlertTableTab.setClosable(false);

        // Ajouter les onglets au TabPane
        tabPane.getTabs().addAll(transactionsTab, fraudPercentageTab, ruleAlertTab, ruleAlertTableTab);

        // Appliquer un style minimaliste au TabPane avec hauteur ajustée
        tabPane.setStyle(
                "-fx-tab-min-width: 200px; " +
                        "-fx-tab-max-width: 1000px; " +
                        "-fx-background-color: #F9F9F9; " +
                        "-fx-border-color: #CCCCCC; " +
                        "-fx-tab-height: 200px;" // Hauteur des onglets
        );

        // Appliquer le style à chaque onglet
        for (Tab tab : tabPane.getTabs()) {
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

        // Encapsuler le TabPane dans un VBox
        VBox vbox = new VBox(tabPane);
        return vbox;
    }

    private LineChart<Number, Number> createTransactionsAlertsChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (minutes)");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(60);
        xAxis.setTickUnit(1);

        NumberAxis yAxisCounts = new NumberAxis();
        yAxisCounts.setLabel("Transactions / Alerts Count");

        LineChart<Number, Number> transactionsAlertsChart = new LineChart<>(xAxis, yAxisCounts);
        transactionsAlertsChart.setTitle("Transactions & Alerts Over Time");
        transactionsSeries.setName("Transactions");
        alertsSeries.setName("Alerts");
        transactionsAlertsChart.getData().addAll(transactionsSeries, alertsSeries);

        return transactionsAlertsChart;
    }

    private LineChart<Number, Number> createFraudPercentageChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (minutes)");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(60);
        xAxis.setTickUnit(1);

        NumberAxis yAxisPercentage = new NumberAxis(0, 100, 10);
        yAxisPercentage.setLabel("Fraud Percentage (%)");

        LineChart<Number, Number> fraudPercentageChart = new LineChart<>(xAxis, yAxisPercentage);
        fraudPercentageChart.setTitle("Fraud Percentage Over Time");
        fraudPercentageSeries.setName("Fraud Percentage");
        fraudPercentageChart.getData().add(fraudPercentageSeries);

        return fraudPercentageChart;
    }

    private PieChart createRuleAlertPieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        ObservableList<RuleAlertStats> ruleAlertData = ruleAlertTable.getItems();  // Récupérer les éléments de la table

        ruleAlertData.forEach(ruleAlert -> {
            String ruleId = ruleAlert.getRuleId();
            int count = ruleAlert.getAlertCount();
            pieChartData.add(new PieChart.Data(ruleId + " (" + count + ")", count));
        });

        ruleAlertPieChart.setData(pieChartData);
        ruleAlertPieChart.setTitle("Alert Distribution by Rule");
        ruleAlertPieChart.setStyle("-fx-pref-width: 600px; -fx-pref-height: 600px;");
        return ruleAlertPieChart;
    }

    private void createRuleAlertTable() {
        // Colonne Rule ID
        TableColumn<RuleAlertStats, String> ruleIdColumn = new TableColumn<>("Rule ID");
        ruleIdColumn.setCellValueFactory(new PropertyValueFactory<>("ruleId"));
        ruleIdColumn.setPrefWidth(300);

        // Colonne Alert Count
        TableColumn<RuleAlertStats, Integer> alertCountColumn = new TableColumn<>("Alert Count");
        alertCountColumn.setCellValueFactory(new PropertyValueFactory<>("alertCount"));
        alertCountColumn.setPrefWidth(300);

        // Colonne Description
        TableColumn<RuleAlertStats, String> alertDescColumn = new TableColumn<>("Description");
        alertDescColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        alertDescColumn.setPrefWidth(650);

        // Ajouter les colonnes à la table
        ruleAlertTable.getColumns().addAll(ruleIdColumn, alertCountColumn, alertDescColumn);



        // Style des colonnes
        ruleAlertTable.getColumns().forEach(column -> {
            column.setStyle("-fx-alignment: CENTER;"); // Alignement centré pour tout
        });

        // Style des lignes
        ruleAlertTable.setRowFactory(tv -> {
            TableRow<RuleAlertStats> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    // Exemple : Style basé sur le nombre d'alertes
                    if (newItem.getAlertCount() > 90) {
                        row.setStyle("-fx-background-color: #fe6d5f; -fx-text-fill: black;"); // Rouge clair
                    } else if (newItem.getAlertCount() > 75) {
                        row.setStyle("-fx-background-color: #fe8f5f; -fx-text-fill: black;"); // Jaune clair
                    }else if (newItem.getAlertCount() > 50) {
                        row.setStyle("-fx-background-color: #feac5f; -fx-text-fill: black;"); // Jaune clair
                    }else if (newItem.getAlertCount() > 35) {
                        row.setStyle("-fx-background-color: #feed5f; -fx-text-fill: black;"); // Jaune clair
                    }else if (newItem.getAlertCount() > 20) {
                        row.setStyle("-fx-background-color: #d0fe5f; -fx-text-fill: black;"); // Jaune clair
                    }else if (newItem.getAlertCount() > 5) {
                        row.setStyle("-fx-background-color: #baffcb; -fx-text-fill: black;"); // Jaune clair
                    }
                    else {
                        row.setStyle("-fx-background-color: #afffa8; -fx-text-fill: black;"); // Vert clair
                    }
                }
            });
            return row;
        });

        // Espacement des lignes (optionnel)
        ruleAlertTable.setFixedCellSize(35);
        ruleAlertTable.setStyle("-fx-padding: 5; -fx-background-color: #f8f9fa; -fx-border-color: #ddd;");

        // Bordures et marges pour la table
        ruleAlertTable.setStyle("-fx-border-color: #ccc; -fx-border-width: 2px;");

        ruleAlertTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    ruleAlertTable.lookupAll(".column-header").forEach(header -> {
                        header.setStyle("-fx-background-color: #a8d6ff; -fx-text-fill: white;");
                    });
                });
            }
        });
    }


    public void startStatisticsUpdater() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(0.1), event -> {
            updateChart();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateChart() {
        currentTime++;

        transactionsSeries.getData().add(new XYChart.Data<>(currentTime, mainView.getTransactionList().size()));
        alertsSeries.getData().add(new XYChart.Data<>(currentTime, mainView.getAlertsList().size()));

        double fraudPercentage = mainView.getTransactionList().isEmpty() ? 0 :
                ((double) mainView.getAlertsList().size() / mainView.getTransactionList().size()) * 100;
        fraudPercentageSeries.getData().add(new XYChart.Data<>(currentTime, fraudPercentage));
    }

    private void updateRuleAlertTable() {
        Map<String, List<com.frauddetection.model.Alert>> groupedAlerts = mainView.getAlertsList().stream()
                .collect(Collectors.groupingBy(Alert::getAlertRuleID));

        ObservableList<RuleAlertStats> ruleAlertData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        groupedAlerts.forEach((ruleId, alerts) -> {
            int alertCount = alerts.size();
            String description = alerts.get(alerts.size() - 1).getAlertDetails();
            ruleAlertData.add(new RuleAlertStats(ruleId, alertCount, description));
            pieChartData.add(new PieChart.Data(ruleId + " (" + alertCount + ")", alertCount));
        });

        ruleAlertTable.setItems(ruleAlertData);
        ruleAlertPieChart.setData(pieChartData);

        ruleAlertPieChart.getData().forEach(data -> {
            Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue());
            Tooltip.install(data.getNode(), tooltip);
        });
    }

    public void startPieChartUpdater() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(1), event -> {
            updateRuleAlertTable();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
