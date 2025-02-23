package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.activity.Review;
import com.globalTravel.services.activity.ReviewService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.Rating;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ReviewGrid implements Navigatable {
    private DashBoard dashBoardController;
    private final ReviewService reviewService = new ReviewService(); // Service des reviews

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private FlowPane reviewsGrid;

    @FXML
    public void initialize() {
        loadReviews(); // Charge les revues dès l'initialisation
    }

    private void loadReviews() {
        List<Review> reviews = reviewService.rechercher(); // Récupère la liste des revues
        reviewsGrid.getChildren().clear(); // Vide le FlowPane avant de charger les nouvelles données

        for (Review review : reviews) {
            VBox reviewCard = createReviewCard(review);
            reviewsGrid.getChildren().add(reviewCard); // Ajoute chaque carte de revue dans le FlowPane
        }
    }

    private VBox createReviewCard(Review review) {
        VBox card = new VBox(15);
        card.getStyleClass().add("review-card");
        card.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 8px; -fx-shadow: 2 2 10 rgba(0, 0, 0, 0.1); -fx-padding: 15;");

        VBox reviewInfo = new VBox(10);
        reviewInfo.getStyleClass().add("review-info");

        // Commentaire avec icône
        HBox commentBox = new HBox(10);
        commentBox.setAlignment(Pos.CENTER_LEFT);
        ImageView commentIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/comment-icon.png")));
        commentIcon.setFitHeight(20);
        commentIcon.setFitWidth(20);
        Label commentaireLabel = createStyledLabel(review.getCommentaire(), "review-commentaire");
        commentBox.getChildren().addAll(commentIcon, commentaireLabel);

        // Affichage de la note sous forme d'étoiles
        Rating noteRating = new Rating();
        noteRating.setRating(review.getNote()); // Définir la note
        noteRating.setMax(5); // Maximum de 5 étoiles
        noteRating.setDisable(true); // Désactiver l'interaction utilisateur

        // Date de revue avec icône
        HBox dateBox = new HBox(10);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        ImageView dateIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/start-date-icon.png")));
        dateIcon.setFitHeight(20);
        dateIcon.setFitWidth(20);
        Label dateReviewLabel = createStyledLabel("Date de revue: " + formatDate(review.getDateReview()), "review-date");
        dateBox.getChildren().addAll(dateIcon, dateReviewLabel);

        // Boutons d'action pour modifier ou supprimer la revue
        Button updateButton = createStyledButton("Modifier", e -> {
            try {
                navigateToUpdateReview(review);
            } catch (IOException ex) {
                ex.printStackTrace(); // Affichage de l'erreur dans la console pour le debug
            }
        }, "#0080ff", "white"); // Green background with white text

        Button deleteButton = createStyledButton("Supprimer", e -> confirmDelete(review), "#F44336", "white"); // Red background with white text

        HBox buttonHbox = new HBox(15);
        buttonHbox.getChildren().addAll(updateButton, deleteButton);

        reviewInfo.getChildren().addAll(
                commentBox, noteRating, dateBox, buttonHbox
        );

        card.getChildren().addAll(reviewInfo);
        return card;
    }

    // Méthode pour créer un label stylisé
    private Label createStyledLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setStyle("-fx-font-family: 'Lora', serif; -fx-font-size: 16px; -fx-font-weight: 400; -fx-line-spacing: 1.5; -fx-text-fill: #2C3E50;");
        return label;
    }

    // Méthode pour créer un bouton stylisé avec des couleurs personnalisées (sans icône)
    private Button createStyledButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action, String backgroundColor, String textColor) {
        Button button = new Button(text);
        button.setOnAction(action);

        // Appliquer le style au bouton
        button.setStyle("-fx-background-color: " + backgroundColor + "; " +
                "-fx-text-fill: " + textColor + "; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10 15; " +
                "-fx-background-radius: 25px; " +
                "-fx-font-family: 'Roboto', sans-serif;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: " + darkenColor(backgroundColor) + "; " +
                "-fx-text-fill: " + textColor + "; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10 15; " +
                "-fx-background-radius: 25px;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: " + backgroundColor + "; " +
                "-fx-text-fill: " + textColor + "; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10 15; " +
                "-fx-background-radius: 25px;"));
        return button;
    }

    // Helper method to darken the color for hover effect
    private String darkenColor(String color) {
        // You can implement a simple logic to darken the color, e.g., by reducing the brightness
        return "#1565C0"; // Example: Darker shade of blue
    }

    // Dialog de confirmation pour la suppression
    private void confirmDelete(Review review) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Supprimer la revue");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette revue ?");
        alert.setContentText("Cette action ne peut pas être annulée.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            deleteReview(review);
            loadReviews(); // Recharge la liste des revues après suppression
        }
    }

    private void deleteReview(Review review) {
        reviewService.supprimer(review); // Appelle le service pour supprimer la revue
        System.out.println("Supprimée: " + review);
    }

    private void navigateToUpdateReview(Review review) throws IOException {
        // Navigation vers le formulaire de mise à jour de la revue
        dashBoardController.navigateTo("dashboard/activity/review-update-form.fxml");

        // Obtention du contrôleur et initialisation de la revue à modifier
        ReviewUpdateForm updateFormController = (ReviewUpdateForm) dashBoardController.getController();
        updateFormController.setReviewToUpdate(review);
    }

    public void addReview() {
        dashBoardController.navigateTo("dashboard/activity/review-create-form.fxml"); // Navigue vers le formulaire de création de revue
    }

    // Formatage de la date
    private String formatDate(java.time.LocalDateTime date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(java.sql.Timestamp.valueOf(date)); // Conversion de LocalDateTime à Timestamp
    }

    public void navigateToReview(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/activity/activity-grid.fxml");
    }
}