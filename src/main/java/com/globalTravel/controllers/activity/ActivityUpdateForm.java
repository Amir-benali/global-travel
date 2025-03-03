package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
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

public class ActivityUpdateForm implements Navigatable {
    private DashBoard dashBoardController;

    @FXML private TextField activityNameField;
    @FXML private TextArea descriptionField;
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
    @FXML private ComboBox<String> hotelIdComboBox;  // ComboBox for hotel names
    @FXML private ComboBox<String> carIdComboBox;   // ComboBox for car brands
    @FXML private ComboBox<String> flightIdComboBox; // ComboBox for flight numbers
    @FXML private Button saveButton;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

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

        // Load hotel names, car brands, and flight numbers from the database
        hotelIdComboBox.getItems().setAll(getNamesFromDatabase("hotel", "nom_h"));
        carIdComboBox.getItems().setAll(getCarBrandsFromDatabase());
        flightIdComboBox.getItems().setAll(getFlightNumbersFromDatabase());
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
            hotelIdComboBox.setValue(getHotelNameById(activityToEdit.getJoinHotelId()));
            carIdComboBox.setValue(getCarBrandById(activityToEdit.getJoinVoitureId()));
            flightIdComboBox.setValue(getFlightNumberById(activityToEdit.getJoinVolsId()));
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

                // Get selected hotel, car, and flight names from ComboBoxes
                String hotelName = hotelIdComboBox.getValue();
                String carBrand = carIdComboBox.getValue();
                String flightNumber = flightIdComboBox.getValue();

                // Get IDs from names
                int hotelId = getHotelIdByName(hotelName);
                int carId = getCarIdByBrand(carBrand);
                int flightId = getFlightIdByNumber(flightNumber);

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
                        hotelId,
                        carId,
                        flightId,
                        0

                );

                // Update activity in the database
                ActivityService activityService = new ActivityService();
                boolean isUpdated = activityService.modifier(activity);

                if (isUpdated) {
                    showAlert("Success", "Activity updated successfully.", Alert.AlertType.INFORMATION);
                    closeForm();
                    dashBoardController.navigateTo("dashboard/activity/activity-grid.fxml");

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
        dashBoardController.navigateTo("dashboard/activity/activity-grid.fxml");
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

        // Check hotel, car, and flight names
        if (hotelIdComboBox.getValue() == null || carIdComboBox.getValue() == null || flightIdComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select valid hotel, car, and flight.", Alert.AlertType.WARNING);
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

    private List<String> getNamesFromDatabase(String tableName, String nameColumnName) {
        List<String> names = new ArrayList<>();
        String query = "SELECT " + nameColumnName + " FROM " + tableName;

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                names.add(resultSet.getString(nameColumnName));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des noms de " + tableName + " : " + e.getMessage());
        }

        return names;
    }

    private List<String> getCarBrandsFromDatabase() {
        List<String> brands = new ArrayList<>();
        String query = "SELECT brand FROM private_car";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                brands.add(resultSet.getString("brand"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des marques de voitures : " + e.getMessage());
        }

        return brands;
    }

    private List<String> getFlightNumbersFromDatabase() {
        List<String> flightNumbers = new ArrayList<>();
        String query = "SELECT flight_number FROM flights";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                flightNumbers.add(resultSet.getString("flight_number"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des numéros de vol : " + e.getMessage());
        }

        return flightNumbers;
    }

    private String getHotelNameById(int hotelId) {
        String query = "SELECT nom_h FROM hotel WHERE id_hotel_h = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, hotelId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("nom_h");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du nom de l'hôtel : " + e.getMessage());
        }
        return "N/A";
    }

    private String getCarBrandById(int carId) {
        String query = "SELECT brand FROM private_car WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, carId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("brand");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la marque de la voiture : " + e.getMessage());
        }
        return "N/A";
    }

    private String getFlightNumberById(int flightId) {
        String query = "SELECT flight_number FROM flights WHERE id_flight = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, flightId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("flight_number");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du numéro de vol : " + e.getMessage());
        }
        return "N/A";
    }

    private int getHotelIdByName(String hotelName) {
        String query = "SELECT id_hotel_h FROM hotel WHERE nom_h = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, hotelName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id_hotel_h");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'ID de l'hôtel : " + e.getMessage());
        }
        return 0; // Retourne 0 si l'hôtel n'est pas trouvé
    }

    private int getCarIdByBrand(String brand) {
        String query = "SELECT id FROM private_car WHERE brand = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, brand);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'ID de la voiture : " + e.getMessage());
        }
        return 0; // Retourne 0 si la voiture n'est pas trouvée
    }

    private int getFlightIdByNumber(String flightNumber) {
        String query = "SELECT id_flight FROM flights WHERE flight_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, flightNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id_flight");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'ID du vol : " + e.getMessage());
        }
        return 0; // Retourne 0 si le vol n'est pas trouvé
    }
}