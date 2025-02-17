package com.globalTravel.controllers.flight;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.flight.Ticket;
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

public class TicketGrid implements Navigatable {
    private DashBoard dashBoardController;
    private final TicketService ticketService = new TicketService();

    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private FlowPane ticketsGrid;

    @FXML
    public void initialize() {
        loadTickets();
    }

    private void loadTickets() {
        ticketsGrid.getChildren().clear();
        List<Ticket> tickets = ticketService.rechercher();
        for (Ticket ticket : tickets) {
            VBox ticketCard = createTicketCard(ticket);
            ticketsGrid.getChildren().add(ticketCard);
        }
    }
    private VBox createTicketCard(Ticket ticket) {
        VBox card = new VBox(10);
        card.getStyleClass().add("flight-offer-card"); // Use the same style class as flight card

        ImageView ticketLogoView = new ImageView(new Image("/images/ticket.png"));
        ticketLogoView.setFitWidth(200);
        ticketLogoView.setFitHeight(150);
        ticketLogoView.setPreserveRatio(true);

        VBox ticketInfo = new VBox(5);
        ticketInfo.getStyleClass().add("flight-info"); // Use the same style class as flight card

        Label seatLabel = new Label("Seat: " + ticket.getSeat_number());
        seatLabel.getStyleClass().add("flight-title"); // Use the same style class as flight card

        Label priceLabel = new Label("Price: $" + String.format("%.2f", ticket.getTicket_price()));
        priceLabel.getStyleClass().add("flight-price"); // Use the same style class as flight card

        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.getStyleClass().add("view-details-button");
        viewDetailsButton.setOnAction(e -> handleViewDetails(ticket));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("view-details-button"); // Use the same style class as flight card
        deleteButton.setOnAction(e -> handleDeleteTicket(ticket));

        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("view-details-button"); // Use the same style class as flight card
        updateButton.setOnAction(e -> handleUpdateTicket(ticket));

        HBox buttonHbox = new HBox(3);
        buttonHbox.getChildren().addAll(viewDetailsButton);
        ticketInfo.getChildren().addAll(seatLabel, priceLabel, buttonHbox);

        card.getChildren().addAll(ticketLogoView, ticketInfo);

        return card;
    }
    private void handleViewDetails(Ticket ticket) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ticket Details");
        alert.setHeaderText("Ticket Information");
        alert.setContentText("Ticket ID: " + ticket.getTicket_id() + "\n" +
                "Flight ID: " + ticket.getFlight_id() + "\n" +
                "Seat Number: " + ticket.getSeat_number() + "\n" +
                "Class: " + ticket.getTicketClass() + "\n" +
                "Price: $" + ticket.getTicket_price() + "\n" +
                "Status: " + ticket.getStatus() + "\n" +
                "Booking Date: " + ticket.getBooking_date());
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
                loadTickets();
            }
        });
    }

    private void handleUpdateTicket(Ticket ticket) {
       // dashBoardController.navigateTo("dashboard/flight/ticket-update-form.fxml", ticket);
    }

    public void addTicket(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/flight/ticket-create-form.fxml");
    }
}