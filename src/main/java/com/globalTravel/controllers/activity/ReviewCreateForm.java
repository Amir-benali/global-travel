package com.globalTravel.controllers.activity;

import com.globalTravel.models.activity.Review;
import com.globalTravel.services.activity.ReviewService;
import com.globalTravel.services.activity.ActivityService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;

import java.util.List;

public class ReviewCreateForm {

    @FXML private TextField commentaireField;
    @FXML private Rating noteRating; // Utilisation de Rating au lieu de ComboBox
    @FXML private ComboBox<Integer> activityIdComboBox;
    @FXML private Button saveButton;
    @FXML private Label statusLabel;

    private final ReviewService reviewService = new ReviewService();
    private final ActivityService activityService = new ActivityService();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Initialiser le Rating avec un maximum de 5 étoiles
        noteRating.setMax(5);
        noteRating.setRating(0); // Par défaut, aucune étoile sélectionnée

        // Remplir dynamiquement le ComboBox des IDs d'activités
        loadActivityIds();
    }

    private void loadActivityIds() {
        List<Integer> activityIds = reviewService.getAllActivityIds();
        ObservableList<Integer> observableActivityIds = FXCollections.observableArrayList(activityIds);
        activityIdComboBox.setItems(observableActivityIds);
    }

    @FXML
    private void handleSaveReview() {
        try {
            if (!validateInputs()) {
                return;
            }

            Review review = createReviewFromInputs();
            reviewService.ajouter(review);

            showAlert("Succès", "Critique ajoutée avec succès !", Alert.AlertType.INFORMATION);
            statusLabel.setText("Critique ajoutée avec succès !");
            statusLabel.setStyle("-fx-text-fill: green;");
            clearForm();
            closeForm();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'enregistrement : " + e.getMessage(), Alert.AlertType.ERROR);
            statusLabel.setText("Erreur : " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private Review createReviewFromInputs() {
        return new Review(
                commentaireField.getText(),
                (int) noteRating.getRating(), // Récupérer la note sous forme d'entier
                activityIdComboBox.getValue()
        );
    }

    private boolean validateInputs() {
        if (commentaireField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le commentaire ne peut pas être vide.", Alert.AlertType.WARNING);
            return false;
        }

        if (noteRating.getRating() == 0) {
            showAlert("Erreur", "Veuillez sélectionner une note.", Alert.AlertType.WARNING);
            return false;
        }

        if (activityIdComboBox.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une activité.", Alert.AlertType.WARNING);
            return false;
        }

        if (!reviewService.activityExists(activityIdComboBox.getValue())) {
            showAlert("Erreur", "L'ID de l'activité sélectionné n'existe pas dans la base de données.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        clearForm();
        closeForm();
    }

    private void clearForm() {
        commentaireField.clear();
        noteRating.setRating(0); // Réinitialiser le rating à 0 étoiles
        activityIdComboBox.setValue(null);
    }

    private void closeForm() {
        if (stage != null) {
            stage.close();
        }
    }
}