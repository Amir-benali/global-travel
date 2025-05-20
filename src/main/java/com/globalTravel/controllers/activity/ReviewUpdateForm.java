package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.Review;
import com.globalTravel.services.activity.ReviewService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.Rating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewUpdateForm implements Navigatable {

    @FXML
    private TextArea commentaireField;

    @FXML
    private Rating noteRating; // Utilisation de Rating au lieu de ComboBox

    @FXML
    private ComboBox<String> activityIdComboBox; // ComboBox pour les noms des activités

    @FXML
    private DatePicker dateReviewPicker;

    @FXML
    private Button saveButton;

    private final ReviewService reviewService = new ReviewService();  // Utilisation du service ReviewService

    private Review reviewToUpdate;
    private DashBoard dashBoardController;
    private Map<String, Integer> activityNameToIdMap = new HashMap<>(); // Pour associer les noms aux IDs

    @FXML
    public void initialize() {
        // Configurer le Rating pour afficher jusqu'à 5 étoiles
        noteRating.setMax(5);

        // Charger les noms des activités depuis la base de données
        loadActivityIds();
    }

    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    private void loadActivityIds() {
        // Récupérer la liste des activités avec leurs noms
        List<Activity> activities = reviewService.getAllActivities();

        // Créer une liste observable pour les noms des activités
        ObservableList<String> observableActivityNames = FXCollections.observableArrayList();
        for (Activity activity : activities) {
            observableActivityNames.add(activity.getNomActivity());
            activityNameToIdMap.put(activity.getNomActivity(), activity.getId()); // Associer le nom à l'ID
        }

        // Remplir le ComboBox avec les noms des activités
        activityIdComboBox.setItems(observableActivityNames);
    }

    public void setReviewToUpdate(Review review) {
        this.reviewToUpdate = review;
        if (review != null) {
            commentaireField.setText(review.getCommentaire());
            noteRating.setRating(review.getNote());

            // Récupérer le nom de l'activité en fonction de l'activityId
            String nomActivity = reviewService.getNomActivity(review.getActivityId());
            activityIdComboBox.setValue(nomActivity); // Afficher le nom de l'activité

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

        // Récupérer l'ID de l'activité sélectionnée
        String selectedActivityName = activityIdComboBox.getValue(); // Récupérer le nom sélectionné
        int activityId = activityNameToIdMap.get(selectedActivityName); // Récupérer l'ID correspondant

        // Appliquer les modifications à la review
        reviewToUpdate.setCommentaire(commentaireField.getText().trim());
        reviewToUpdate.setNote((int) noteRating.getRating()); // Récupérer la note du Rating
        reviewToUpdate.setActivityId(activityId); // Mettre à jour l'ID de l'activité

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

        // Vérifier qu'une activité est sélectionnée
        if (activityIdComboBox.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une activité.", Alert.AlertType.WARNING);
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