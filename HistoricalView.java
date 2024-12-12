package com.frauddetection;

import com.frauddetection.DATABASE.DatabaseConnection;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

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

        // Add both tabs to the TabPane
        tabPane.getTabs().addAll(last7DaysTab, last30DaysTab);

        // Return the TabPane in a VBox
        return new VBox(tabPane);
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
}