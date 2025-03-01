package com.globalTravel.controllers.flight;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.models.flight.Ticket;
import com.globalTravel.models.flight.TicketClass;
import com.globalTravel.models.flight.TicketStatus;
import com.globalTravel.services.flight.TicketService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Timestamp;

public class TicketUpdateForm {

    @FXML private TextField flightIdField;
    @FXML private TextField seatNumberField;
    @FXML private TextField ticketClassField;
    @FXML private TextField ticketPriceField;
    @FXML private TextField ticketStatusField;
    @FXML private TextField bookingDateField;
    @FXML private Button backButton;

    private TicketService ticketService = new TicketService();
    private Stage stage;
    private DashBoard dashBoardController;
    private Ticket ticket;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        populateForm();
    }

    @FXML
    public void initialize() {
        // Initialization logic if needed
    }

    private void populateForm() {
        flightIdField.setText(String.valueOf(ticket.getFlight_id()));
        seatNumberField.setText(ticket.getSeat_number());
        ticketClassField.setText(ticket.getTicketClass().name());
        ticketPriceField.setText(String.valueOf(ticket.getTicket_price()));
        ticketStatusField.setText(ticket.getStatus().name());
        bookingDateField.setText(ticket.getBooking_date().toString());
    }

    @FXML
    private void handleSaveTicket() {
        if (!validateInput()) return;

        try {
            ticket.setFlight_id(Integer.parseInt(flightIdField.getText()));
            ticket.setSeat_number(seatNumberField.getText());
            ticket.setTicketClass(TicketClass.valueOf(ticketClassField.getText()));
            ticket.setTicket_price(Double.parseDouble(ticketPriceField.getText()));
            ticket.setStatus(TicketStatus.valueOf(ticketStatusField.getText()));
            ticket.setBooking_date(Timestamp.valueOf(bookingDateField.getText()));

            ticketService.modifier(ticket);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket updated successfully.");
            closeForm();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error updating ticket: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        String errorMessage = "";

        if (flightIdField.getText().isEmpty()) {
            errorMessage += "Flight ID is required.\n";
        }

        if (seatNumberField.getText().isEmpty()) {
            errorMessage += "Seat number is required.\n";
        }

        if (ticketClassField.getText().isEmpty()) {
            errorMessage += "Ticket class is required.\n";
        }

        if (ticketPriceField.getText().isEmpty()) {
            errorMessage += "Ticket price is required.\n";
        }

        if (ticketStatusField.getText().isEmpty()) {
            errorMessage += "Ticket status is required.\n";
        }

        if (bookingDateField.getText().isEmpty()) {
            errorMessage += "Booking date is required.\n";
        }

        if (!errorMessage.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", errorMessage);
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private void closeForm() {
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void handleBack() {
        dashBoardController.navigateTo("dashboard/flight/ticket-grid.fxml");
    }
}