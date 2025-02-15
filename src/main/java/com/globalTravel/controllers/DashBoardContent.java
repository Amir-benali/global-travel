package com.globalTravel.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DashBoardContent {

    @FXML private Label totalBookingsLabel;
    @FXML private Label revenueLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label newActivitiesLabel;

    @FXML private PieChart bookingsPieChart;
    @FXML private LineChart<String, Number> revenueTrendChart;
    @FXML private TableView<TopActivity> topActivitiesTable;

    @FXML
    public void initialize() {
        updateQuickStats();
        setupBookingsPieChart();
        setupRevenueTrendChart();
        setupTopActivitiesTable();
    }

    private void updateQuickStats() {
        totalBookingsLabel.setText("1,234");
        revenueLabel.setText("$56,789");
        activeUsersLabel.setText("5,678");
        newActivitiesLabel.setText("42");
    }

    private void setupBookingsPieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Hotels", 35),
                new PieChart.Data("Flights", 30),
                new PieChart.Data("Activities", 25),
                new PieChart.Data("Car Rentals", 10)
        );
        bookingsPieChart.setData(pieChartData);
    }

    private void setupRevenueTrendChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Revenue");

        series.getData().add(new XYChart.Data<>("Jan", 50000));
        series.getData().add(new XYChart.Data<>("Feb", 60000));
        series.getData().add(new XYChart.Data<>("Mar", 55000));
        series.getData().add(new XYChart.Data<>("Apr", 65000));
        series.getData().add(new XYChart.Data<>("May", 75000));
        series.getData().add(new XYChart.Data<>("Jun", 80000));

        revenueTrendChart.getData().add(series);
    }

    private void setupTopActivitiesTable() {
        TableColumn<TopActivity, String> nameColumn = new TableColumn<>("Activity Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<TopActivity, Integer> bookingsColumn = new TableColumn<>("Bookings");
        bookingsColumn.setCellValueFactory(new PropertyValueFactory<>("bookings"));

        TableColumn<TopActivity, String> revenueColumn = new TableColumn<>("Revenue");
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        TableColumn<TopActivity, Double> ratingColumn = new TableColumn<>("Rating");
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        topActivitiesTable.getColumns().setAll(nameColumn, bookingsColumn, revenueColumn, ratingColumn);

        ObservableList<TopActivity> activities = FXCollections.observableArrayList(
                new TopActivity("City Tour", 150, "$7,500", 4.8),
                new TopActivity("Mountain Hiking", 120, "$6,000", 4.7),
                new TopActivity("Beach Volleyball", 100, "$5,000", 4.6),
                new TopActivity("Wine Tasting", 80, "$4,000", 4.9)
        );

        topActivitiesTable.setItems(activities);
    }

    // Inner class for top activities table
    public static class TopActivity {
        private final String name;
        private final int bookings;
        private final String revenue;
        private final double rating;

        public TopActivity(String name, int bookings, String revenue, double rating) {
            this.name = name;
            this.bookings = bookings;
            this.revenue = revenue;
            this.rating = rating;
        }

        public String getName() { return name; }
        public int getBookings() { return bookings; }
        public String getRevenue() { return revenue; }
        public double getRating() { return rating; }
    }
}