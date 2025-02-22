package com.globalTravel.controllers.hotel;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.services.hotel.HotelService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

public class HotelCreateForm implements Navigatable {

    @FXML private Label formTitleLabel;
    @FXML private TextField hotelNameField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField countryField;
    @FXML private TextField categoryField;
    @FXML private TextField servicesField;
    @FXML private TextField coordinatesField;
    @FXML private TextArea reviewsField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    private DashBoard dashBoardController;
    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }
    private HotelService hotelService = new HotelService();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        System.out.println("Initializing HotelCreateForm...");
        addListeners();
    }

    private void addListeners() {
        hotelNameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        addressField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        cityField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        countryField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        categoryField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        servicesField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        coordinatesField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        reviewsField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    /**
     * Valide le formulaire :
     * - Les champs (nom, adresse, ville, pays, services, coordonnées) doivent contenir entre 3 et 30 caractères.
     * - Le champ d'avis (reviews) doit contenir entre 2 et 50 caractères.
     * - La catégorie doit être un nombre entre 1 et 5.
     */
    private boolean validateForm() {
        boolean isValid = true;

        // Nom (3-30)
        String name = hotelNameField.getText().trim();
        if (name.isEmpty()) {
            setFieldError(hotelNameField, "Le nom est obligatoire.");
            isValid = false;
        } else if (name.length() < 3 || name.length() > 30) {
            setFieldError(hotelNameField, "Le nom doit contenir entre 3 et 30 caractères.");
            isValid = false;
        } else {
            clearFieldError(hotelNameField);
        }

        // Adresse (3-30)
        String address = addressField.getText().trim();
        if (address.isEmpty()) {
            setFieldError(addressField, "L'adresse est obligatoire.");
            isValid = false;
        } else if (address.length() < 3 || address.length() > 30) {
            setFieldError(addressField, "L'adresse doit contenir entre 3 et 30 caractères.");
            isValid = false;
        } else {
            clearFieldError(addressField);
        }

        // Ville (3-30)
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            setFieldError(cityField, "La ville est obligatoire.");
            isValid = false;
        } else if (city.length() < 3 || city.length() > 30) {
            setFieldError(cityField, "La ville doit contenir entre 3 et 30 caractères.");
            isValid = false;
        } else {
            clearFieldError(cityField);
        }

        // Pays (3-30)
        String country = countryField.getText().trim();
        if (country.isEmpty()) {
            setFieldError(countryField, "Le pays est obligatoire.");
            isValid = false;
        } else if (country.length() < 3 || country.length() > 30) {
            setFieldError(countryField, "Le pays doit contenir entre 3 et 30 caractères.");
            isValid = false;
        } else {
            clearFieldError(countryField);
        }

        // Catégorie : doit être un nombre entre 1 et 5
        String category = categoryField.getText().trim();
        if (category.isEmpty() || !category.matches("\\d+")) {
            setFieldError(categoryField, "La catégorie doit être un nombre.");
            isValid = false;
        } else {
            int catValue = Integer.parseInt(category);
            if (catValue < 1 || catValue > 5) {
                setFieldError(categoryField, "La catégorie doit être comprise entre 1 et 5.");
                isValid = false;
            } else {
                clearFieldError(categoryField);
            }
        }

        // Services (3-30)
        String services = servicesField.getText().trim();
        if (services.isEmpty()) {
            setFieldError(servicesField, "Les services sont obligatoires.");
            isValid = false;
        } else if (services.length() < 3 || services.length() > 30) {
            setFieldError(servicesField, "Les services doivent contenir entre 3 et 30 caractères.");
            isValid = false;
        } else {
            clearFieldError(servicesField);
        }

        // Coordonnées (3-30)
        String coordinates = coordinatesField.getText().trim();
        if (coordinates.isEmpty()) {
            setFieldError(coordinatesField, "Les coordonnées sont obligatoires.");
            isValid = false;
        } else if (coordinates.length() < 3 || coordinates.length() > 30) {
            setFieldError(coordinatesField, "Les coordonnées doivent contenir entre 3 et 30 caractères.");
            isValid = false;
        } else {
            clearFieldError(coordinatesField);
        }

        // Avis (2-50)
        String reviews = reviewsField.getText().trim();
        if (reviews.isEmpty()) {
            setFieldError(reviewsField, "Les avis sont obligatoires.");
            isValid = false;
        } else if (reviews.length() < 2 || reviews.length() > 50) {
            setFieldError(reviewsField, "Les avis doivent contenir entre 2 et 50 caractères.");
            isValid = false;
        } else {
            clearFieldError(reviewsField);
        }

        saveButton.setDisable(!isValid);
        return isValid;
    }

    private void setFieldError(TextField field, String message) {
        field.setStyle("-fx-border-color: red;");
        field.setTooltip(new Tooltip(message));
    }

    private void setFieldError(TextArea field, String message) {
        field.setStyle("-fx-border-color: red;");
        field.setTooltip(new Tooltip(message));
    }

    private void clearFieldError(TextField field) {
        field.setStyle("");
        field.setTooltip(null);
    }

    private void clearFieldError(TextArea field) {
        field.setStyle("");
        field.setTooltip(null);
    }

    @FXML
    private void handleSaveHotel() {
        try {
            if (!validateForm()) {
                return;
            }

            Hotel hotel = new Hotel(
                    hotelNameField.getText().trim(),
                    addressField.getText().trim(),
                    cityField.getText().trim(),
                    countryField.getText().trim(),
                    Integer.parseInt(categoryField.getText().trim()),
                    servicesField.getText().trim(),
                    coordinatesField.getText().trim(),
                    reviewsField.getText().trim()
            );

            hotelService.ajouter(hotel);
            showConfirmation("Hôtel créé avec succès !");
            closeForm();
            dashBoardController.navigateTo("dashboard/hotel/hotel-grid.fxml");
        } catch (Exception e) {
            System.err.println("Error saving hotel: " + e.getMessage());
            showError("Erreur lors de la création de l'hôtel !");
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        closeForm();
    }

    private void clearForm() {
        hotelNameField.clear();
        addressField.clear();
        cityField.clear();
        countryField.clear();
        categoryField.clear();
        servicesField.clear();
        coordinatesField.clear();
        reviewsField.clear();
    }

    private void closeForm() {
        if (stage != null) {
            stage.close();
        }
    }

    private void showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
