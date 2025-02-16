package com.globalTravel.controllers.activity;

import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.TypeActivity;
import com.globalTravel.services.activity.ActivityService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

public class ActivityUpdateForm {

    @FXML private TextField activityNameField;
    @FXML private TextField descriptionField;
    @FXML private TextField locationField;
    @FXML private DatePicker startDatePicker;
    @FXML private ComboBox<String> startHourComboBox;
    @FXML private ComboBox<String> startMinuteComboBox;
    @FXML private ComboBox<String> startSecondComboBox;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> endHourComboBox;
    @FXML private ComboBox<String> endMinuteComboBox;
    @FXML private ComboBox<String> endSecondComboBox;
    @FXML private TextField priceField;
    @FXML private ComboBox<TypeActivity> typeComboBox;
    @FXML private TextField hotelTextField;  // TextField for hotel selection
    @FXML private TextField carTextField;  // TextField for car selection
    @FXML private TextField flightTextField;  // TextField for flight selection
    @FXML private Button saveButton;

    private Activity activityToEdit;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Dynamically populate ComboBox with TypeActivity enum values
        typeComboBox.setItems(FXCollections.observableArrayList(TypeActivity.values()));
        populateHourMinuteSecondComboBoxes();
    }

    public void initialize(Activity activityToEdit) {
        System.out.println("Initializing Activity Update Form...");

        if (activityToEdit != null) {
            this.activityToEdit = activityToEdit;
        }
        populateForm();
    }

    private void populateForm() {
        System.out.println("Populating form with activity data...");
        Platform.runLater(() -> {
            activityNameField.setText(activityToEdit.getNomActivity());
            descriptionField.setText(activityToEdit.getDescription());
            locationField.setText(activityToEdit.getLocalisation());
            startDatePicker.setValue(LocalDate.parse(activityToEdit.getDateDebut().toString().split(" ")[0]));
            String[] startTime = activityToEdit.getDateDebut().toString().split(" ")[1].split(":");
            startHourComboBox.setValue(startTime[0]);
            startMinuteComboBox.setValue(startTime[1]);
            startSecondComboBox.setValue(startTime[2]);
            endDatePicker.setValue(LocalDate.parse(activityToEdit.getDateFin().toString().split(" ")[0]));
            String[] endTime = activityToEdit.getDateFin().toString().split(" ")[1].split(":");
            endHourComboBox.setValue(endTime[0]);
            endMinuteComboBox.setValue(endTime[1]);
            endSecondComboBox.setValue(endTime[2]);

            priceField.setText(String.valueOf(activityToEdit.getPrixTotal()));
            typeComboBox.setValue(activityToEdit.getTypeActivity());

            // Set hotel, car, flight TextField values
            hotelTextField.setText(String.valueOf(activityToEdit.getJoinHotelId()));
            carTextField.setText(String.valueOf(activityToEdit.getJoinVoitureId()));
            flightTextField.setText(String.valueOf(activityToEdit.getJoinVolsId()));
        });
    }

    private void populateHourMinuteSecondComboBoxes() {
        // Populate hours for ComboBox
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i);
        }
        startHourComboBox.getItems().setAll(hours);
        endHourComboBox.getItems().setAll(hours);

        // Populate minutes and seconds for ComboBox
        String[] minutesAndSeconds = new String[] {"00", "15", "30", "45"};
        startMinuteComboBox.getItems().setAll(minutesAndSeconds);
        endMinuteComboBox.getItems().setAll(minutesAndSeconds);
        startSecondComboBox.getItems().setAll(minutesAndSeconds);
        endSecondComboBox.getItems().setAll(minutesAndSeconds);
    }

    @FXML
    private void handleSaveActivity() {
        try {
            // Check if activityToEdit is null
            if (activityToEdit == null) {
                showAlert("Error", "No activity selected to update.", Alert.AlertType.ERROR);
                return;
            }

            // Validate fields
            if (validateFields()) {
                // Construct the updated activity object
                TypeActivity selectedType = typeComboBox.getValue();
                if (selectedType == null) {
                    showAlert("Validation Error", "Please select an activity type.", Alert.AlertType.WARNING);
                    return;
                }

                // Get selected start and end times from ComboBoxes
                String startHour = startHourComboBox.getValue();
                String startMinute = startMinuteComboBox.getValue();
                String startSecond = startSecondComboBox.getValue();
                String endHour = endHourComboBox.getValue();
                String endMinute = endMinuteComboBox.getValue();
                String endSecond = endSecondComboBox.getValue();

                if (startHour == null || startMinute == null || startSecond == null || endHour == null || endMinute == null || endSecond == null) {
                    showAlert("Validation Error", "Please select valid start and end times.", Alert.AlertType.WARNING);
                    return;
                }

                // Parse start and end times into Timestamp
                String startTime = String.format("%s:%s:%s", startHour, startMinute, startSecond);
                String endTime = String.format("%s:%s:%s", endHour, endMinute, endSecond);

                // Create updated Activity object
                Activity activity = new Activity(
                        activityToEdit.getId(),
                        Timestamp.valueOf(startDatePicker.getValue().atTime(LocalTime.parse(startTime))),
                        Timestamp.valueOf(endDatePicker.getValue().atTime(LocalTime.parse(endTime))),
                        descriptionField.getText(),
                        locationField.getText(),
                        Integer.parseInt(priceField.getText()),
                        activityNameField.getText(),
                        selectedType,
                        parseIntOrNull(hotelTextField.getText()),
                        parseIntOrNull(carTextField.getText()),
                        parseIntOrNull(flightTextField.getText())
                );

                // Update activity in the database
                ActivityService activityService = new ActivityService();
                activityService.modifier(activity);

                showAlert("Success", "Activity updated successfully.", Alert.AlertType.INFORMATION);
                closeForm();
            }
        } catch (Exception e) {
            showAlert("Error", "Error saving activity: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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

    // Method to validate fields before saving
    private boolean validateFields() {
        if (activityNameField.getText().isEmpty() || descriptionField.getText().isEmpty() || locationField.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill in all required fields.", Alert.AlertType.WARNING);
            return false;
        }
        try {
            Integer.parseInt(priceField.getText());
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid price.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    // Method to show alert
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper method to parse integer safely
    private Integer parseIntOrNull(String text) {
        try {
            return text.isEmpty() ? null : Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
