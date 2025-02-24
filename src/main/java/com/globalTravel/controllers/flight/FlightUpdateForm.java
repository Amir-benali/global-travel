package com.globalTravel.controllers.flight;

import com.globalTravel.models.flight.Flight;
import com.globalTravel.models.flight.FlightStatus;
import com.globalTravel.services.flight.AirlineService;
import com.globalTravel.services.flight.AirportService;
import com.globalTravel.services.flight.FlightService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlightUpdateForm {

    @FXML private TextField flightNumberField;
    @FXML private ComboBox<String> airline_nameField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> departure_countryField;
    @FXML private ComboBox<String> arrival_countryField;
    @FXML private ComboBox<String> departureAirportField;
    @FXML private ComboBox<String> arrivalAirportField;
    @FXML private DatePicker departureDatePicker;
    @FXML private ComboBox<Integer> departureHourComboBox;
    @FXML private ComboBox<Integer> departureMinuteComboBox;
    @FXML private DatePicker arrivalDatePicker;
    @FXML private ComboBox<Integer> arrivalHourComboBox;
    @FXML private ComboBox<Integer> arrivalMinuteComboBox;
    @FXML private TextField availableSeatsField;
    @FXML private TextField priceField;
    @FXML private Label selectedImageLabel;
    @FXML private ImageView airlineLogoPreview;
    @FXML private Button saveButton;

    private AirlineService airlineService = new AirlineService();
    private AirportService airportService = new AirportService();
    private FlightService flightService = new FlightService();
    private Flight flightToEdit;
    private Stage stage;
    private File selectedLogoFile;
    private List<String> airportNames;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize(Flight flight) {
        flightToEdit = flight;

        departureAirportField.setEditable(true);
        arrivalAirportField.setEditable(true);

        try {
            List<String> airportCountries = airportService.fetchAirportCountries();
            departure_countryField.getItems().setAll(airportCountries);
            arrival_countryField.getItems().setAll(airportCountries);
        } catch (Exception e) {
            System.out.println("Error fetching airport countries: " + e.getMessage());
        }

        try {
            airportNames = airportService.fetchAirportNames();
            departureAirportField.getItems().setAll(airportNames);
            arrivalAirportField.getItems().setAll(airportNames);
        } catch (Exception e) {
            System.out.println("Error fetching airport names: " + e.getMessage());
        }

        departureAirportField.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            filterComboBox(departureAirportField, airportNames);
        });

        arrivalAirportField.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            filterComboBox(arrivalAirportField, airportNames);
        });

        departureAirportField.setOnMouseClicked(event -> departureAirportField.show());
        arrivalAirportField.setOnMouseClicked(event -> arrivalAirportField.show());

        statusComboBox.getItems().setAll(Arrays.stream(FlightStatus.values())
                .map(Enum::name)
                .toList());

        List<String> airlineNames = airlineService.getAllAirlineNames();
        airline_nameField.getItems().setAll(airlineNames);

        departureDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        arrivalDatePicker.setDisable(true);

        departureDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            arrivalDatePicker.setDisable(false);
            arrivalDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(newValue));
                }
            });
        });

        arrivalDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBefore(departureDatePicker.getValue())) {
                showAlert(Alert.AlertType.ERROR, "Invalid Date", "Arrival date cannot be before departure date.");
                arrivalDatePicker.setValue(null);
            }
        });

        if (flightToEdit != null) {
            populateForm();
        }
    }

    private void filterComboBox(ComboBox<String> comboBox, List<String> items) {
        String query = comboBox.getEditor().getText();
        List<String> filteredItems = items.stream()
                .filter(item -> item.toLowerCase().contains(query.toLowerCase()))
                .sorted(
                        (item1, item2) -> {
                            if (item1.toLowerCase().startsWith(query.toLowerCase())) {
                                return -1;
                            } else if (item2.toLowerCase().startsWith(query.toLowerCase())) {
                                return 1;
                            }
                            return item1.compareTo(item2);
                        }
                )
                .collect(Collectors.toList());
        comboBox.getItems().setAll(filteredItems);
        comboBox.show();
    }

    private void populateForm() {
        Platform.runLater(() -> {
            flightNumberField.setText(flightToEdit.getFlight_number());
            airline_nameField.setValue(flightToEdit.getAirline_name());
            departure_countryField.setValue(flightToEdit.getDeparture_country());
            arrival_countryField.setValue(flightToEdit.getArrival_country());
            departureAirportField.setValue(flightToEdit.getDeparture_airport());
            arrivalAirportField.setValue(flightToEdit.getArrival_airport());
            departureDatePicker.setValue(flightToEdit.getDeparture_time().toLocalDateTime().toLocalDate());
            departureHourComboBox.setValue(flightToEdit.getDeparture_time().toLocalDateTime().getHour());
            departureMinuteComboBox.setValue(flightToEdit.getDeparture_time().toLocalDateTime().getMinute());
            arrivalDatePicker.setValue(flightToEdit.getArrival_time().toLocalDateTime().toLocalDate());
            arrivalHourComboBox.setValue(flightToEdit.getArrival_time().toLocalDateTime().getHour());
            arrivalMinuteComboBox.setValue(flightToEdit.getArrival_time().toLocalDateTime().getMinute());
            availableSeatsField.setText(String.valueOf(flightToEdit.getAvailable_seats()));
            priceField.setText(String.valueOf(flightToEdit.getBase_price()));
            statusComboBox.setValue(flightToEdit.getStatus().name());
        });
    }

    private boolean validateInput() {
        String errorMessage = "";

        if (flightNumberField.getText().isEmpty()) errorMessage += "Flight number is required.\n";
        if (airline_nameField.getValue() == null) errorMessage += "Airline name is required.\n";
        if (departure_countryField.getValue() == null) errorMessage += "Departure country is required.\n";
        if (arrival_countryField.getValue() == null) errorMessage += "Arrival country is required.\n";
        if (departureAirportField.getValue() == null) errorMessage += "Departure airport is required.\n";
        if (arrivalAirportField.getValue() == null) errorMessage += "Arrival airport is required.\n";
        if (departureDatePicker.getValue() == null) errorMessage += "Departure date is required.\n";
        if (arrivalDatePicker.getValue() == null) errorMessage += "Arrival date is required.\n";
        if (statusComboBox.getValue() == null) errorMessage += "Flight status is required.\n";

        try {
            Integer.parseInt(availableSeatsField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "Invalid number of available seats.\n";
        }

        try {
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "Invalid price.\n";
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
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Airline Logo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            selectedLogoFile = selectedFile;
            selectedImageLabel.setText(selectedFile.getName());
            airlineLogoPreview.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    @FXML
    private void handleSaveFlight() {
        if (!validateInput()) return;

        try {
            String flightNumber = flightNumberField.getText();
            FlightStatus status = FlightStatus.valueOf(statusComboBox.getValue());
            LocalDate departureDate = departureDatePicker.getValue();
            LocalDate arrivalDate = arrivalDatePicker.getValue();
            LocalTime departureTime = LocalTime.of(
                    Integer.parseInt(String.valueOf(departureHourComboBox.getValue())),
                    Integer.parseInt(String.valueOf(departureMinuteComboBox.getValue()))
            );
            LocalTime arrivalTime = LocalTime.of(
                    Integer.parseInt(String.valueOf(arrivalHourComboBox.getValue())),
                    Integer.parseInt(String.valueOf(arrivalMinuteComboBox.getValue()))
            );
            LocalDateTime departureDateTime = LocalDateTime.of(departureDate, departureTime);
            LocalDateTime arrivalDateTime = LocalDateTime.of(arrivalDate, arrivalTime);

            long durationInHours = java.time.Duration.between(departureDateTime, arrivalDateTime).toHours();

            Flight flight = new Flight(
                    flightToEdit.getId_flight(),
                    flightNumber,
                    airline_nameField.getValue(),
                    departure_countryField.getValue(),
                    arrival_countryField.getValue(),
                    departureAirportField.getValue(),
                    arrivalAirportField.getValue(),
                    Timestamp.valueOf(departureDateTime),
                    Timestamp.valueOf(arrivalDateTime),
                    (int) durationInHours,
                    Integer.parseInt(availableSeatsField.getText()),
                    Double.parseDouble(priceField.getText()),
                    status
            );

            flightService.modifier(flight);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Flight updated successfully.");
            clearForm();
            closeForm();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error updating flight: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        closeForm();
    }

    private void clearForm() {
        flightNumberField.clear();
        airline_nameField.getSelectionModel().clearSelection();
        departure_countryField.getSelectionModel().clearSelection();
        arrival_countryField.getSelectionModel().clearSelection();
        departureAirportField.getSelectionModel().clearSelection();
        arrivalAirportField.getSelectionModel().clearSelection();
        departureDatePicker.setValue(null);
        arrivalDatePicker.setDisable(true);
        arrivalDatePicker.setValue(null);
        departureHourComboBox.getSelectionModel().clearSelection();
        departureMinuteComboBox.getSelectionModel().clearSelection();
        arrivalHourComboBox.getSelectionModel().clearSelection();
        arrivalMinuteComboBox.getSelectionModel().clearSelection();
        availableSeatsField.clear();
        priceField.clear();
        selectedImageLabel.setText("No image selected");
        airlineLogoPreview.setImage(null);
        statusComboBox.getSelectionModel().clearSelection();
    }

    private void closeForm() {
        if (stage != null) {
            stage.close();
        }
    }

    public void setFlightToEdit(Flight object) {
        flightToEdit = object;
    }
}