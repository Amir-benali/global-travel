package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.services.activity.ReviewService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.globalTravel.models.activity.Review;
import org.controlsfx.control.Rating;

import java.util.List;

public class ReviewUpdateForm implements Navigatable  {

    @FXML
    private TextArea commentaireField;

    @FXML
    private Rating noteRating; // Utilisation de Rating au lieu de ComboBox

    @FXML
    private ComboBox<Integer> activityIdComboBox; // ComboBox pour les IDs des activités

    @FXML
    private DatePicker dateReviewPicker;

    @FXML
    private Button saveButton;

    private final ReviewService reviewService = new ReviewService();  // Utilisation du service ReviewService

    private Review reviewToUpdate;
    private DashBoard dashBoardController;

    @FXML
    public void initialize() {
        // Configurer le Rating pour afficher jusqu'à 5 étoiles
        noteRating.setMax(5);

        // Charger les Activity IDs depuis la base de données
        loadActivityIds();
    }
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    private void loadActivityIds() {
        // Charger les IDs des activités depuis la base de données via le service
        List<Integer> activityIds = reviewService.getAllActivityIds();  // Appeler la méthode de ReviewService
        ObservableList<Integer> observableActivityIds = FXCollections.observableArrayList(activityIds);
        activityIdComboBox.setItems(observableActivityIds);
    }

    public void setReviewToUpdate(Review review) {
        this.reviewToUpdate = review;
        if (review != null) {

            commentaireField.setText(review.getCommentaire());
            noteRating.setRating(review.getNote());
            activityIdComboBox.setValue(review.getActivityId());
            dateReviewPicker.setValue(review.getDateReview().toLocalDate());
        }
    }

    @FXML
    private void handleSaveReview() {
        if (reviewToUpdate == null) {
            showAlert("Erreur", "Aucune review à mettre à jour.", Alert.AlertType.ERROR);
            return;
        }

        // Validation des champs obligatoires
        if (!validateInputs()) {
            return;
        }

        // Appliquer les modifications à la review
        reviewToUpdate.setCommentaire(commentaireField.getText().trim());
        reviewToUpdate.setNote((int) noteRating.getRating()); // Récupérer la note du Rating
        reviewToUpdate.setActivityId(activityIdComboBox.getValue());

        // Sauvegarder les modifications via le service ReviewService
        reviewService.modifier(reviewToUpdate);

        // Afficher un message de succès
        showAlert("Succès", "La review a été mise à jour avec succès.", Alert.AlertType.INFORMATION);
    }

    // Méthode pour valider les champs de saisie
    private boolean validateInputs() {
        // Vérifier que le commentaire n'est pas vide
        if (commentaireField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le commentaire ne peut pas être vide.", Alert.AlertType.WARNING);
            return false;
        }

        // Vérifier qu'une note est sélectionnée
        if (noteRating.getRating() == 0) {
            showAlert("Erreur", "Veuillez sélectionner une note.", Alert.AlertType.WARNING);
            return false;
        }

        // Vérifier qu'un ID d'activité est sélectionné
        if (activityIdComboBox.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une activité.", Alert.AlertType.WARNING);
            return false;
        }

        // Vérifier que l'ID d'activité existe dans la base de données
        if (!reviewService.activityExists(activityIdComboBox.getValue())) {
            showAlert("Erreur", "L'ID de l'activité sélectionné n'existe pas dans la base de données.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
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
        dashBoardController.navigateTo("dashboard/activity/review-grid.fxml");

    }
}