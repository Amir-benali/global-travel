package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.activity.Activity;
import com.globalTravel.services.activity.ActivityService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ActivityGrid implements Navigatable {
    private DashBoard dashBoardController;
    private final ActivityService activityService = new ActivityService(); // Ajout du service

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private FlowPane activitiesGrid;

    @FXML
    public void initialize() {
        loadActivities();
    }

    private void loadActivities() {
        List<Activity> activities = activityService.rechercher(); // Correction de la méthode
        activitiesGrid.getChildren().clear();

        for (Activity activity : activities) {
            VBox activityCard = createActivityCard(activity);
            activitiesGrid.getChildren().add(activityCard);
        }
    }

    private VBox createActivityCard(Activity activity) {
        VBox card = new VBox(15);
        card.getStyleClass().add("activity-offer-card");
        card.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 8px; -fx-shadow: 2 2 10 rgba(0, 0, 0, 0.1); -fx-padding: 15;");

        VBox activityInfo = new VBox(10);
        activityInfo.getStyleClass().add("activity-info");

        // Labels and styling
        Label activityNameLabel = createStyledLabel("Activity Name: " + activity.getNomActivity(), "activity-title");
        Label descriptionLabel = createStyledLabel("Description: " + activity.getDescription(), "activity-description");
        Label localisationLabel = createStyledLabel("Location: " + activity.getLocalisation(), "activity-localisation");
        Label startDateLabel = createStyledLabel("Start Date: " + formatDate(activity.getDateDebut()), "activity-start-date");
        Label startTimeLabel = createStyledLabel("Start Time: " + formatTime(activity.getDateDebut()), "activity-start-time");
        Label endDateLabel = createStyledLabel("End Date: " + formatDate(activity.getDateFin()), "activity-end-date");
        Label endTimeLabel = createStyledLabel("End Time: " + formatTime(activity.getDateFin()), "activity-end-time");
        Label priceLabel = createStyledLabel("Price: $" + activity.getPrixTotal(), "activity-price");
        Label typeLabel = createStyledLabel("Type: " + activity.getTypeActivity(), "activity-type");

        // IDs
        Label hotelIdLabel = createStyledLabel("Hotel ID: " + activity.getJoinHotelId(), "activity-hotel-id");
        Label carIdLabel = createStyledLabel("Car ID: " + activity.getJoinVoitureId(), "activity-car-id");
        Label flightIdLabel = createStyledLabel("Flight ID: " + activity.getJoinVolsId(), "activity-flight-id");

        // Action buttons
        Button updateButton = createStyledButton("Update Activity", e -> {
            try {
                navigateToUpdateActivity(activity);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        Button deleteButton = createStyledButton("Delete", e -> confirmDelete(activity));

        HBox buttonHbox = new HBox(15);
        buttonHbox.getChildren().addAll(updateButton, deleteButton);

        activityInfo.getChildren().addAll(
                activityNameLabel, descriptionLabel, localisationLabel,
                startDateLabel, startTimeLabel, endDateLabel, endTimeLabel,
                priceLabel, typeLabel, hotelIdLabel, carIdLabel, flightIdLabel, buttonHbox
        );

        card.getChildren().addAll(activityInfo);
        return card;
    }

    // Helper method to create a stylish label
    private Label createStyledLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setStyle("-fx-font-family: 'Lora', serif; -fx-font-size: 16px; -fx-font-weight: 400; -fx-line-spacing: 1.5; -fx-text-fill: #2C3E50;");
        return label;
    }

    // Helper method to create a stylish button
    private Button createStyledButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setOnAction(action);
        button.setStyle("-fx-background-color: #1E88E5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 25px; -fx-font-family: 'Roboto', sans-serif;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 25px;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: #1E88E5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 25px;"));
        return button;
    }

    // Confirmation dialog for deletion
    private void confirmDelete(Activity activity) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Activity");
        alert.setHeaderText("Are you sure you want to delete this activity?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            deleteActivity(activity);
            loadActivities(); // Reload activities after deletion
        }
    }

    private void deleteActivity(Activity activity) {
        activityService.supprimer(activity); // Correction de la méthode de suppression
        System.out.println("Deleted: " + activity);
    }

    private void navigateToUpdateActivity(Activity activity) throws IOException {
        dashBoardController.navigateTo("dashboard/activity/activity-update-form.fxml");
        ((ActivityUpdateForm)dashBoardController.getController()).initialize(activity);
    }

    public void addActivity() {
        dashBoardController.navigateTo("dashboard/activity/activity-create-form.fxml");
    }

    // Formatage de la date
    private String formatDate(java.util.Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    // Formatage de l'heure
    private String formatTime(java.util.Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    public void navigateToReview(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/activity/review-grid.fxml");
    }
}
