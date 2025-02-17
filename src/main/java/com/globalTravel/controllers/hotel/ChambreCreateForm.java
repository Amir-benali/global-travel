package com.globalTravel.controllers.hotel;

import com.globalTravel.models.hotel.Chambre;
import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.services.hotel.ChambreService;
import com.globalTravel.services.hotel.HotelService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ChambreCreateForm {

    @FXML private Label formTitleLabel;
    @FXML private TextField typeField;
    @FXML private TextField priceField;
    @FXML private DatePicker availabilityField;
    @FXML private TextField optionsField;
    @FXML private ComboBox<Hotel> hotelComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private ChambreService chambreService = new ChambreService();
    private HotelService hotelService = new HotelService();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        System.out.println("Initializing ChambreCreateForm...");
        loadHotels();

        // Désactiver le bouton de sauvegarde au départ
        saveButton.setDisable(true);

        // Validation en temps réel sur chaque champ
        typeField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        priceField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        availabilityField.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        optionsField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        hotelComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());

        // Pour le prix : n'autoriser que des chiffres
        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                priceField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void loadHotels() {
        ObservableList<Hotel> hotels = FXCollections.observableArrayList(hotelService.rechercher());
        hotelComboBox.setItems(hotels);
    }

    private void validateForm() {
        boolean isValid = true;

        // Validation pour typeField : 2-50 caractères, lettres et espaces seulement
        String typeText = typeField.getText();
        if (typeText.isEmpty()) {
            setFieldError(typeField, "Le type est obligatoire.");
            isValid = false;
        } else if (typeText.length() < 2 || typeText.length() > 50) {
            setFieldError(typeField, "Le type doit contenir entre 2 et 50 caractères.");
            isValid = false;
        } else if (!typeText.matches("[A-Za-zÀ-ÖØ-öø-ÿ\\s]+")) {
            setFieldError(typeField, "Le type ne peut contenir que des lettres et des espaces.");
            isValid = false;
        } else {
            clearFieldError(typeField);
        }

        // Validation pour priceField : doit être numérique et compris entre 2 et 50
        String priceText = priceField.getText();
        if (priceText.isEmpty() || !priceText.matches("\\d+")) {
            setFieldError(priceField, "Le prix doit être un nombre positif.");
            isValid = false;
        } else {
            int priceValue = Integer.parseInt(priceText);
            if (priceValue < 2 || priceValue > 50) {
                setFieldError(priceField, "Le prix doit être compris entre 2 et 50.");
                isValid = false;
            } else {
                clearFieldError(priceField);
            }
        }

        // Validation pour availabilityField : non nul
        if (availabilityField.getValue() == null) {
            setFieldError(availabilityField, "La date de disponibilité est obligatoire.");
            isValid = false;
        } else {
            clearFieldError(availabilityField);
        }

        // Validation pour optionsField : 2-50 caractères, autorise lettres, chiffres, espaces, virgules, points et tirets
        String optionsText = optionsField.getText();
        if (optionsText.isEmpty()) {
            setFieldError(optionsField, "Les options sont obligatoires.");
            isValid = false;
        } else if (optionsText.length() < 2 || optionsText.length() > 50) {
            setFieldError(optionsField, "Les options doivent contenir entre 2 et 50 caractères.");
            isValid = false;
        } else if (!optionsText.matches("[A-Za-z0-9 ,.\\-]+")) {
            setFieldError(optionsField, "Les options contiennent des caractères non autorisés.");
            isValid = false;
        } else {
            clearFieldError(optionsField);
        }

        // Validation pour hotelComboBox : un hôtel doit être sélectionné
        if (hotelComboBox.getSelectionModel().isEmpty()) {
            setFieldError(hotelComboBox, "Veuillez sélectionner un hôtel.");
            isValid = false;
        } else {
            clearFieldError(hotelComboBox);
        }

        // Activer/désactiver le bouton de sauvegarde
        saveButton.setDisable(!isValid);
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
    private void handleSaveChambre() {
        try {
            Hotel selectedHotel = hotelComboBox.getSelectionModel().getSelectedItem();
            Chambre chambre = new Chambre(
                    typeField.getText(),
                    Integer.parseInt(priceField.getText()),
                    availabilityField.getValue(),
                    optionsField.getText(),
                    selectedHotel
            );

            chambreService.ajouter(chambre);
            showAlert("Succès", "Chambre créée avec succès !");
            closeForm();
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de la chambre : " + e.getMessage());
            showAlert("Erreur", "Une erreur s'est produite lors de la création de la chambre.");
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
