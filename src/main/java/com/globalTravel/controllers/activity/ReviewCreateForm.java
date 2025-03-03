package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.Review;
import com.globalTravel.services.activity.ReviewService;
import com.globalTravel.services.activity.ActivityService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewCreateForm implements Navigatable {
    private DashBoard dashBoardController;
    private Stage stage;

    @FXML private TextArea commentaireField;
    @FXML private Rating noteRating;
    @FXML private ComboBox<String> activityIdComboBox; // Changé pour afficher des noms
    @FXML private Button saveButton;
    @FXML private Label statusLabel;

    private final ReviewService reviewService = new ReviewService();
    private final ActivityService activityService = new ActivityService();
    private Map<String, Integer> activityNameToIdMap = new HashMap<>(); // Pour associer les noms aux IDs
    private int userId;

    // Méthode pour définir le Stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Méthode pour définir le DashBoardController
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
        this.userId=this.dashBoardController.getCurrentUser().getId();
    }

    @FXML
    public void initialize() {
        // Initialiser le Rating avec un maximum de 5 étoiles
        noteRating.setMax(5);
        noteRating.setRating(0); // Par défaut, aucune étoile sélectionnée

        // Remplir dynamiquement le ComboBox des noms d'activités
        loadActivityNames();
    }

    private void loadActivityNames() {
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
        String selectedActivityName = activityIdComboBox.getValue(); // Récupérer le nom sélectionné
        int activityId = activityNameToIdMap.get(selectedActivityName); // Récupérer l'ID correspondant

        return new Review(
                commentaireField.getText(),
                (int) noteRating.getRating(), // Récupérer la note sous forme d'entier
                activityId,
                userId

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
        // Effacer le formulaire
        clearForm();

        // Fermer la fenêtre si le stage est initialisé
        if (stage != null) {
            stage.close();
        } else {
            System.out.println("Stage n'est pas initialisé.");
        }

        // Naviguer vers le tableau de bord si le contrôleur est initialisé
        if (dashBoardController != null) {
            dashBoardController.navigateTo("dashboard/activity/review-grid.fxml");
        } else {
            System.out.println("DashBoardController n'est pas initialisé.");
        }
    }

    private void clearForm() {
        commentaireField.clear();
        noteRating.setRating(0); // Réinitialiser le rating à 0 étoiles
        activityIdComboBox.setValue(null);
    }

    private void closeForm() {
        // Fermer la fenêtre si le stage est initialisé
        if (stage != null) {
            stage.close();
        } else {
            System.out.println("Stage n'est pas initialisé.");
        }
    }
}