package com.globalTravel.controllers.flight;

import com.globalTravel.models.flight.Flight;
import com.globalTravel.models.flight.FlightStatus;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;

public class FlightUpdateForm {

    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label formTitleLabel;
    @FXML private TextField flightNumberField;
    @FXML private TextField airlineIdField;
    @FXML private TextField departureAirportField;
    @FXML private TextField arrivalAirportField;
    @FXML private DatePicker departureDatePicker;
    @FXML private TextField departureTimeField;
    @FXML private TextField arrivalTimeField;
    @FXML private TextField durationField;
    @FXML private TextField availableSeatsField;
    @FXML private TextField priceField;
    @FXML private Label selectedImageLabel;
    @FXML private ImageView airlineLogoPreview;
    @FXML private Button saveButton;

    private File selectedLogoFile;
    private Flight flightToEdit;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize(Flight flightToEdit) {
        System.out.println("Initializing FlightForm...");

        // Populate statusComboBox with FlightStatus values
        statusComboBox.getItems().setAll(Arrays.stream(FlightStatus.values())
                .map(Enum::name)
                .toList());
        if (flightToEdit != null) {
            this.flightToEdit = flightToEdit;
        }
        populateForm();

//        clearForm();
    }




    private void clearForm() {
        flightNumberField.clear();
        airlineIdField.clear();
        departureAirportField.clear();
        arrivalAirportField.clear();
        departureDatePicker.setValue(null);
        departureTimeField.clear();
        arrivalTimeField.clear();
        durationField.clear();
        availableSeatsField.clear();
        priceField.clear();
        selectedImageLabel.setText("No image selected");
        airlineLogoPreview.setImage(null);
        statusComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void populateForm() {

            System.out.println("Populating form with flight data...");
            Platform.runLater(() -> {
                flightNumberField.setText(flightToEdit.getFlight_number());
                airlineIdField.setText(String.valueOf(flightToEdit.getAirline_id()));
                departureAirportField.setText(flightToEdit.getDeparture_airport());
                arrivalAirportField.setText(flightToEdit.getArrival_airport());
                departureDatePicker.setValue(LocalDate.parse(flightToEdit.getDeparture_time().split(" ")[0]));
                departureTimeField.setText(flightToEdit.getDeparture_time());
                arrivalTimeField.setText(flightToEdit.getArrival_time());
                durationField.setText(String.valueOf(flightToEdit.getDuration()));
                availableSeatsField.setText(String.valueOf(flightToEdit.getAvailable_seats()));
                priceField.setText(String.valueOf(flightToEdit.getBase_price()));
                statusComboBox.setValue(flightToEdit.getStatus().name());

//                // Load airline logo if available
//                if (flightToEdit.getAirlineLogoPath() != null) {
//                    File logoFile = new File(flightToEdit.getAirlineLogoPath());
//                    if (logoFile.exists()) {
//                        selectedLogoFile = logoFile;
//                        selectedImageLabel.setText(logoFile.getName());
//                        airlineLogoPreview.setImage(new Image(logoFile.toURI().toString()));
//                    }
//                }

            });

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
        try {
            String selectedStatus = statusComboBox.getValue();
            FlightStatus status = FlightStatus.valueOf(selectedStatus);

            Flight flight = new Flight(
                    Integer.parseInt(flightNumberField.getText()),
                    flightNumberField.getText(),
                    Integer.parseInt(airlineIdField.getText()),
                    departureAirportField.getText(),
                    arrivalAirportField.getText(),
                    departureTimeField.getText(),
                    arrivalTimeField.getText(),
                    Integer.parseInt(durationField.getText()),
                    Integer.parseInt(availableSeatsField.getText()),
                    Double.parseDouble(priceField.getText()),
                    status
            );


                updateFlight(flight);


            closeForm();
        } catch (Exception e) {
            System.err.println("Error saving flight: " + e.getMessage());
        }
    }


    private void updateFlight(Flight flight) {
        System.out.println("Updating flight: " + flight);
        // Implement logic to update flight
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
}
