package com.globalTravel.controllers.flight;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.flight.Ticket;
import com.globalTravel.models.flight.TicketClass;
import com.globalTravel.models.flight.TicketStatus;
import com.globalTravel.services.flight.TicketService;
import com.globalTravel.models.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Timestamp;

public class TicketCreateForm implements Navigatable, FrontNavigatable {

    @FXML private TextField flightIdField;
    @FXML private TextField seatNumberField;
    @FXML private TextField ticketClassField;
    @FXML private TextField ticketPriceField;
    @FXML private TextField ticketStatusField;
    @FXML private TextField bookingDateField;
    @FXML private Button backButton;

    private final TicketService ticketService = new TicketService();
    private Stage stage;
    private DashBoard dashBoardController;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    public void initialize() {
        // Initialization logic if needed

    }

    @FXML
    private void handleSaveTicket() {
        if (!validateInput()) return;

        try {
            Ticket ticket = new Ticket(
                    Integer.parseInt(flightIdField.getText()),
                    dashBoardController.getCurrentUser().getId(),
                    dashBoardController.getCurrentUser().getEmail(),
                    seatNumberField.getText(),
                    TicketClass.valueOf(ticketClassField.getText()),
                    Double.parseDouble(ticketPriceField.getText()),
                    TicketStatus.valueOf(ticketStatusField.getText()),
                    Timestamp.valueOf(bookingDateField.getText()),
                    -1
            );

            ticketService.ajouter(ticket);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket created successfully.");
            clearForm();
            closeForm();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error creating ticket: " + e.getMessage());
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

    private void clearForm() {
        flightIdField.clear();
        seatNumberField.clear();
        ticketClassField.clear();
        ticketPriceField.clear();
        ticketStatusField.clear();
        bookingDateField.clear();
    }

    @FXML
    private void handleCancel() {
        clearForm();
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

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {


    }
}