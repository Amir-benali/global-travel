package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.TypeActivity;
import com.globalTravel.services.activity.ActivityService;
import com.globalTravel.utils.DataSource;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityCreateForm implements Navigatable {
    private DashBoard dashBoardController;

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
    @FXML private ComboBox<Integer> hotelIdComboBox;
    @FXML private ComboBox<Integer> carIdComboBox;
    @FXML private ComboBox<Integer> flightIdComboBox;
    @FXML private Button saveButton;
    @FXML private Label statusLabel;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }
    private final ActivityService activityService = new ActivityService();
    private final Connection connection = DataSource.getInstance().getConnection();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        typeComboBox.getItems().setAll(TypeActivity.values());
        populateHourMinuteSecondComboBoxes();

        // Charger les IDs depuis la base de données
        hotelIdComboBox.getItems().setAll(getIdsFromDatabase("hotel", "id_hotel_h"));
        carIdComboBox.getItems().setAll(getIdsFromDatabase("private_car", "id"));
        flightIdComboBox.getItems().setAll(getIdsFromDatabase("flights", "id_flight"));
    }

    private List<Integer> getIdsFromDatabase(String tableName, String idColumnName) {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT " + idColumnName + " FROM " + tableName;

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ids.add(resultSet.getInt(idColumnName));
                System.out.println("ID récupéré depuis " + tableName + ": " + resultSet.getInt(idColumnName));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des ID de " + tableName + " : " + e.getMessage());
        }

        return ids;
    }

    private void populateHourMinuteSecondComboBoxes() {
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i);
        }
        startHourComboBox.getItems().setAll(hours);
        endHourComboBox.getItems().setAll(hours);

        String[] minutesSeconds = {"00", "15", "30", "45"};
        startMinuteComboBox.getItems().setAll(minutesSeconds);
        endMinuteComboBox.getItems().setAll(minutesSeconds);
        startSecondComboBox.getItems().setAll(minutesSeconds);
        endSecondComboBox.getItems().setAll(minutesSeconds);
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
                    dashBoardController.navigateTo("dashboard/activity/activity-grid.fxml");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "L'ajout a échoué !");
                    statusLabel.setText("Erreur lors de l'ajout !");
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
                hotelIdComboBox.getValue() != null ? hotelIdComboBox.getValue() : 0,
                carIdComboBox.getValue() != null ? carIdComboBox.getValue() : 0,
                flightIdComboBox.getValue() != null ? flightIdComboBox.getValue() : 0
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
        // Contrôle du nom de l'activité
        if (activityNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le nom de l'activité est requis.");
            return false;
        }

        // Contrôle de la description
        if (descriptionField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La description est requise.");
            return false;
        }

        // Contrôle du prix
        if (!isValidPrice(priceField.getText())) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le prix est invalide. Veuillez entrer un nombre positif.");
            return false;
        }

        // Contrôle de la localisation
        if (localisationField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La localisation est requise.");
            return false;
        }

        // Contrôle des dates de début et de fin
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Les dates de début et de fin sont requises.");
            return false;
        }

        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La date de début ne peut pas être après la date de fin.");
            return false;
        }

        // Contrôle des heures, minutes et secondes
        if (startHourComboBox.getValue() == null || startMinuteComboBox.getValue() == null || startSecondComboBox.getValue() == null ||
                endHourComboBox.getValue() == null || endMinuteComboBox.getValue() == null || endSecondComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez sélectionner une heure, une minute et une seconde valides.");
            return false;
        }

        // Contrôle du type d'activité
        if (typeComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez sélectionner un type d'activité.");
            return false;
        }

        // Contrôle de l'ID de l'hôtel
        if (hotelIdComboBox.getValue() == null || hotelIdComboBox.getValue() == 0) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez sélectionner un ID d'hôtel valide.");
            return false;
        } else if (!idExistsInDatabase("hotel", "id_hotel_h", hotelIdComboBox.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation", "L'ID de l'hôtel sélectionné n'existe pas dans la base de données.");
            return false;
        }

        // Contrôle de l'ID de la voiture
        if (carIdComboBox.getValue() == null || carIdComboBox.getValue() == 0) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez sélectionner un ID de voiture valide.");
            return false;
        } else if (!idExistsInDatabase("private_car", "id", carIdComboBox.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation", "L'ID de la voiture sélectionné n'existe pas dans la base de données.");
            return false;
        }

        // Contrôle de l'ID du vol
        if (flightIdComboBox.getValue() == null || flightIdComboBox.getValue() == 0) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez sélectionner un ID de vol valide.");
            return false;
        } else if (!idExistsInDatabase("flights", "id_flight", flightIdComboBox.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation", "L'ID du vol sélectionné n'existe pas dans la base de données.");
            return false;
        }

        return true;
    }

    /**
     * Vérifie si un ID existe dans une table spécifique de la base de données.
     *
     * @param tableName    Le nom de la table.
     * @param idColumnName Le nom de la colonne ID.
     * @param id           L'ID à vérifier.
     * @return true si l'ID existe, false sinon.
     */
    private boolean idExistsInDatabase(String tableName, String idColumnName, int id) {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + idColumnName + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Retourne true si l'ID existe
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de l'ID dans la table " + tableName + " : " + e.getMessage());
        }
        return false;
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
        hotelIdComboBox.getSelectionModel().clearSelection();
        carIdComboBox.getSelectionModel().clearSelection();
        flightIdComboBox.getSelectionModel().clearSelection();
        typeComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
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