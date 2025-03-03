// TicketGrid.java
package com.globalTravel.controllers.flight;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.flight.Flight;
import com.globalTravel.models.flight.Ticket;
import com.globalTravel.models.user.User;
import com.globalTravel.services.flight.FlightService;
import com.globalTravel.services.flight.TicketService;
import javafx.event.ActionEvent;
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

    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
        loadTicketsForBackOffice();
    }

    @FXML
    private FlowPane ticketsGrid;

    @FXML
    public void initialize() {
        System.out.println("TicketGrid initialized");
        if (frontOfficeController != null) {
            loadTicketsForFrontOffice();
        } else if (dashBoardController != null) {
            loadTicketsForBackOffice();
        }
    }

    private void loadTicketsForBackOffice() {
        ticketsGrid.getChildren().clear();
        List<Ticket> tickets = ticketService.rechercher();
        System.out.println("Tickets found: " + tickets.size());
        for (Ticket ticket : tickets) {
            VBox ticketCard = createBackOfficeTicketCard(ticket);
            ticketsGrid.getChildren().add(ticketCard);
        }
    }

    private void loadTicketsForFrontOffice() {
        ticketsGrid.getChildren().clear();
        if (frontOfficeController != null) {
            User currentUser = frontOfficeController.getCurrentUser();
            if (currentUser != null) {
                List<Ticket> tickets = ticketService.getTicketsByUserId(currentUser.getId());
                System.out.println("Tickets found: " + tickets.size());
                for (Ticket ticket : tickets) {
                    VBox ticketCard = createFrontOfficeTicketCard(ticket);
                    ticketsGrid.getChildren().add(ticketCard);
                }
            } else {
                System.out.println("Current user is null");
            }
        } else {
            System.out.println("FrontOfficeController is null");
        }
    }

    private VBox createBackOfficeTicketCard(Ticket ticket) {
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

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("view-details-button");
        deleteButton.setOnAction(e -> handleDeleteTicket(ticket));

        ticketInfo.getChildren().addAll(seatLabel, priceLabel, deleteButton);
        card.getChildren().addAll(ticketLogoView, ticketInfo);

        return card;
    }

    private VBox createFrontOfficeTicketCard(Ticket ticket) {
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
        FlightService flightService = new FlightService();
        Flight flight = flightService.getFlightById(ticket.getFlight_id());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ticket Details");
        alert.setHeaderText("Ticket and Flight Information");
        alert.setContentText("Ticket ID: " + ticket.getTicket_id() + "\n" +
                "Flight ID: " + ticket.getFlight_id() + "\n" +
                "Seat Number: " + ticket.getSeat_number() + "\n" +
                "Passenger Email: " + ticket.getPassenger_email() + "\n" +
                "Class: " + ticket.getTicketClass() + "\n" +
                "Price: $" + ticket.getTicket_price() + "\n" +
                "Status: " + ticket.getStatus() + "\n" +
                "Booking Date: " + ticket.getBooking_date() + "\n\n" +
                "Flight Number: " + flight.getFlight_number() + "\n" +
                "Departure: " + flight.getDeparture_country()+ " - " + flight.getDeparture_airport()+ " (" + flight.getDeparture_time() + "\n" +
                "Arrival: " + flight.getArrival_country()+ " - " + flight.getArrival_airport()+ " (" + flight.getArrival_time()+ "\n" +
                "Status: " + flight.getStatus());
        alert.showAndWait();
    }

    private void handleDeleteTicket(Ticket ticket) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Ticket");
        alert.setHeaderText("Are you sure you want to delete this ticket?");
        alert.setContentText("Ticket ID: " + ticket.getTicket_id());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ticketService.supprimer(ticket);
                loadTicketsForBackOffice();
            }
        });
    }

    private void handleCancelTicket(Ticket ticket) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Ticket");
        alert.setHeaderText("Are you sure you want to cancel this ticket?");
        alert.setContentText("Ticket ID: " + ticket.getTicket_id());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ticketService.supprimer(ticket);
                loadTicketsForFrontOffice();
            }
        });
    }



    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
        loadTicketsForFrontOffice(); // Call loadTickets after setting the controller
    }
}