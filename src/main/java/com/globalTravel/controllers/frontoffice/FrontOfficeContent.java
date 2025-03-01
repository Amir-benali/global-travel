package com.globalTravel.controllers.frontoffice;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FrontOfficeContent {

    // Pie Charts
    @FXML
    private PieChart bookingDistribution;
    @FXML
    private PieChart revenueDistribution;

    // Table and Columns
    @FXML
    private TableView<Booking> recentBookingsTable;
    @FXML
    private TableColumn<Booking, String> bookingIdColumn;
    @FXML
    private TableColumn<Booking, String> customerColumn;
    @FXML
    private TableColumn<Booking, String> serviceColumn;
    @FXML
    private TableColumn<Booking, String> dateColumn;
    @FXML
    private TableColumn<Booking, Double> amountColumn;
    @FXML
    private TableColumn<Booking, String> statusColumn;

    @FXML
    public void initialize() {
        // Initialize Charts
        setupCharts();

        // Initialize Table
        setupTable();
    }

    /**
     * Set up the pie charts with sample data.
     */
    private void setupCharts() {
        // Booking Distribution Chart
        ObservableList<PieChart.Data> bookingData = FXCollections.observableArrayList(
                new PieChart.Data("Flights", 30),
                new PieChart.Data("Cars", 25),
                new PieChart.Data("Hotels", 35),
                new PieChart.Data("Activities", 10)
        );
        bookingDistribution.setData(bookingData);

        // Revenue Distribution Chart
        ObservableList<PieChart.Data> revenueData = FXCollections.observableArrayList(
                new PieChart.Data("Flights", 40),
                new PieChart.Data("Cars", 20),
                new PieChart.Data("Hotels", 30),
                new PieChart.Data("Activities", 10)
        );
        revenueDistribution.setData(revenueData);
    }

    /**
     * Set up the table with sample data.
     */
    private void setupTable() {
        // Bind columns to Booking properties
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add sample data to the table
        ObservableList<Booking> bookings = FXCollections.observableArrayList(
                new Booking("B001", "John Doe", "Flight", "2023-09-15", 500.00, "Confirmed"),
                new Booking("B002", "Jane Smith", "Hotel", "2023-09-16", 300.00, "Pending"),
                new Booking("B003", "Bob Johnson", "Car", "2023-09-17", 150.00, "Confirmed")
        );

        recentBookingsTable.setItems(bookings);
    }

    /**
     * Inner class representing a Booking.
     */
    public static class Booking {
        private String bookingId;
        private String customer;
        private String service;
        private String date;
        private double amount;
        private String status;

        public Booking(String bookingId, String customer, String service, String date, double amount, String status) {
            this.bookingId = bookingId;
            this.customer = customer;
            this.service = service;
            this.date = date;
            this.amount = amount;
            this.status = status;
        }

        // Getters
        public String getBookingId() {
            return bookingId;
        }

        public String getCustomer() {
            return customer;
        }

        public String getService() {
            return service;
        }

        public String getDate() {
            return date;
        }

        public double getAmount() {
            return amount;
        }

        public String getStatus() {
            return status;
        }
    }
}