package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.Review;
import com.globalTravel.services.activity.ReviewService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;

import java.time.LocalDateTime;

public class ActivityReviewForm implements FrontNavigatable {
    private FrontOffice frontOfficeController;
    private Stage stage;
    private Activity activity;

    @FXML private TextArea commentaireField;
    @FXML private Rating noteRating;
    @FXML private Button saveButton;
    @FXML private Label statusLabel;

    private final ReviewService reviewService = new ReviewService();
    private int userId;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
        this.userId=this.frontOfficeController.getCurrentUser().getId();
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

            // Validation supplémentaire
            if (commentaire.isEmpty()) {
                throw new IllegalArgumentException("Le commentaire ne peut pas être vide.");
            }

            if (note < 1 || note > 5) {
                throw new IllegalArgumentException("La note doit être comprise entre 1 et 5.");
            }

            // Créer une nouvelle instance de Review
            Review review = new Review(commentaire, note, activityId,userId);

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
        // Demander une confirmation avant de fermer le formulaire
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Êtes-vous sûr de vouloir annuler ?");
        alert.setContentText("Toutes les modifications non enregistrées seront perdues.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            closeForm();
        }
    }

    // Méthode pour fermer le formulaire
    private void closeForm() {
        if (stage != null) {
            stage.close();
        }
    }
}