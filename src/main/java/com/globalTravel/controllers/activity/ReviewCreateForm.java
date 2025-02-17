package com.globalTravel.controllers.activity;

import com.globalTravel.models.activity.Review;
import com.globalTravel.services.activity.ReviewService;
import com.globalTravel.services.activity.ActivityService;
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
            // Validation des champs obligatoires
            if (!validateInputs()) {
                return;
            }

            // Créer la review à partir des inputs
            Review review = createReviewFromInputs();

            // Utiliser la méthode ajouter du ReviewService pour ajouter la critique
            reviewService.ajouter(review);

            // Afficher un message de succès
            showAlert("Succès", "Critique ajoutée avec succès !", Alert.AlertType.INFORMATION);
            statusLabel.setText("Critique ajoutée avec succès !");
            statusLabel.setStyle("-fx-text-fill: green;");
            clearForm();
            closeForm();
        } catch (Exception e) {
            // Afficher un message d'erreur en cas d'échec
            showAlert("Erreur", "Erreur lors de l'enregistrement : " + e.getMessage(), Alert.AlertType.ERROR);
            statusLabel.setText("Erreur : " + e.getMessage());
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

    // Méthode pour valider les champs de saisie
    private boolean validateInputs() {
        // Vérifier que le commentaire n'est pas vide
        if (commentaireField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le commentaire ne peut pas être vide.", Alert.AlertType.WARNING);
            return false;
        }

        // Vérifier qu'une note est sélectionnée
        if (noteComboBox.getValue() == null) {
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