package com.globalTravel.controllers.flight;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.flight.Flight;
import com.globalTravel.models.flight.Ticket;
import com.globalTravel.models.flight.TicketClass;
import com.globalTravel.models.flight.TicketStatus;
import com.globalTravel.models.user.User;
import com.globalTravel.services.flight.FlightBookingService;
import com.globalTravel.services.flight.FlightService;
import com.globalTravel.services.user.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.resource.Emailv31;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingController implements Navigatable, FrontNavigatable {
    public Button btnCancelBooking;
    @FXML private Label lblTicketPrice;
    @FXML private TextField txtPassengerName;
    @FXML private TextField txtPassengerEmail;
    @FXML private ComboBox<TicketClass> cmbTicketClass;
    @FXML private Button btnConfirmBooking;
    @FXML private VBox flightDetailsContainer;
    @FXML private GridPane seatGrid;
    @FXML private TextField selectedUserField;
    @FXML private Button selectUserButton;
    @FXML private TextField searchField;
    private DashBoard dashBoardController;
    private FrontOffice frontOfficeController;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    private Flight flight;
    private User currentUser;
    private User selectedUser;
    private FlightBookingService flightBookingService = new FlightBookingService();
    private UserService userService = new UserService();
    private Button selectedSeatButton;

    private static final int TOTAL_SEATS = 40; // Set total seats per flight
    private static final int SEATS_PER_ROW = 4; // Default layout (4 seats per row)

    public void initialize(Flight flight, User currentUser) {
        this.flight = flight;
        this.currentUser = currentUser;
        populateFlightDetails(flight);
        populateSeatGrid(); // Generates 40 seats dynamically
        populateTicketClasses();

        selectUserButton.setOnAction(e -> handleSelectUser());
    }

    private void handleSelectUser() {
        List<User> users = fetchUsers();

        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Select User");
        dialog.setHeaderText("Choose a user from the list");

        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

        ListView<User> userListView = new ListView<>();
        userListView.getItems().addAll(users);

        userListView.setCellFactory(param -> new ListCell<User>() {
            private final ImageView imageView = new ImageView();
            private final Label nameLabel = new Label();

            {
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                imageView.setPreserveRatio(true);

                setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4; -fx-border-color: #ddd; -fx-border-width: 1;");
                nameLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
            }

            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String imageUrl = user.getImage();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        imageView.setImage(new Image(imageUrl));
                    } else {
                        imageView.setImage(new Image("/images/user-icon.png"));
                    }

                    nameLabel.setText(user.getFirstName() + " " + user.getLastName());

                    HBox hbox = new HBox(10, imageView, nameLabel);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    setGraphic(hbox);
                }
            }
        });

        searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            List<User> filteredUsers = users.stream()
                    .filter(user -> user.getFirstName().toLowerCase().contains(newValue.toLowerCase()) ||
                            user.getLastName().toLowerCase().contains(newValue.toLowerCase()))
                    .collect(Collectors.toList());
            userListView.getItems().setAll(filteredUsers);
        });

        VBox dialogContent = new VBox(10, searchField, userListView);
        dialog.getDialogPane().setContent(dialogContent);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == selectButtonType) {
                return userListView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            selectedUser = user;
            selectedUserField.setText(user.getFirstName() + " " + user.getLastName());
        });
    }

    private List<User> fetchUsers() {
        List<User> users = userService.rechercher();
        return users.stream().filter(user -> user.getRoles().toLowerCase().equals("employee")).collect(Collectors.toList());
    }

    private List<String> getFlightDetailsText(Flight flight) {
        List<String> details = new ArrayList<>();
        details.add("Flight Number: " + flight.getFlight_number());
        details.add("Airline: " + flight.getAirlineId());
        details.add("Departure: \n     " + flight.getDeparture_country() + "\n     " + flight.getDeparture_airport() + "\n     (" + flight.getDeparture_time() + ")");
        details.add("Arrival: \n     " + flight.getArrival_country() + "\n     " + flight.getArrival_airport() + "\n     (" + flight.getArrival_time() + ")");
        details.add("Base Price: $" + flight.getBase_price());
        return details;
    }

    private void populateFlightDetails(Flight flight) {
        flightDetailsContainer.getChildren().clear();
        List<String> details = getFlightDetailsText(flight);
        for (String detail : details) {
            HBox detailBox = new HBox();
            detailBox.getStyleClass().add("flight-detail-box");
            Label label = new Label(detail);
            label.getStyleClass().add("flight-detail");
            detailBox.getChildren().add(label);
            flightDetailsContainer.getChildren().add(detailBox);
        }
    }

    private void populateSeatGrid() {
        seatGrid.getChildren().clear();
        TicketClass selectedClass = cmbTicketClass.getValue();
        char[] seatLetters = {'A', 'B', 'C', 'D'};
        int totalRows = TOTAL_SEATS / SEATS_PER_ROW;

        for (int row = 1; row <= totalRows; row++) {
            for (int col = 0; col < SEATS_PER_ROW; col++) {
                String seatNumber = row + String.valueOf(seatLetters[col]);
                Button seatButton = new Button(seatNumber);
                seatButton.getStyleClass().add("seat-button");

                if (selectedClass == TicketClass.Economy && row <= 5) {
                    seatButton.setDisable(true);
                } else if (selectedClass == TicketClass.Business && (row <= 2 || row > 5)) {
                    seatButton.setDisable(true);
                } else if (selectedClass == TicketClass.First_Class && row > 2) {
                    seatButton.setDisable(true);
                } else {
                    seatButton.setOnAction(e -> handleSeatSelection(seatButton));
                }

                seatGrid.add(seatButton, col, row - 1);
            }
        }
    }

    private void populateTicketClasses() {
        cmbTicketClass.getItems().addAll(TicketClass.Economy, TicketClass.Business, TicketClass.First_Class);
        cmbTicketClass.setOnAction(e -> {
            populateSeatGrid();
            updateTicketPrice();
        });
    }

    private void handleSeatSelection(Button seatButton) {
        if (selectedSeatButton != null) {
            selectedSeatButton.getStyleClass().remove("selected-seat");
        }
        selectedSeatButton = seatButton;
        selectedSeatButton.getStyleClass().add("selected-seat");
    }

    @FXML
    private void handleConfirmBooking() {
        String passengerName = txtPassengerName.getText();
        String passengerEmail = txtPassengerEmail.getText();
        String seatNumber = selectedSeatButton != null ? selectedSeatButton.getText() : null;
        TicketClass ticketClass = cmbTicketClass.getValue();

        if (passengerName.isEmpty() || passengerEmail.isEmpty() || seatNumber == null || ticketClass == null) {
            showAlert("Error", "Please enter all required information.");
            return;
        }

        if (!isValidEmail(passengerEmail)) {
            showAlert("Error", "Please enter a valid email address.");
            return;
        }

        if (selectedUser == null) {
            showAlert("Error", "Please select a user.");
            return;
        }

        double ticketPrice = calculateTicketPrice(flight.getBase_price(), ticketClass);

        Ticket ticket = new Ticket(
                flight.getId_flight(),
                currentUser.getId(),
                passengerEmail,
                seatNumber,
                ticketClass,
                ticketPrice,
                TicketStatus.Not_Booked,
                new Timestamp(System.currentTimeMillis()),
                selectedUser.getId()
        );

        try {
            sendConfirmationEmail(passengerEmail, passengerName, seatNumber, ticketClass, ticketPrice);
        } catch (Exception e) {
            showAlert("Error", "An error occurred. Please try again.");
            return;
        }

        // Navigate to payment form
        if (dashBoardController != null) {
            dashBoardController.navigateTo("dashboard/flight/FlightPaymentForm.fxml");
            ((FlightPaymentForm) dashBoardController.getController()).initialize(ticket, flight, currentUser);
        } else if (frontOfficeController != null) {
            frontOfficeController.navigateTo("dashboard/flight/FlightPaymentForm.fxml");
            ((FlightPaymentForm) frontOfficeController.getController()).initialize(ticket, flight, currentUser);
        }
        else {
            showAlert("Error", "An error occurred. Please try again.");
        }
    }


    public void sendConfirmationEmail(String email, String name, String seatNumber, TicketClass ticketClass, double ticketPrice) throws MailjetException {
        MailjetRequest request;
        MailjetResponse response;
        ClientOptions options = ClientOptions.builder()
                .apiKey("0a7f1ea93da8c099802fd68d7bc4075c")
                .apiSecretKey("23ed580b04b4d1b04b7c4c1f553cb5bc")
                .build();

        MailjetClient client = new MailjetClient(options);
        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", "global.travel.companyteam@gmail.com")
                                        .put("Name", "GlobalTravel"))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", email)
                                                .put("Name", name)))
                                .put(Emailv31.Message.SUBJECT, "Flight Booking Confirmation")
                                .put(Emailv31.Message.TEXTPART, "Greetings from GlobalTravel!")
                                .put(Emailv31.Message.HTMLPART, "<h3>Dear " + name + ",</h3>"
                                        +"<div style='font-family: Arial, sans-serif; color: #333;'>"
                                                + "<h2 style='color: #0056b3;'>Your Booking Confirmation</h2>"
                                                + "<p>We are pleased to confirm your flight booking. Below are your flight details:</p>"
                                                + "<div style='border: 1px solid #ddd; padding: 10px; border-radius: 8px; background-color: #f9f9f9;'>"
                                                + "<p><strong>Flight Number:</strong> " + flight.getFlight_number() + "</p>"
                                                + "<p><strong>Departure:</strong> " + flight.getDeparture_country() + " - "
                                                + flight.getDeparture_airport() + " at " + flight.getDeparture_time() + "</p>"
                                                + "<p><strong>Arrival:</strong> " + flight.getArrival_country() + " - "
                                                + flight.getArrival_airport() + " at " + flight.getArrival_time() + "</p>"
                                                + "<p><strong>Seat Number:</strong> " + seatNumber + "</p>"
                                                + "<p><strong>Ticket Class:</strong> " + ticketClass + "</p>"
                                                + "<p><strong>Price:</strong> $" +ticketPrice + "</p>"
                                                + "</div>"
                                                + "<p>Thank you for choosing <strong>GlobalTravel</strong>!</p>"
                                                + "<p>Have a great flight,</p>"
                                                + "<p><strong>GlobalTravel Team</strong></p>"
                                                + "</div>")));

        response = client.post(request);
        System.out.println(response.getStatus());
        System.out.println(response.getData());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleCancelBooking() {
        if (dashBoardController != null) {
            dashBoardController.navigateTo("dashboard/flight/flight-grid.fxml");
        }
        if (frontOfficeController != null) {
            frontOfficeController.navigateTo("dashboard/flight/flight-grid.fxml");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private double calculateTicketPrice(double basePrice, TicketClass ticketClass) {
        switch (ticketClass) {
            case Economy:
                return basePrice;
            case Business:
                return basePrice * 1.5;
            case First_Class:
                return basePrice * 2.0;
            default:
                throw new IllegalArgumentException("Unknown ticket class: " + ticketClass);
        }
    }

    private void updateTicketPrice() {
        TicketClass selectedClass = cmbTicketClass.getValue();
        if (selectedClass != null) {
            double ticketPrice = calculateTicketPrice(flight.getBase_price(), selectedClass);
            lblTicketPrice.setText("$" + ticketPrice);
        } else {
            lblTicketPrice.setText("");
        }
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
    }
}