package com.globalTravel.controllers.hotel;

import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.services.hotel.HotelService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class HotelUpdateForm {

    // Champs FXML correspondant aux éléments du formulaire
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

    // Variables pour gérer l'hôtel à modifier et la fenêtre
    private Hotel hotelToEdit;
    private Stage stage;
    private HotelService hotelService = new HotelService();

    // Méthode pour définir la fenêtre (stage)
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Méthode d'initialisation du formulaire
    public void initialize(Hotel hotelToEdit) {
        System.out.println("Initializing HotelUpdateForm...");

        // Configuration du Spinner pour la catégorie (1 à 5 étoiles)
        SpinnerValueFactory<Integer> categoryFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3);
        categorySpinner.setValueFactory(categoryFactory);

        if (hotelToEdit != null) {
            this.hotelToEdit = hotelToEdit;
            populateForm(); // Remplir le formulaire avec les données de l'hôtel
        } else {
            System.out.println("Aucun hôtel à modifier n'a été fourni.");
        }
    }

    // Méthode pour remplir le formulaire avec les données de l'hôtel
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

    // Méthode pour gérer l'enregistrement des modifications
    @FXML
    private void handleSaveHotel() {
        try {
            // Vérifier que tous les champs sont remplis
            if (nameField.getText().isEmpty() || addressField.getText().isEmpty() || cityField.getText().isEmpty() ||
                    countryField.getText().isEmpty() || amenitiesField.getText().isEmpty() ||
                    locationField.getText().isEmpty() || reviewField.getText().isEmpty()) {
                showError("Veuillez remplir tous les champs !");
                return;
            }

            // Créer un nouvel objet Hotel avec les données mises à jour
            Hotel updatedHotel = new Hotel(
                    hotelToEdit.getId_hotel_h(), // Garder l'ID de l'hôtel pour la mise à jour
                    nameField.getText(),
                    addressField.getText(),
                    cityField.getText(),
                    countryField.getText(),
                    categorySpinner.getValue(),
                    amenitiesField.getText(),
                    locationField.getText(),
                    reviewField.getText()
            );

            // Appeler le service pour mettre à jour l'hôtel
            hotelService.modifier(updatedHotel);
            System.out.println("Hôtel mis à jour avec succès !");

            // Afficher une confirmation et fermer la fenêtre
            showConfirmation("Hôtel mis à jour avec succès !");
            closeForm();
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
            showError("Erreur lors de la mise à jour !");
        }
    }

    // Méthode pour annuler et fermer le formulaire
    @FXML
    private void handleCancel() {
        closeForm();
    }

    // Méthode pour fermer la fenêtre
    private void closeForm() {
        if (stage != null) {
            stage.close();
        }
    }

    // Méthode pour afficher une boîte de dialogue de confirmation
    private void showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher une boîte de dialogue d'erreur
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}