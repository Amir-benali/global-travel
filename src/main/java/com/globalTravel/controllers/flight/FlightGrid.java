package com.globalTravel.controllers.flight;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.flight.Flight;
import com.globalTravel.models.flight.FlightStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

/**
 * The FlightGrid class represents a user interface component that displays a grid of flights
 * and allows users to interact with flight data. It implements the Navigatable interface, providing
 * navigation capabilities within the application.
 *
 * Responsibilities of this class include:
 * - Loading and displaying a list of flights in a grid format.
 * - Creating individual flight cards with flight details and actions such as update, view, and delete.
 * - Navigating to other views or forms within the application for updating or adding flights.
 */
public class FlightGrid implements Navigatable {
    private DashBoard dashBoardController;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private FlowPane flightsGrid;

    private List<Flight> flights;

    @FXML
    public void initialize() {
        loadFlights();
    }

    private void loadFlights() {
        flights = getFlights();

        for (Flight flight : flights) {
            VBox flightCard = createFlightCard(flight);
            flightsGrid.getChildren().add(flightCard);
        }
    }

    private VBox createFlightCard(Flight flight) {
        VBox card = new VBox(10);
        card.getStyleClass().add("flight-offer-card");

        // Add airline logo
        ImageView airlineLogoView = new ImageView(new Image("/images/logo.jpg"));
        airlineLogoView.setFitWidth(200);
        airlineLogoView.setFitHeight(150);
        airlineLogoView.setPreserveRatio(true);

        VBox flightInfo = new VBox(5);
        flightInfo.getStyleClass().add("flight-info");

        // Flight details
        Label routeLabel = new Label(flight.getDeparture_airport() + " to " + flight.getArrival_airport());
        routeLabel.getStyleClass().add("flight-title");

        Label departureTimeLabel = new Label("Departure: " + flight.getDeparture_time());
        departureTimeLabel.getStyleClass().add("flight-departure-time");

        Label arrivalTimeLabel = new Label("Arrival: " + flight.getArrival_time());
        arrivalTimeLabel.getStyleClass().add("flight-arrival-time");

        Label priceLabel = new Label("Price: $" + String.format("%.2f", flight.getBase_price()));
        priceLabel.getStyleClass().add("flight-price");

        // Flight status display
        Label statusLabel = new Label("Status: " + flight.getStatus().toString());
        statusLabel.getStyleClass().add("flight-status");


        // Buttons
        Button updateButton = new Button("update flight");
        updateButton.setOnAction(e -> {
            try {
                navigateToUpdateFlight(flight);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        updateButton.getStyleClass().add("view-details-button");

        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.getStyleClass().add("view-details-button");
        viewDetailsButton.setOnAction(e -> handleViewDetails(flight));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("view-details-button");
        deleteButton.setOnAction(e -> {deleteFlight(flight);});
        HBox buttonHbox = new HBox(3);
        buttonHbox.getChildren().addAll(updateButton,deleteButton);
        flightInfo.getChildren().addAll(routeLabel, departureTimeLabel, arrivalTimeLabel, priceLabel, statusLabel,buttonHbox );

        card.getChildren().addAll(airlineLogoView, flightInfo);

        return card;
    }

    private void deleteFlight(Flight flight) {
        System.out.println("deleting : "+ flight);
    }


    private void navigateToUpdateFlight(Flight flight) throws IOException {
        dashBoardController.navigateTo("dashboard/flight/flight-update-form.fxml");
        ((FlightUpdateForm)dashBoardController.getController()).initialize(flight);
    }

    private void handleViewDetails(Flight flight) {
        // Implement view details logic here
    }

    private List<Flight> getFlights() {
        return Arrays.asList(
                new Flight(
                        101,
                        "AB1234",
                        1,
                        "JFK",
                        "LAX",
                        "2025-02-20 10:00",
                        "2025-02-20 13:00",
                        300,
                        150,
                        250.50,
                        FlightStatus.Scheduled
                )
        );
    }

    /**
     * Navigates to the flight form view to add a new flight.
     *
     * @param actionEvent the action event that triggered this method
     */
    public void addFlight(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/flight/flight-create-form.fxml");
    }
}
