package com.globalTravel.controllers.activity;

import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.TypeActivity;
import com.globalTravel.services.activity.ActivityService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import com.globalTravel.utils.DataSource;

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
    @FXML private ComboBox<Integer> hotelIdComboBox;  // ComboBox for hotel selection
    @FXML private ComboBox<Integer> carIdComboBox;   // ComboBox for car selection
    @FXML private ComboBox<Integer> flightIdComboBox; // ComboBox for flight selection
    @FXML private Button saveButton;

    private Activity activityToEdit;
    private Stage stage;
    private final Connection connection = DataSource.getInstance().getConnection();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Dynamically populate ComboBox with TypeActivity enum values
        typeComboBox.setItems(FXCollections.observableArrayList(TypeActivity.values()));
        populateHourMinuteSecondComboBoxes();

        // Load IDs from the database
        hotelIdComboBox.getItems().setAll(getIdsFromDatabase("hotel", "id_hotel_h"));
        carIdComboBox.getItems().setAll(getIdsFromDatabase("private_car", "id"));
        flightIdComboBox.getItems().setAll(getIdsFromDatabase("flights", "id_flight"));
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

            // Set hotel, car, flight ComboBox values
            hotelIdComboBox.setValue(activityToEdit.getJoinHotelId());
            carIdComboBox.setValue(activityToEdit.getJoinVoitureId());
            flightIdComboBox.setValue(activityToEdit.getJoinVolsId());
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
            if (validateInputs()) {
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

                // Get selected hotel, car, and flight IDs from ComboBoxes
                Integer hotelId = hotelIdComboBox.getValue();
                Integer carId = carIdComboBox.getValue();
                Integer flightId = flightIdComboBox.getValue();

                // Validate hotel, car, and flight IDs
                if (hotelId != null && !idExistsInDatabase("hotel", "id_hotel_h", hotelId)) {
                    showAlert("Validation Error", "The selected hotel ID does not exist in the database.", Alert.AlertType.WARNING);
                    return;
                }
                if (carId != null && !idExistsInDatabase("private_car", "id", carId)) {
                    showAlert("Validation Error", "The selected car ID does not exist in the database.", Alert.AlertType.WARNING);
                    return;
                }
                if (flightId != null && !idExistsInDatabase("flights", "id_flight", flightId)) {
                    showAlert("Validation Error", "The selected flight ID does not exist in the database.", Alert.AlertType.WARNING);
                    return;
                }

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
                        hotelId != null ? hotelId : 0,
                        carId != null ? carId : 0,
                        flightId != null ? flightId : 0
                );

                // Update activity in the database
                ActivityService activityService = new ActivityService();
                boolean isUpdated = activityService.modifier(activity);

                if (isUpdated) {
                    showAlert("Success", "Activity updated successfully.", Alert.AlertType.INFORMATION);
                    closeForm();
                } else {
                    showAlert("Error", "Failed to update activity.", Alert.AlertType.ERROR);
                }
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
    private boolean validateInputs() {
        // Check required fields
        if (activityNameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "The activity name is required.", Alert.AlertType.WARNING);
            return false;
        }
        if (descriptionField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "The description is required.", Alert.AlertType.WARNING);
            return false;
        }
        if (locationField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "The location is required.", Alert.AlertType.WARNING);
            return false;
        }

        // Check price field
        if (!isValidPrice(priceField.getText())) {
            showAlert("Validation Error", "The price must be a valid positive number.", Alert.AlertType.WARNING);
            return false;
        }

        // Check dates
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert("Validation Error", "Start and end dates are required.", Alert.AlertType.WARNING);
            return false;
        }
        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showAlert("Validation Error", "The start date cannot be after the end date.", Alert.AlertType.WARNING);
            return false;
        }

        // Check time fields
        if (startHourComboBox.getValue() == null || startMinuteComboBox.getValue() == null || startSecondComboBox.getValue() == null ||
                endHourComboBox.getValue() == null || endMinuteComboBox.getValue() == null || endSecondComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select valid start and end times.", Alert.AlertType.WARNING);
            return false;
        }

        // Check activity type
        if (typeComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select an activity type.", Alert.AlertType.WARNING);
            return false;
        }

        // Check hotel, car, and flight IDs
        if (hotelIdComboBox.getValue() == null || carIdComboBox.getValue() == null || flightIdComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select valid IDs for hotel, car, and flight.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    // Check if the price is valid
    private boolean isValidPrice(String priceText) {
        try {
            return Integer.parseInt(priceText) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Method to show alert
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Retrieves IDs from the database for a given table and column.
     *
     * @param tableName    The name of the table.
     * @param idColumnName The name of the ID column.
     * @return A list of IDs.
     */
    private List<Integer> getIdsFromDatabase(String tableName, String idColumnName) {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT " + idColumnName + " FROM " + tableName;

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ids.add(resultSet.getInt(idColumnName));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des ID de " + tableName + " : " + e.getMessage());
        }

        return ids;
    }

    /**
     * Checks if an ID exists in a specific table in the database.
     *
     * @param tableName    The name of the table.
     * @param idColumnName The name of the ID column.
     * @param id           The ID to check.
     * @return true if the ID exists, false otherwise.
     */
    private boolean idExistsInDatabase(String tableName, String idColumnName, int id) {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + idColumnName + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Returns true if the ID exists
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de l'ID dans la table " + tableName + " : " + e.getMessage());
        }
        return false;
    }
}