package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.activity.Review;
import com.globalTravel.services.activity.ReviewService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.Rating;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ReviewGrid implements Navigatable, FrontNavigatable {
    @FXML private Button btnAddReview;
    private DashBoard dashBoardController;
    private final ReviewService reviewService = new ReviewService(); // Service des reviews
    private FrontOffice frontOfficeController;

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

        // Mettre à jour la visibilité des boutons en fonction du contexte (front office ou back office)
        updateButtonVisibility();
    }

    private VBox createReviewCard(Review review) {
        VBox card = new VBox(15);
        card.getStyleClass().add("review-card");
        card.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 8px; -fx-shadow: 2 2 10 rgba(0, 0, 0, 0.1); -fx-padding: 15;");

        VBox reviewInfo = new VBox(10);
        reviewInfo.getStyleClass().add("review-info");

        // Afficher le nom et le prénom de l'utilisateur
        Label userLabel = new Label("Avis de : " + review.getUserPrenom() + " " + review.getUserNom());
        userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        reviewInfo.getChildren().add(userLabel);

        // Commentaire avec icône FontAwesome
        FontAwesomeIconView commentIcon = new FontAwesomeIconView(FontAwesomeIcon.COMMENT);
        commentIcon.setSize("16px");
        commentIcon.setFill(Color.web("#2C3E50")); // Couleur de l'icône

        HBox commentBox = new HBox(10);
        commentBox.setAlignment(Pos.CENTER_LEFT);
        Label commentaireLabel = createStyledLabel(review.getCommentaire(), "review-commentaire");
        commentBox.getChildren().addAll(commentIcon, commentaireLabel);

        // Affichage de la note sous forme d'étoiles
        Rating noteRating = new Rating();
        noteRating.setRating(review.getNote()); // Définir la note
        noteRating.setMax(5); // Maximum de 5 étoiles
        noteRating.setDisable(true); // Désactiver l'interaction utilisateur

        // Date de revue avec icône FontAwesome
        FontAwesomeIconView dateIcon = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR);
        dateIcon.setSize("16px");
        dateIcon.setFill(Color.web("#2C3E50")); // Couleur de l'icône

        HBox dateBox = new HBox(10);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        Label dateReviewLabel = createStyledLabel("Date de revue: " + formatDate(review.getDateReview()), "review-date");
        dateBox.getChildren().addAll(dateIcon, dateReviewLabel);

        // Bouton "Modifier" avec icône FontAwesome
        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        editIcon.setSize("16px");
        editIcon.setFill(Color.WHITE);

        Button updateButton = new Button("Modifier", editIcon);
        updateButton.setOnAction(e -> {
            try {
                navigateToUpdateReview(review); // Méthode pour naviguer vers le formulaire de modification
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        updateButton.setStyle("-fx-background-color: #0080ff; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 25px;");

        // Bouton "Supprimer" avec icône FontAwesome
        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
        deleteIcon.setSize("16px");
        deleteIcon.setFill(Color.WHITE);

        Button deleteButton = new Button("Supprimer", deleteIcon);
        deleteButton.setOnAction(e -> confirmDelete(review)); // Méthode pour confirmer la suppression
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 25px;");

        // Conteneur pour les boutons
        HBox buttonHbox = new HBox(15);
        buttonHbox.getChildren().addAll(updateButton, deleteButton);

        // Ajouter tous les éléments à la carte
        reviewInfo.getChildren().addAll(
                commentBox, noteRating, dateBox, buttonHbox
        );

        card.getChildren().addAll(reviewInfo);
        return card;
    }

    private void updateButtonVisibility() {
        for (Node node : reviewsGrid.getChildren()) {
            if (node instanceof VBox) {
                VBox card = (VBox) node;
                for (Node child : card.getChildren()) {
                    if (child instanceof VBox) {
                        VBox reviewInfo = (VBox) child;
                        for (Node nestedChild : reviewInfo.getChildren()) {
                            if (nestedChild instanceof HBox) {
                                HBox buttonBox = (HBox) nestedChild;
                                // Trouver les boutons "Modifier" et "Supprimer" par leur texte
                                Button updateButton = (Button) buttonBox.getChildren().stream()
                                        .filter(btn -> btn instanceof Button && "Modifier".equals(((Button) btn).getText()))
                                        .findFirst()
                                        .orElse(null);

                                Button deleteButton = (Button) buttonBox.getChildren().stream()
                                        .filter(btn -> btn instanceof Button && "Supprimer".equals(((Button) btn).getText()))
                                        .findFirst()
                                        .orElse(null);

                                // Si en mode front office, masquer les boutons
                                if (frontOfficeController != null) {
                                    if (updateButton != null) {
                                        updateButton.setVisible(false);
                                    }
                                    if (deleteButton != null) {
                                        deleteButton.setVisible(false);
                                    }
                                } else {
                                    // Si en mode back office, s'assurer que les boutons sont visibles
                                    if (updateButton != null) {
                                        updateButton.setVisible(true);
                                    }
                                    if (deleteButton != null) {
                                        deleteButton.setVisible(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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

    // Méthode pour assombrir la couleur (effet hover)
    private String darkenColor(String color) {
        // Vous pouvez implémenter une logique pour assombrir la couleur, par exemple en réduisant la luminosité
        return "#1565C0"; // Exemple : Nuance plus foncée de bleu
    }

    // Boîte de dialogue de confirmation pour la suppression
    private void confirmDelete(Review review) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
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
        if (frontOfficeController != null) {
            frontOfficeController.navigateTo("dashboard/activity/review-grid.fxml");
        } else {
            dashBoardController.navigateTo("dashboard/activity/review-grid.fxml");
        }
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
        updateButtonVisibility();
        btnAddReview.setVisible(false);
    }
}