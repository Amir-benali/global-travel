package com.globalTravel.controllers.hotel;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.hotel.Chambre;
import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.services.hotel.ChambreService;
import com.globalTravel.services.hotel.HotelService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ChambreUpdateForm implements Navigatable {

    @FXML private Label formTitleLabel;
    @FXML private TextField typeField;
    @FXML private TextField priceField;
    @FXML private DatePicker availabilityField;
    @FXML private TextField optionsField;
    @FXML private ComboBox<Hotel> hotelComboBox;
    @FXML private Button saveButton;

    private Chambre chambreToEdit;
    private Stage stage;
    private ChambreService chambreService = new ChambreService();
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
     * Initialisation de la fenêtre avec la chambre à modifier.
     */
    public void initialize(Chambre chambreToEdit) {
        System.out.println("Initializing ChambreUpdateForm...");
        if (chambreToEdit != null) {
            this.chambreToEdit = chambreToEdit;
            loadHotels();
            populateForm();
            addListeners(); // Ajoute les écouteurs pour la validation en temps réel
        } else {
            System.out.println("Aucune chambre à modifier n'a été fournie.");
        }
    }

    private void loadHotels() {
        ObservableList<Hotel> hotels = FXCollections.observableArrayList(hotelService.rechercher());
        hotelComboBox.setItems(hotels);
        hotelComboBox.setCellFactory(comboBox -> new ListCell<>() {
            @Override
            protected void updateItem(Hotel hotel, boolean empty) {
                super.updateItem(hotel, empty);
                setText(empty || hotel == null ? null : hotel.getNom_h());
            }
        });
        hotelComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Hotel hotel, boolean empty) {
                super.updateItem(hotel, empty);
                setText(empty || hotel == null ? null : hotel.getNom_h());
            }
        });
    }

    private void populateForm() {
        System.out.println("Populating form with chambre data...");
        typeField.setText(chambreToEdit.getType_chambre_h());
        priceField.setText(String.valueOf(chambreToEdit.getPrix_nuit_h()));
        availabilityField.setValue(chambreToEdit.getDispo_h());
        optionsField.setText(chambreToEdit.getOption_h());
        // Sélectionner l'hôtel associé dans la ComboBox
        Hotel currentHotel = chambreToEdit.getid_hotel_j();
        hotelComboBox.getSelectionModel().select(currentHotel);
    }

    private void addListeners() {
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

    /**
     * Valide les champs du formulaire.
     * Vérifie notamment :
     * - Le type doit contenir entre 2 et 50 caractères et uniquement des lettres et espaces.
     * - Le prix doit être numérique et compris entre 2 et 50.
     * - La date de disponibilité ne doit pas être nulle.
     * - Les options doivent contenir entre 2 et 50 caractères et autoriser lettres, chiffres, espaces, virgules, points et tirets.
     * - Un hôtel doit être sélectionné.
     */
    private void validateForm() {
        boolean isValid = true;

        // Validation pour typeField
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

        // Validation pour priceField : numérique et valeur entre 2 et 50
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

        // Validation pour availabilityField
        if (availabilityField.getValue() == null) {
            setFieldError(availabilityField, "La date de disponibilité est obligatoire.");
            isValid = false;
        } else {
            clearFieldError(availabilityField);
        }

        // Validation pour optionsField
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

        // Validation pour hotelComboBox
        if (hotelComboBox.getSelectionModel().isEmpty()) {
            setFieldError(hotelComboBox, "Veuillez sélectionner un hôtel.");
            isValid = false;
        } else {
            clearFieldError(hotelComboBox);
        }

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

    /**
     * Vérifie à nouveau la validité du formulaire avant de lancer l'enregistrement.
     */
    private boolean validateFormFields() {
        validateForm();
        return !saveButton.isDisabled();
    }

    @FXML
    private void handleSaveChambre() {
        try {
            if (!validateFormFields()) {
                return;
            }
            Hotel selectedHotel = hotelComboBox.getSelectionModel().getSelectedItem();
            Chambre updatedChambre = new Chambre(
                    chambreToEdit.getId_Chambre_h(),
                    typeField.getText(),
                    Integer.parseInt(priceField.getText()),
                    availabilityField.getValue(),
                    optionsField.getText(),
                    selectedHotel
            );
            chambreService.modifier(updatedChambre);
            System.out.println("Chambre mise à jour avec succès !");
            showAlert("Succès", "Chambre mise à jour avec succès !");
            dashBoardController.navigateTo("dashboard/hotel/chambre-grid.fxml");
            closeForm();
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de la chambre : " + e.getMessage());
            showAlert("Erreur", "Une erreur s'est produite lors de la mise à jour de la chambre.");
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
