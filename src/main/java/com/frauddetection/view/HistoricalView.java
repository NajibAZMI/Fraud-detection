package com.frauddetection.view;

import com.frauddetection.rules.BlackListItem;
import com.frauddetection.DATABASE.DatabaseConnection;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
public class HistoricalView {

    public static VBox getView() {
        // Create a TabPane
        TabPane tabPane = new TabPane();

        // Create the first tab for the last 7 days
        Tab last7DaysTab = new Tab("Last 7 Days");
        last7DaysTab.setClosable(false);
        last7DaysTab.setContent(createHistogram(7)); // Call createHistogram with 7 days

        // Create the second tab for the last 30 days
        Tab last30DaysTab = new Tab("Last Month");
        last30DaysTab.setClosable(false);
        last30DaysTab.setContent(createHistogram(30)); // Call createHistogram with 30 days


        Tab blackListTab = new Tab("Black List");
        blackListTab.setClosable(false);
        blackListTab.setContent(createBlackListTable()); // Create a table for Black List

        // Add the "Download CSV" button to the Black List tab
        Button downloadButton = new Button("Download as CSV");
        downloadButton.setOnAction(e -> downloadBlackListAsCSV());
        // Transaction Button
        downloadButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #28A745; -fx-background-color: #FFFFFF; " +
                "-fx-border-color: #28A745; -fx-border-width: 2px; -fx-padding: 10px 20px; " +
                "-fx-border-radius: 5px; -fx-background-radius: 5px;");

        downloadButton.setOnMouseEntered(event -> {
            downloadButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF;" +
                    " -fx-background-color: #28A745; " +
                    "-fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-padding: 10px 20px; " +
                    "-fx-border-radius: 5px; -fx-background-radius: 5px;");
        });

        downloadButton.setOnMouseExited(event -> {
            downloadButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #28A745; -fx-background-color: #FFFFFF; " +
                    "-fx-border-color: #28A745; -fx-border-width: 2px; -fx-padding: 10px 20px; " +

                    "-fx-border-radius: 5px; -fx-background-radius: 5px;");
        });
        VBox vbox = new VBox(downloadButton);
        VBox.setMargin(downloadButton, new javafx.geometry.Insets(120, 40, 0, 100)); // Top, Right, Bottom, Left margins


        StackPane root = new StackPane(vbox);
        Image telechargeIcon = new Image(HistoricalView.class.getResourceAsStream("/images/telechargements.png"));
        if (telechargeIcon.isError()) {
            System.out.println("Error loading image");
        }
        ImageView IconForBlacklist= new ImageView(telechargeIcon);
        IconForBlacklist.setFitHeight(35);
        IconForBlacklist.setFitWidth(35);
        downloadButton.setGraphic(IconForBlacklist);

        VBox blackListContent = new VBox(createBlackListTable(), downloadButton);

        blackListTab.setContent(blackListContent); // Set the table and button in the tab

        // Add all tabs to the TabPane
        tabPane.getTabs().addAll(last7DaysTab, last30DaysTab, blackListTab);

        // Return the TabPane in a VBox
        return new VBox(tabPane);
    }

    public static TableView<BlackListItem> createBlackListTable() {
        // Create a TableView to display the Black List
        TableView<BlackListItem> table = new TableView<>();

        // Define the columns
        TableColumn<BlackListItem, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());

        TableColumn<BlackListItem, Integer> alertColumn = new TableColumn<>("NOMBRE ALERT");
        alertColumn.setCellValueFactory(cellData -> cellData.getValue().alertCountProperty().asObject());

        TableColumn<BlackListItem, java.sql.Date> dateColumn = new TableColumn<>("Last Date");
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().lastAlertDateProperty());

        TableColumn<BlackListItem, String> descriptionColumn = new TableColumn<>("Description Alert");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().lastAlertDescriptionProperty());

        // Add the columns to the table
        table.getColumns().addAll(idColumn, alertColumn, dateColumn, descriptionColumn);

        // Query to fetch data from the database for the Black List
        String query = "SELECT id, nbr_alertes, last_date, description FROM black_list ORDER BY last_date DESC";

        // Fetch data and populate the table
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                int numberOfAlerts = resultSet.getInt("nbr_alertes");
                java.sql.Date lastDate = resultSet.getDate("last_date");
                String description = resultSet.getString("description");

                // Add the row to the table
                BlackListItem item = new BlackListItem(id, numberOfAlerts, lastDate, description);
                table.getItems().add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return table;
    }

    public static BarChart<String, Number> createHistogram(int days) {
        // Create the X and Y axes
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Date");
        yAxis.setLabel("Count");

        // Create the BarChart
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Alerts and Transactions for the Last " + days + " Days");

        // Query to fetch data from database
        String query = "SELECT nbr_alertes, nbr_transactions, date FROM alert_par_jour WHERE date >= CURRENT_DATE - INTERVAL '" + days + " days' ORDER BY date ASC";

        // Prepare data series
        XYChart.Series<String, Number> alertSeries = new XYChart.Series<>();
        alertSeries.setName("Alerts");

        XYChart.Series<String, Number> transactionSeries = new XYChart.Series<>();
        transactionSeries.setName("Transactions");

        // Map to store data from database
        Map<LocalDate, int[]> dataMap = new HashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int nbalert = resultSet.getInt("nbr_alertes");
                int nbrtransaction = resultSet.getInt("nbr_transactions");
                Date date = resultSet.getDate("date");
                LocalDate localDate = date.toLocalDate();

                // Store data in the map
                dataMap.put(localDate, new int[]{nbalert, nbrtransaction});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Generate the last 'days' range
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate currentDate = today.minusDays(i);
            String formattedDate = currentDate.format(formatter);

            if (dataMap.containsKey(currentDate)) {
                // Use data from the database
                int[] counts = dataMap.get(currentDate);
                alertSeries.getData().add(new XYChart.Data<>(formattedDate, counts[0]));
                transactionSeries.getData().add(new XYChart.Data<>(formattedDate, counts[1]));
            } else {
                // Add (0, 0) for missing dates
                alertSeries.getData().add(new XYChart.Data<>(formattedDate, 0));
                transactionSeries.getData().add(new XYChart.Data<>(formattedDate, 0));
            }
        }

        // Add the series to the BarChart
        barChart.getData().addAll(alertSeries, transactionSeries);
        return barChart;
    }

    private static void downloadBlackListAsCSV() {
        TableView<BlackListItem> table = createBlackListTable();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Black List as CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ID,NBR_ALERTES,LAST_DATE,DESCRIPTION\n");

                for (BlackListItem item : table.getItems()) {
                    writer.write(String.format("%s,%d,%s,%s\n",
                            item.getId(),
                            item.getAlertCount(),
                            item.getLastAlertDate().toString(),
                            item.getLastAlertDescription()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}