package com.globalTravel.controllers.activity;

import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.TypeActivity;
import com.globalTravel.services.activity.ActivityService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ActivityCreateForm {

    @FXML private ComboBox<TypeActivity> typeComboBox;
    @FXML private TextField activityNameField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker startDatePicker;
    @FXML private ComboBox<String> startHourComboBox;
    @FXML private ComboBox<String> startMinuteComboBox;
    @FXML private ComboBox<String> startSecondComboBox;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> endHourComboBox;
    @FXML private ComboBox<String> endMinuteComboBox;
    @FXML private ComboBox<String> endSecondComboBox;
    @FXML private TextField priceField;
    @FXML private TextField localisationField;
    @FXML private TextField hotelIdField;
    @FXML private TextField carIdField;
    @FXML private TextField flightIdField;
    @FXML private Button saveButton;
    @FXML private Label statusLabel;

    private final ActivityService activityService = new ActivityService();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        typeComboBox.getItems().setAll(TypeActivity.values());
        populateHourMinuteSecondComboBoxes();
    }

    private void populateHourMinuteSecondComboBoxes() {
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i);
        }
        startHourComboBox.getItems().setAll(hours);
        endHourComboBox.getItems().setAll(hours);

        String[] minutes = {"00", "15", "30", "45"};
        startMinuteComboBox.getItems().setAll(minutes);
        endMinuteComboBox.getItems().setAll(minutes);

        String[] seconds = {"00", "15", "30", "45"};
        startSecondComboBox.getItems().setAll(seconds);
        endSecondComboBox.getItems().setAll(seconds);
    }

    @FXML
    private void handleSaveActivity() {
        try {
            if (validateInputs()) {
                Activity activity = createActivityFromInputs();
                boolean isSaved = activityService.ajouter(activity);

                if (isSaved) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Activité ajoutée avec succès !");
                    statusLabel.setText("Activité ajoutée avec succès !");
                    statusLabel.setStyle("-fx-text-fill: green;");
                    clearForm();
                    closeForm();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "L'activité n'a pas pu être ajoutée.");
                    statusLabel.setText("Échec de l'ajout de l'activité.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            statusLabel.setText("Erreur : " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private Activity createActivityFromInputs() {
        Timestamp startTimestamp = combineDateTime(startDatePicker.getValue(), startHourComboBox.getValue(), startMinuteComboBox.getValue(), startSecondComboBox.getValue());
        Timestamp endTimestamp = combineDateTime(endDatePicker.getValue(), endHourComboBox.getValue(), endMinuteComboBox.getValue(), endSecondComboBox.getValue());

        return new Activity(
                startTimestamp,
                endTimestamp,
                descriptionField.getText().trim(),
                localisationField.getText().trim(),
                parsePrice(priceField.getText()),
                activityNameField.getText().trim(),
                typeComboBox.getValue(),
                parseId(hotelIdField.getText()),
                parseId(carIdField.getText()),
                parseId(flightIdField.getText())
        );
    }

    private Timestamp combineDateTime(java.time.LocalDate date, String hour, String minute, String second) {
        if (date == null || hour == null || minute == null || second == null) {
            return null;
        }
        LocalDateTime dateTime = LocalDateTime.of(date, java.time.LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(second)));
        return Timestamp.valueOf(dateTime);
    }

    private boolean validateInputs() {
        if (activityNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le nom de l'activité est requis.");
            return false;
        }
        if (descriptionField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La description est requise.");
            return false;
        }
        if (!isValidPrice(priceField.getText())) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le prix est invalide. Veuillez entrer un nombre positif.");
            return false;
        }
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Les dates de début et de fin sont requises.");
            return false;
        }
        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La date de début ne peut pas être après la date de fin.");
            return false;
        }
        return true;
    }

    private boolean isValidPrice(String priceText) {
        try {
            return Integer.parseInt(priceText) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int parsePrice(String priceText) {
        return isValidPrice(priceText) ? Integer.parseInt(priceText) : 0;
    }

    private int parseId(String idText) {
        try {
            return Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        closeForm();
    }

    private void clearForm() {
        activityNameField.clear();
        descriptionField.clear();
        localisationField.clear();
        priceField.clear();
        hotelIdField.clear();
        carIdField.clear();
        flightIdField.clear();
        typeComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        startHourComboBox.setValue(null);
        startMinuteComboBox.setValue(null);
        startSecondComboBox.setValue(null);
        endHourComboBox.setValue(null);
        endMinuteComboBox.setValue(null);
        endSecondComboBox.setValue(null);
    }

    private void closeForm() {
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
