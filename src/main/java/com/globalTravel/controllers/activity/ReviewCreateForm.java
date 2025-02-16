package com.globalTravel.controllers.activity;

import com.globalTravel.models.activity.Review;
import com.globalTravel.services.activity.ReviewService;
import com.globalTravel.services.activity.ActivityService; // Import du service ActivityService
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class ReviewCreateForm {

    @FXML private TextField commentaireField;
    @FXML private ComboBox<Integer> noteComboBox;
    @FXML private ComboBox<Integer> activityIdComboBox; // Utilisation du ComboBox pour les ID d'activités
    @FXML private Button saveButton;
    @FXML private Label statusLabel;

    private final ReviewService reviewService = new ReviewService();
    private final ActivityService activityService = new ActivityService(); // Service pour récupérer les activités
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Remplir le ComboBox de la note avec des valeurs de 0 à 5
        noteComboBox.getItems().setAll(0, 1, 2, 3, 4, 5);

        // Remplir dynamiquement le ComboBox des IDs d'activités avec les valeurs provenant du service ActivityService
        loadActivityIds();
    }

    private void loadActivityIds() {
        // Charger les IDs des activités depuis la base de données via le service
        List<Integer> activityIds = reviewService.getAllActivityIds();  // Appeler la méthode de ReviewService
        ObservableList<Integer> observableActivityIds = FXCollections.observableArrayList(activityIds);
        activityIdComboBox.setItems(observableActivityIds);
    }

    @FXML
    private void handleSaveReview() {
        try {
            if (validateInputs()) {
                Review review = createReviewFromInputs();

                // Utiliser la méthode ajouter du ReviewService pour ajouter la critique
                reviewService.ajouter(review);

                statusLabel.setText("Critique ajoutée avec succès !");
                statusLabel.setStyle("-fx-text-fill: green;");
                clearForm();
                closeForm();
            }
        } catch (Exception e) {
            statusLabel.setText("Erreur lors de l'enregistrement : " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private Review createReviewFromInputs() {
        return new Review(
                commentaireField.getText(),
                noteComboBox.getValue(),
                activityIdComboBox.getValue() // Utilisation de l'ID d'activité sélectionné dans le ComboBox
        );
    }

    private boolean validateInputs() {
        if (commentaireField.getText().isEmpty()) {
            statusLabel.setText("Le commentaire est requis.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return false;
        }
        if (noteComboBox.getValue() == null) {
            statusLabel.setText("La note est requise.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return false;
        }
        if (activityIdComboBox.getValue() == null) {
            statusLabel.setText("L'ID de l'activité est requis.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return false;
        }
        return true;
    }

    @FXML
    private void handleCancel() {
        clearForm();
        closeForm();
    }

    private void clearForm() {
        commentaireField.clear();
        noteComboBox.setValue(null);
        activityIdComboBox.setValue(null); // Effacer le ComboBox de l'ID d'activité
    }

    private void closeForm() {
        if (stage != null) {
            stage.close();
        }
    }
}
