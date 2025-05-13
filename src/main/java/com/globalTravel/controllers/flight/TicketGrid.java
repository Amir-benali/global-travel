// TicketGrid.java
package com.globalTravel.controllers.flight;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.flight.Flight;
import com.globalTravel.models.flight.Ticket;
import com.globalTravel.models.user.User;
import com.globalTravel.services.flight.TicketService;
import com.globalTravel.services.flight.FlightService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class TicketGrid implements Navigatable, FrontNavigatable {
    private DashBoard dashBoardController;
    private FrontOffice frontOfficeController;
    private final TicketService ticketService = new TicketService();
    private final FlightService flightService = new FlightService();

    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
        loadTickets();
    }

    @FXML
    private FlowPane ticketsGrid;

    @FXML
    public void initialize() {
        System.out.println("TicketGrid initialized");
        loadTickets();
    }

    private void loadTickets() {
        ticketsGrid.getChildren().clear();
        User currentUser = getCurrentUser();
        if (currentUser != null && currentUser.getRoles().toLowerCase().equals("employee")) {
            int selectedUserId =currentUser.getId(); // Method to get the selected user ID
            List<Ticket> tickets = ticketService.getTicetsbySelectedUserId(selectedUserId);
            System.out.println("Tickets found: " + tickets.size());
            for (Ticket ticket : tickets) {
                VBox ticketCard = createTicketCard(ticket);
                ticketsGrid.getChildren().add(ticketCard);
            }
        }
                else if (currentUser != null && currentUser.getRoles().toLowerCase().equals("user")) {
                    int currentUserId = currentUser.getId();
                    List<Ticket> tickets = ticketService.getTicketsByUserId(currentUserId);
                    System.out.println("Tickets found: " + tickets.size());
                    for (Ticket ticket : tickets) {
                        VBox ticketCard = createTicketCard(ticket);
                        ticketsGrid.getChildren().add(ticketCard);
                    }
                }
        else {
            System.out.println("Current user is not an employee or is null");
        }
    }



    private User getCurrentUser() {
        if (frontOfficeController != null) {
            return frontOfficeController.getCurrentUser();
        } else if (dashBoardController != null) {
            return dashBoardController.getCurrentUser();
        }
        return null;
    }

    private VBox createTicketCard(Ticket ticket) {
        VBox card = new VBox(10);
        card.getStyleClass().add("flight-offer-card");

        ImageView ticketLogoView = new ImageView(new Image("/images/ticket.png"));
        ticketLogoView.setFitWidth(200);
        ticketLogoView.setFitHeight(150);
        ticketLogoView.setPreserveRatio(true);

        VBox ticketInfo = new VBox(5);
        ticketInfo.getStyleClass().add("flight-info");

        Label seatLabel = new Label("Seat: " + ticket.getSeat_number());
        seatLabel.getStyleClass().add("flight-title");

        Label priceLabel = new Label("Price: $" + String.format("%.2f", ticket.getTicket_price()));
        priceLabel.getStyleClass().add("flight-price");

        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.getStyleClass().add("view-details-button");
        viewDetailsButton.setOnAction(e -> handleViewDetails(ticket));

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("view-details-button");
        cancelButton.setOnAction(e -> handleCancelTicket(ticket));

        HBox buttonHbox = new HBox(3);
        buttonHbox.getChildren().addAll(viewDetailsButton, cancelButton);
        ticketInfo.getChildren().addAll(seatLabel, priceLabel, buttonHbox);

        card.getChildren().addAll(ticketLogoView, ticketInfo);

        return card;
    }

    private void handleViewDetails(Ticket ticket) {
        // Assuming you have a method to get flight details by flight ID
        Flight flight = flightService.getFlightById(ticket.getFlight_id());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ticket Details");
        alert.setHeaderText("Ticket ID: " + ticket.getTicket_id());
        alert.setContentText("Seat: " + ticket.getSeat_number() + "\n" +
                "Price: $" + String.format("%.2f", ticket.getTicket_price()) + "\n" +
                "Class: " + ticket.getTicketClass().name() + "\n" +
                "Status: " + ticket.getStatus().name() + "\n" +
                "Booking Date: " + ticket.getBooking_date() + "\n" +
                "Flight Number: " + flight.getFlight_number() + "\n" +
                "Departure: " + flight.getDeparture_country()+ ", " + flight.getDeparture_airport()+ "at " + flight.getDeparture_time() + " + \n" +
                "Arrival: " + flight.getArrival_country()+ ", " + flight.getArrival_airport()+ "at " + flight.getArrival_time());

        alert.showAndWait();
    }

    private void handleCancelTicket(Ticket ticket) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Ticket");
        alert.setHeaderText("Are you sure you want to cancel this ticket?");
        alert.setContentText("Ticket ID: " + ticket.getTicket_id());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ticketService.supprimer(ticket);
                loadTickets();
            }
        });
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
        loadTickets(); // Call loadTickets after setting the controller
    }
}