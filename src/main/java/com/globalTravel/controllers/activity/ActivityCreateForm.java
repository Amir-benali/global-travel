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
    @FXML private ComboBox<String> hotelIdComboBox; // Affiche les noms d'hôtels
    @FXML private ComboBox<String> carIdComboBox; // Affiche les marques de voitures
    @FXML private ComboBox<String> flightIdComboBox; // Affiche les numéros de vol
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
        // Récupérer les valeurs de l'énumération TypeActivity.
        typeComboBox.getItems().setAll(TypeActivity.values());
        populateHourMinuteSecondComboBoxes();

        // Charger les noms d'hôtels, les marques de voitures et les numéros de vol depuis la base de données
        hotelIdComboBox.getItems().setAll(getNamesFromDatabase("hotel", "nom_h"));
        carIdComboBox.getItems().setAll(getCarBrandsFromDatabase()); // Charger les marques de voitures
        flightIdComboBox.getItems().setAll(getFlightNumbersFromDatabase()); // Charger les numéros de vol
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
                getHotelIdByName(hotelIdComboBox.getValue()), // Récupérer l'ID de l'hôtel par son nom
                getCarIdByBrand(carIdComboBox.getValue()), // Récupérer l'ID de la voiture par sa marque
                getFlightIdByNumber(flightIdComboBox.getValue()) // Récupérer l'ID du vol par son numéro
        );
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

        // Contrôle du nom de l'hôtel
        if (hotelIdComboBox.getValue() == null || hotelIdComboBox.getValue().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez sélectionner un nom d'hôtel valide.");
            return false;
        } else if (!nameExistsInDatabase("hotel", "nom_h", hotelIdComboBox.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le nom de l'hôtel sélectionné n'existe pas dans la base de données.");
            return false;
        }

        // Contrôle de la marque de la voiture
        if (carIdComboBox.getValue() == null || carIdComboBox.getValue().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez sélectionner une marque de voiture valide.");
            return false;
        } else if (!brandExistsInDatabase("private_car", "brand", carIdComboBox.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La marque de voiture sélectionnée n'existe pas dans la base de données.");
            return false;
        }

        // Contrôle du numéro de vol
        if (flightIdComboBox.getValue() == null || flightIdComboBox.getValue().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez sélectionner un numéro de vol valide.");
            return false;
        } else if (!flightNumberExistsInDatabase("flights", "flight_number", flightIdComboBox.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le numéro de vol sélectionné n'existe pas dans la base de données.");
            return false;
        }

        return true;
    }

    private boolean nameExistsInDatabase(String tableName, String nameColumnName, String name) {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + nameColumnName + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Retourne true si le nom existe
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification du nom dans la table " + tableName + " : " + e.getMessage());
        }
        return false;
    }

    private boolean brandExistsInDatabase(String tableName, String brandColumnName, String brand) {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + brandColumnName + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, brand);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Retourne true si la marque existe
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de la marque dans la table " + tableName + " : " + e.getMessage());
        }
        return false;
    }

    private boolean flightNumberExistsInDatabase(String tableName, String flightNumberColumnName, String flightNumber) {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + flightNumberColumnName + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, flightNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Retourne true si le numéro de vol existe
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification du numéro de vol dans la table " + tableName + " : " + e.getMessage());
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