package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.activity.Activity;
import com.globalTravel.services.activity.ActivityService;
import com.globalTravel.utils.DataSource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ActivityGrid implements Navigatable {
    private DashBoard dashBoardController;
    private final ActivityService activityService = new ActivityService();
    private final Connection connection = DataSource.getInstance().getConnection(); // Connexion à la base de données

    @FXML
    private FlowPane activitiesGrid;

    @FXML
    private TextField searchField; // Ajout du champ de recherche

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    public void initialize() {
        loadActivities();
    }

    private void loadActivities() {
        loadActivities(null); // Charge toutes les activités si aucun filtre n'est appliqué
    }

    private void loadActivities(String searchQuery) {
        List<Activity> activities;
        if (searchQuery == null || searchQuery.isEmpty()) {
            activities = activityService.rechercher(); // Charge toutes les activités
        } else {
            activities = activityService.rechercherParNom(searchQuery); // Charge les activités filtrées
        }
        activitiesGrid.getChildren().clear();

        for (Activity activity : activities) {
            VBox activityCard = createActivityCard(activity);
            activitiesGrid.getChildren().add(activityCard);
        }
    }

    @FXML
    public void searchActivities() {
        String searchQuery = searchField.getText();
        loadActivities(searchQuery); // Charge les activités filtrées par le nom
    }

    private VBox createActivityCard(Activity activity) {
        VBox card = new VBox(10);
        card.getStyleClass().add("activity-card-actt");
        card.setPadding(new Insets(15));

        // Nom de l'activité
        HBox nameBox = new HBox(10);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        ImageView nameIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/activity-name-icon.png")));
        nameIcon.setFitHeight(30);
        nameIcon.setFitWidth(30);
        Label nameLabel = new Label(activity.getNomActivity());
        nameLabel.getStyleClass().add("card-title-actt");
        nameBox.getChildren().addAll(nameIcon, nameLabel);

        // Description
        HBox descriptionBox = new HBox(10);
        descriptionBox.setAlignment(Pos.CENTER_LEFT);
        ImageView descriptionIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/description-icon.png")));
        descriptionIcon.setFitHeight(30);
        descriptionIcon.setFitWidth(30);
        Label descriptionLabel = new Label(activity.getDescription());
        descriptionLabel.getStyleClass().add("card-text-actt");
        descriptionBox.getChildren().addAll(descriptionIcon, descriptionLabel);

        // Localisation
        HBox locationBox = new HBox(10);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        ImageView locationIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/location-icon.png")));
        locationIcon.setFitHeight(30);
        locationIcon.setFitWidth(30);
        Label locationLabel = new Label(activity.getLocalisation());
        locationLabel.getStyleClass().add("card-text-actt");
        locationBox.getChildren().addAll(locationIcon, locationLabel);

        // Date et heure de début
        HBox startDateBox = new HBox(10);
        startDateBox.setAlignment(Pos.CENTER_LEFT);
        ImageView startDateIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/start-date-icon.png")));
        startDateIcon.setFitHeight(30);
        startDateIcon.setFitWidth(30);
        Label startDateLabel = new Label("Start Date: " + formatDate(activity.getDateDebut()));
        startDateLabel.getStyleClass().add("card-text-actt");
        startDateBox.getChildren().addAll(startDateIcon, startDateLabel);

        // Date et heure de fin
        HBox endDateBox = new HBox(10);
        endDateBox.setAlignment(Pos.CENTER_LEFT);
        ImageView endDateIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/end-date-icon.png")));
        endDateIcon.setFitHeight(30);
        endDateIcon.setFitWidth(30);
        Label endDateLabel = new Label("End Date: " + formatDate(activity.getDateFin()));
        endDateLabel.getStyleClass().add("card-text-actt");
        endDateBox.getChildren().addAll(endDateIcon, endDateLabel);

        // Prix
        HBox priceBox = new HBox(10);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        ImageView priceIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/price-icon.png")));
        priceIcon.setFitHeight(30);
        priceIcon.setFitWidth(30);
        Label priceLabel = new Label("Price: $" + activity.getPrixTotal());
        priceLabel.getStyleClass().add("card-text-actt");
        priceBox.getChildren().addAll(priceIcon, priceLabel);

        // Type d'activité
        HBox typeBox = new HBox(10);
        typeBox.setAlignment(Pos.CENTER_LEFT);
        ImageView typeIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/type-icon.png")));
        typeIcon.setFitHeight(30);
        typeIcon.setFitWidth(30);
        Label typeLabel = new Label("Type: " + activity.getTypeActivity());
        typeLabel.getStyleClass().add("card-text-actt");
        typeBox.getChildren().addAll(typeIcon, typeLabel);

        // Hôtel
        HBox hotelBox = new HBox(10);
        hotelBox.setAlignment(Pos.CENTER_LEFT);
        ImageView hotelIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/hotel-icon.png")));
        hotelIcon.setFitHeight(30);
        hotelIcon.setFitWidth(30);
        Label hotelLabel = new Label("Hotel: " + getHotelNameById(activity.getJoinHotelId()));
        hotelLabel.getStyleClass().add("card-text-actt");
        hotelBox.getChildren().addAll(hotelIcon, hotelLabel);

        // Voiture
        HBox carBox = new HBox(10);
        carBox.setAlignment(Pos.CENTER_LEFT);
        ImageView carIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/car-icon.png")));
        carIcon.setFitHeight(30);
        carIcon.setFitWidth(30);
        Label carLabel = new Label("Car: " + getCarBrandById(activity.getJoinVoitureId()));
        carLabel.getStyleClass().add("card-text-actt");
        carBox.getChildren().addAll(carIcon, carLabel);

        // Numéro de vol
        HBox flightBox = new HBox(10);
        flightBox.setAlignment(Pos.CENTER_LEFT);
        ImageView flightIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/flight-icon.png")));
        flightIcon.setFitHeight(30);
        flightIcon.setFitWidth(30);
        Label flightLabel = new Label("Flight: " + getFlightNumberById(activity.getJoinVolsId()));
        flightLabel.getStyleClass().add("card-text-actt");
        flightBox.getChildren().addAll(flightIcon, flightLabel);

        // Boutons d'action
        Button updateButton = createStyledButton("Update Activity", e -> {
            try {
                navigateToUpdateActivity(activity);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }, "#0080ff", "white"); // Green background with white text

        Button deleteButton = createStyledButton("Delete", e -> confirmDelete(activity), "#F44336", "white"); // Red background with white text

        HBox buttonHbox = new HBox(15);
        buttonHbox.getChildren().addAll(updateButton, deleteButton);

        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(
                nameBox, descriptionBox, locationBox,
                startDateBox, endDateBox, priceBox,
                typeBox, hotelBox, carBox, flightBox,
                buttonHbox
        );

        return card;
    }

    private String getHotelNameById(int hotelId) {
        String query = "SELECT nom_h FROM hotel WHERE id_hotel_h = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, hotelId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("nom_h");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du nom de l'hôtel : " + e.getMessage());
        }
        return "N/A";
    }

    private String getCarBrandById(int carId) {
        String query = "SELECT brand FROM private_car WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, carId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("brand");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la marque de la voiture : " + e.getMessage());
        }
        return "N/A";
    }

    private String getFlightNumberById(int flightId) {
        String query = "SELECT flight_number FROM flights WHERE id_flight = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, flightId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("flight_number");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du numéro de vol : " + e.getMessage());
        }
        return "N/A";
    }

    // Helper method to create a stylish button
    private Button createStyledButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action, String backgroundColor, String textColor) {
        Button button = new Button(text);
        button.setOnAction(action);
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

    // Formatage de la date
    private String formatDate(java.util.Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    // Confirmation dialog for deletion
    private void confirmDelete(Activity activity) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Activity");
        alert.setHeaderText("Are you sure you want to delete this activity?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            deleteActivity(activity);
            loadActivities(); // Reload activities after deletion
        }
    }

    // Supprimer une activité
    private void deleteActivity(Activity activity) {
        activityService.supprimer(activity);
        System.out.println("Deleted: " + activity);
    }

    // Naviguer vers le formulaire de mise à jour
    private void navigateToUpdateActivity(Activity activity) throws IOException {
        dashBoardController.navigateTo("dashboard/activity/activity-update-form.fxml");
        ((ActivityUpdateForm) dashBoardController.getController()).initialize(activity);
    }

    public void addActivity() {
        dashBoardController.navigateTo("dashboard/activity/activity-create-form.fxml");
    }

    public void navigateToReview(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/activity/review-grid.fxml");
    }
}