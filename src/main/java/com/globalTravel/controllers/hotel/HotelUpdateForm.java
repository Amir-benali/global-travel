package com.globalTravel.controllers.hotel;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.services.hotel.HotelService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class HotelUpdateForm implements Navigatable {

    @FXML private Label formTitleLabel;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField countryField;
    @FXML private Spinner<Integer> categorySpinner;
    @FXML private TextField amenitiesField;
    @FXML private TextField locationField;
    @FXML private TextArea reviewField;
    @FXML private Button saveButton;

    private Hotel hotelToEdit;
    private Stage stage;
    private HotelService hotelService = new HotelService();
    private DashBoard dashBoardController;
    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initialisation du formulaire avec l'hôtel à modifier.
     */
    public void initialize(Hotel hotelToEdit) {
        System.out.println("Initializing HotelUpdateForm...");
        // Configuration du Spinner pour la catégorie (1 à 5)
        SpinnerValueFactory<Integer> categoryFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3);
        categorySpinner.setValueFactory(categoryFactory);

        if (hotelToEdit != null) {
            this.hotelToEdit = hotelToEdit;
            populateForm();
            addListeners();
        } else {
            System.out.println("Aucun hôtel à modifier n'a été fourni.");
        }
    }

    private void populateForm() {
        System.out.println("Remplissage du formulaire avec les données de l'hôtel...");
        nameField.setText(hotelToEdit.getNom_h());
        addressField.setText(hotelToEdit.getAdresse_h());
        cityField.setText(hotelToEdit.getVille_h());
        countryField.setText(hotelToEdit.getPays_h());
        categorySpinner.getValueFactory().setValue(hotelToEdit.getCategorie_h());
        amenitiesField.setText(hotelToEdit.getServices_h());
        locationField.setText(hotelToEdit.getCoordonnees_h());
        reviewField.setText(hotelToEdit.getAvis_h());
    }

    private void addListeners() {
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        addressField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        cityField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        countryField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        amenitiesField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        locationField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        reviewField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    /**
     * Valide le formulaire :
     * - Les champs nom, adresse, ville, pays, services et coordonnées doivent contenir entre 3 et 30 caractères.
     * - L'avis doit contenir entre 2 et 50 caractères.
     */
    private boolean validateForm() {
        boolean isValid = true;

        // Nom (3-30)
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            setFieldError(nameField, "Le nom est obligatoire.");
            isValid = false;
        } else if (name.length() < 3 || name.length() > 30) {
            setFieldError(nameField, "Le nom doit contenir entre 3 et 30 caractères.");
            isValid = false;
        } else {
            clearFieldError(nameField);
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

        // Services (3-30)
        String amenities = amenitiesField.getText().trim();
        if (amenities.isEmpty()) {
            setFieldError(amenitiesField, "Les services sont obligatoires.");
            isValid = false;
        } else if (amenities.length() < 3 || amenities.length() > 30) {
            setFieldError(amenitiesField, "Les services doivent contenir entre 3 et 30 caractères.");
            isValid = false;
        } else {
            clearFieldError(amenitiesField);
        }

        // Coordonnées (3-30)
        String location = locationField.getText().trim();
        if (location.isEmpty()) {
            setFieldError(locationField, "La localisation est obligatoire.");
            isValid = false;
        } else if (location.length() < 3 || location.length() > 30) {
            setFieldError(locationField, "La localisation doit contenir entre 3 et 30 caractères.");
            isValid = false;
        } else {
            clearFieldError(locationField);
        }

        // Avis (2-50)
        String review = reviewField.getText().trim();
        if (review.isEmpty()) {
            setFieldError(reviewField, "L'avis est obligatoire.");
            isValid = false;
        } else if (review.length() < 2 || review.length() > 50) {
            setFieldError(reviewField, "L'avis doit contenir entre 2 et 50 caractères.");
            isValid = false;
        } else {
            clearFieldError(reviewField);
        }

        saveButton.setDisable(!isValid);
        return isValid;
    }

    private void setFieldError(Control field, String message) {
        field.setStyle("-fx-border-color: red;");
        field.setTooltip(new Tooltip(message));
    }

    private void clearFieldError(Control field) {
        field.setStyle("");
        field.setTooltip(null);
    }

    @FXML
    private void handleSaveHotel() {
        try {
            if (!validateForm()) {
                return;
            }
            // Créer un nouvel objet Hotel avec les données mises à jour
            Hotel updatedHotel = new Hotel(
                    hotelToEdit.getId_hotel_h(),
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    cityField.getText().trim(),
                    countryField.getText().trim(),
                    categorySpinner.getValue(),
                    amenitiesField.getText().trim(),
                    locationField.getText().trim(),
                    reviewField.getText().trim()
            );
            hotelService.modifier(updatedHotel);
            System.out.println("Hôtel mis à jour avec succès !");
            showConfirmation("Hôtel mis à jour avec succès !");
            closeForm();
            dashBoardController.navigateTo("dashboard/hotel/hotel-grid.fxml");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
            showError("Erreur lors de la mise à jour !");
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
