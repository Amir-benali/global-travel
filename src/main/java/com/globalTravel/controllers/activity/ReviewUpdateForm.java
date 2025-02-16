package com.globalTravel.controllers.activity;

import com.globalTravel.services.activity.ReviewService;  // Utilisation de ReviewService au lieu de ActivityService
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.globalTravel.models.activity.Review;
import java.util.List;

public class ReviewUpdateForm {

    @FXML
    private TextField commentaireField;

    @FXML
    private ComboBox<Integer> noteComboBox;

    @FXML
    private ComboBox<Integer> activityIdComboBox; // ComboBox pour les IDs des activités

    @FXML
    private DatePicker dateReviewPicker;

    @FXML
    private Button saveButton;

    private final ReviewService reviewService = new ReviewService();  // Utilisation du service ReviewService

    private Review reviewToUpdate;

    @FXML
    public void initialize(Review review) {
        // Charger les notes possibles (de 0 à 5)
        noteComboBox.setItems(FXCollections.observableArrayList(0, 1, 2, 3, 4, 5));

        // Charger les Activity IDs depuis la base de données
        loadActivityIds();
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
            // Pré-remplir les champs avec les valeurs de la review existante
            commentaireField.setText(review.getCommentaire());
            noteComboBox.setValue(review.getNote());
            activityIdComboBox.setValue(review.getActivityId());
            dateReviewPicker.setValue(review.getDateReview().toLocalDate()); // Affichage de la date, mais non modifiable
        }
    }

    @FXML
    private void handleSaveReview() {
        if (reviewToUpdate == null) {
            showAlert("Erreur", "Aucune review à mettre à jour.");
            return;
        }

        String commentaire = commentaireField.getText().trim();
        Integer note = noteComboBox.getValue();
        Integer selectedActivityId = activityIdComboBox.getValue();

        if (commentaire.isEmpty()) {
            showAlert("Erreur", "Le commentaire ne peut pas être vide.");
            return;
        }

        if (note == null) {
            showAlert("Erreur", "Veuillez sélectionner une note.");
            return;
        }

        if (selectedActivityId == null || !reviewService.activityExists(selectedActivityId)) {
            showAlert("Erreur", "Veuillez sélectionner une activité valide.");
            return;
        }

        // Appliquer les modifications à la revue
        reviewToUpdate.setCommentaire(commentaire);
        reviewToUpdate.setNote(note);
        reviewToUpdate.setActivityId(selectedActivityId);

        // Sauvegarder les modifications via le service ReviewService
        reviewService.modifier(reviewToUpdate);
        showAlert("Succès", "La review a été mise à jour avec succès.");
    }

    private void showAlert(String title, String message) {
        // Afficher une alerte d'information ou d'erreur
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Alerte d'information
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        // Fermer la fenêtre ou annuler l'édition
        // Stage stage = (Stage) saveButton.getScene().getWindow();
        // stage.close();
    }
}
