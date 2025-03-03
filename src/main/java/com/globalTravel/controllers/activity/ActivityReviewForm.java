package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.Review;
import com.globalTravel.services.activity.ReviewService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;

import java.time.LocalDateTime;

public class ActivityReviewForm {
    private FrontOffice frontOfficeController;
    private Stage stage;
    private Activity activity;

    @FXML private TextArea commentaireField;
    @FXML private Rating noteRating;
    @FXML private Button saveButton;
    @FXML private Label statusLabel;

    private final ReviewService reviewService = new ReviewService();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @FXML
    public void initialize() {
        noteRating.setMax(5);
        noteRating.setRating(0); // Par défaut, aucune note sélectionnée
    }

    @FXML
    private void handleSaveReview() {
        try {
            // Validation des champs
            String commentaire = commentaireField.getText().trim();
            int note = (int) noteRating.getRating();
            int activityId = activity.getId();

            // Créer une nouvelle instance de Review
            Review review = new Review(commentaire, note, activityId);

            // Enregistrer la review dans la base de données
            reviewService.ajouter(review);

            // Afficher un message de succès
            showAlert("Succès", "Commentaire et note ajoutés avec succès !", Alert.AlertType.INFORMATION);
            statusLabel.setText("Commentaire et note ajoutés avec succès !");
            statusLabel.setStyle("-fx-text-fill: green;");

            // Fermer le formulaire après l'enregistrement
            closeForm();
        } catch (IllegalArgumentException e) {
            // Gestion des erreurs de validation
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
            statusLabel.setText("Erreur : " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            // Gestion des autres erreurs
            showAlert("Erreur", "Une erreur s'est produite lors de l'enregistrement : " + e.getMessage(), Alert.AlertType.ERROR);
            statusLabel.setText("Erreur : " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    // Méthode pour fermer le formulaire
    private void closeForm() {
        if (stage != null) {
            stage.close();
        }
    }
}