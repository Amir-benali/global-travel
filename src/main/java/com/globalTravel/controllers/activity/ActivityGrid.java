package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.user.User;
import com.globalTravel.services.activity.ActivityService;
import com.globalTravel.services.user.UserService;
import com.globalTravel.utils.DataSource;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActivityGrid implements Navigatable, FrontNavigatable {
    private ObservableList<Activity> activities = FXCollections.observableArrayList();
    private ObservableList<User> users = FXCollections.observableArrayList();

    @FXML private Button btnAddActivity;

    private DashBoard dashBoardController;
    private final ActivityService activityService = new ActivityService();
    private final Connection connection = DataSource.getInstance().getConnection(); // Connexion à la base de données

    @FXML
    private FlowPane activitiesGrid;

    @FXML
    private TextField searchField; // Ajout du champ de recherche
    private FrontOffice frontOfficeController;

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

        // Update button visibility based on the context (front office or back office)
        updateButtonVisibility();
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
        FontAwesomeIconView nameIcon = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR); // Icône FontAwesome pour le nom
        nameIcon.setSize("20px");
        nameIcon.setFill(Color.GRAY); // Couleur bleue
        Label nameLabel = new Label(activity.getNomActivity());
        nameLabel.getStyleClass().add("card-title-actt");
        nameBox.getChildren().addAll(nameIcon, nameLabel);

        // Description
        HBox descriptionBox = new HBox(10);
        descriptionBox.setAlignment(Pos.CENTER_LEFT);
        FontAwesomeIconView descriptionIcon = new FontAwesomeIconView(FontAwesomeIcon.INFO_CIRCLE); // Icône FontAwesome pour la description
        descriptionIcon.setSize("20px");
        nameIcon.setFill(Color.GRAY); // Couleur bleue
        Label descriptionLabel = new Label(activity.getDescription());
        descriptionLabel.getStyleClass().add("card-text-actt");
        descriptionBox.getChildren().addAll(descriptionIcon, descriptionLabel);

        // Localisation
        HBox locationBox = new HBox(10);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        FontAwesomeIconView locationIcon = new FontAwesomeIconView(FontAwesomeIcon.MAP_MARKER); // Icône FontAwesome pour la localisation
        locationIcon.setSize("20px");
        nameIcon.setFill(Color.GRAY); // Couleur bleue
        Label locationLabel = new Label(activity.getLocalisation());
        locationLabel.getStyleClass().add("card-text-actt");
        locationBox.getChildren().addAll(locationIcon, locationLabel);

        // Date et heure de début
        HBox startDateBox = new HBox(10);
        startDateBox.setAlignment(Pos.CENTER_LEFT);
        FontAwesomeIconView startDateIcon = new FontAwesomeIconView(FontAwesomeIcon.CLOCK_ALT); // Icône FontAwesome pour la date de début
        startDateIcon.setSize("20px");
        nameIcon.setFill(Color.GRAY); // Couleur bleue
        Label startDateLabel = new Label("Start Date: " + formatDate(activity.getDateDebut()));
        startDateLabel.getStyleClass().add("card-text-actt");
        startDateBox.getChildren().addAll(startDateIcon, startDateLabel);

        // Date et heure de fin
        HBox endDateBox = new HBox(10);
        endDateBox.setAlignment(Pos.CENTER_LEFT);
        FontAwesomeIconView endDateIcon = new FontAwesomeIconView(FontAwesomeIcon.CLOCK_ALT); // Icône FontAwesome pour la date de fin
        endDateIcon.setSize("20px");
        nameIcon.setFill(Color.GRAY); // Couleur bleue
        Label endDateLabel = new Label("End Date: " + formatDate(activity.getDateFin()));
        endDateLabel.getStyleClass().add("card-text-actt");
        endDateBox.getChildren().addAll(endDateIcon, endDateLabel);

        // Prix
        HBox priceBox = new HBox(10);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        FontAwesomeIconView priceIcon = new FontAwesomeIconView(FontAwesomeIcon.DOLLAR); // Icône FontAwesome pour le prix
        priceIcon.setSize("20px");
        nameIcon.setFill(Color.GRAY); // Couleur bleue
        Label priceLabel = new Label("Price: $" + activity.getPrixTotal());
        priceLabel.getStyleClass().add("card-text-actt");
        priceBox.getChildren().addAll(priceIcon, priceLabel);

        // Type d'activité
        HBox typeBox = new HBox(10);
        typeBox.setAlignment(Pos.CENTER_LEFT);
        FontAwesomeIconView typeIcon = new FontAwesomeIconView(FontAwesomeIcon.TAG); // Icône FontAwesome pour le type d'activité
        typeIcon.setSize("20px");
        nameIcon.setFill(Color.GRAY); // Couleur bleue
        Label typeLabel = new Label("Type: " + activity.getTypeActivity());
        typeLabel.getStyleClass().add("card-text-actt");
        typeBox.getChildren().addAll(typeIcon, typeLabel);

        // Hôtel
        HBox hotelBox = new HBox(10);
        hotelBox.setAlignment(Pos.CENTER_LEFT);
        FontAwesomeIconView hotelIcon = new FontAwesomeIconView(FontAwesomeIcon.HOTEL); // Icône FontAwesome pour l'hôtel
        hotelIcon.setSize("20px");
        nameIcon.setFill(Color.GRAY); // Couleur bleue
        Label hotelLabel = new Label("Hotel: " + getHotelNameById(activity.getJoinHotelId()));
        hotelLabel.getStyleClass().add("card-text-actt");
        hotelBox.getChildren().addAll(hotelIcon, hotelLabel);

        // Voiture
        HBox carBox = new HBox(10);
        carBox.setAlignment(Pos.CENTER_LEFT);
        FontAwesomeIconView carIcon = new FontAwesomeIconView(FontAwesomeIcon.CAR); // Icône FontAwesome pour la voiture
        carIcon.setSize("20px");
        nameIcon.setFill(Color.GRAY); // Couleur bleue
        Label carLabel = new Label("Car: " + getCarBrandById(activity.getJoinVoitureId()));
        carLabel.getStyleClass().add("card-text-actt");
        carBox.getChildren().addAll(carIcon, carLabel);

        // Numéro de vol
        HBox flightBox = new HBox(10);
        flightBox.setAlignment(Pos.CENTER_LEFT);
        FontAwesomeIconView flightIcon = new FontAwesomeIconView(FontAwesomeIcon.PLANE); // Icône FontAwesome pour le vol
        flightIcon.setSize("20px");
        nameIcon.setFill(Color.GRAY); // Couleur bleue
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
        }, "#0080ff", "white"); // Blue background with white text

        Button deleteButton = createStyledButton("Delete", e -> confirmDelete(activity), "#F44336", "white"); // Red background with white text

        // Nouveaux boutons pour accepter et annuler
        Button acceptButton = createStyledButton("Accepter", e -> acceptActivity(activity), "#4CAF50", "white"); // Green background with white text
        Button cancelButton = createStyledButton("Annuler", e -> cancelActivity(activity), "#FF0000", "white"); // Orange background with white text

        HBox buttonHbox = new HBox(15);
        buttonHbox.getChildren().addAll(updateButton, deleteButton, acceptButton, cancelButton);

        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(
                nameBox, descriptionBox, locationBox,
                startDateBox, endDateBox, priceBox,
                typeBox, hotelBox, carBox, flightBox,
                buttonHbox
        );

        return card;
    }

    private void updateButtonVisibility() {
        for (Node node : activitiesGrid.getChildren()) {
            if (node instanceof VBox) {
                VBox card = (VBox) node;
                for (Node child : card.getChildren()) {
                    if (child instanceof HBox) {
                        HBox buttonBox = (HBox) child;
                        // Find the Update, Delete, Accept, and Cancel buttons by their text
                        Button updateButton = (Button) buttonBox.getChildren().stream()
                                .filter(btn -> btn instanceof Button && "Update Activity".equals(((Button) btn).getText()))
                                .findFirst()
                                .orElse(null);

                        Button deleteButton = (Button) buttonBox.getChildren().stream()
                                .filter(btn -> btn instanceof Button && "Delete".equals(((Button) btn).getText()))
                                .findFirst()
                                .orElse(null);
//
//                        Button acceptButton = (Button) buttonBox.getChildren().stream()
//                                .filter(btn -> btn instanceof Button && "Accepter".equals(((Button) btn).getText()))
//                                .findFirst()
//                                .orElse(null);
//
//                        Button cancelButton = (Button) buttonBox.getChildren().stream()
//                                .filter(btn -> btn instanceof Button && "Annuler".equals(((Button) btn).getText()))
//                                .findFirst()
//                                .orElse(null);

                        // If in front office mode, hide the buttons
                        if (frontOfficeController != null) {
                            if (updateButton != null) {
                                updateButton.setVisible(false);
                            }
                            if (deleteButton != null) {
                                deleteButton.setVisible(false);
                            }
//                            if (acceptButton != null) {
//                                acceptButton.setVisible(false);
//                            }
//                            if (cancelButton != null) {
//                                cancelButton.setVisible(false);
//                            }
                        } else {
                            // If in back office mode, ensure the buttons are visible
                            if (updateButton != null) {
                                updateButton.setVisible(true);
                            }
                            if (deleteButton != null) {
                                deleteButton.setVisible(true);
                            }
//                            if (acceptButton != null) {
//                                acceptButton.setVisible(true);
//                            }
//                            if (cancelButton != null) {
//                                cancelButton.setVisible(true);
//                            }
                        }
                    }
                }
            }
        }
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

    private void acceptActivity(Activity activity) {
        // Afficher la boîte de dialogue pour sélectionner les utilisateurs
        showUserSelectionDialog(activity);

        // Change le style du bouton "Accepter" pour indiquer que l'activité est acceptée
        for (Node node : activitiesGrid.getChildren()) {
            if (node instanceof VBox) {
                VBox card = (VBox) node;
                // Vérifier si cette carte correspond à l'activité sélectionnée
                if (card.getChildren().stream().anyMatch(child -> {
                    if (child instanceof HBox) {
                        HBox hbox = (HBox) child;
                        return hbox.getChildren().stream().anyMatch(grandChild -> {
                            if (grandChild instanceof Label) {
                                Label label = (Label) grandChild;
                                return label.getText().equals(activity.getNomActivity());
                            }
                            return false;
                        });
                    }
                    return false;
                })) {
                    // Trouver le bouton "Accepter" dans cette carte
                    for (Node child : card.getChildren()) {
                        if (child instanceof HBox) {
                            HBox buttonBox = (HBox) child;
                            Button acceptButton = (Button) buttonBox.getChildren().stream()
                                    .filter(btn -> btn instanceof Button && "Accepter".equals(((Button) btn).getText()))
                                    .findFirst()
                                    .orElse(null);

                            if (acceptButton != null) {
                                // Changer le style du bouton "Accepter"
                                acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 25px;");
                                acceptButton.setText("Accepté");
                                acceptButton.setDisable(true); // Désactive le bouton après acceptation
                            }
                        }
                    }
                    break; // Sortir de la boucle après avoir trouvé l'activité
                }
            }
        }
    }

    // Méthode pour annuler l'acceptation d'une activité
    private void cancelActivity(Activity activity) {
        // Implémentez la logique pour annuler l'acceptation de l'activité
        System.out.println("Acceptation annulée pour: " + activity.getNomActivity());
        // Vous pouvez mettre à jour l'état de l'activité dans la base de données ici

        // Trouver la carte de l'activité spécifique
        for (Node node : activitiesGrid.getChildren()) {
            if (node instanceof VBox) {
                VBox card = (VBox) node;
                // Vérifier si cette carte correspond à l'activité sélectionnée
                if (card.getChildren().stream().anyMatch(child -> {
                    if (child instanceof HBox) {
                        HBox hbox = (HBox) child;
                        return hbox.getChildren().stream().anyMatch(grandChild -> {
                            if (grandChild instanceof Label) {
                                Label label = (Label) grandChild;
                                return label.getText().equals(activity.getNomActivity());
                            }
                            return false;
                        });
                    }
                    return false;
                })) {
                    // Trouver le bouton "Accepter" dans cette carte
                    for (Node child : card.getChildren()) {
                        if (child instanceof HBox) {
                            HBox buttonBox = (HBox) child;
                            Button acceptButton = (Button) buttonBox.getChildren().stream()
                                    .filter(btn -> btn instanceof Button && "Accepté".equals(((Button) btn).getText()))
                                    .findFirst()
                                    .orElse(null);

                            if (acceptButton != null) {
                                // Changer le style du bouton "Accepter" pour revenir à l'état initial
                                acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 25px;");
                                acceptButton.setText("Accepter");
                                acceptButton.setDisable(false); // Réactive le bouton après annulation
                            }
                        }
                    }
                    break; // Sortir de la boucle après avoir trouvé l'activité
                }
            }
        }
    }

    public void addActivity() {
        dashBoardController.navigateTo("dashboard/activity/activity-create-form.fxml");
    }

    public void navigateToReview(ActionEvent actionEvent) {
        if (frontOfficeController != null) {
            frontOfficeController.navigateTo("dashboard/activity/review-grid.fxml");
        } else {
            dashBoardController.navigateTo("dashboard/activity/review-grid.fxml");
        }
    }


    // Méthode pour afficher une liste d'utilisateurs (clients) lors de l'acceptation d'une activité
    // Méthode pour afficher une liste d'utilisateurs (clients) lors de l'acceptation d'une activité
    private void showUserSelectionDialog(Activity activity) {
        // Récupérer la liste des utilisateurs (clients) depuis la base de données
        UserService userService = new UserService();
        List<User> users = userService.rechercher(); // Récupérer tous les utilisateurs

        // Filtrer les utilisateurs pour n'afficher que ceux ayant le rôle "USER"
        List<User> userRoleUsers = users.stream()
                .filter(user -> user.getRoles().toLowerCase().contains("employee")) // Filtrer par rôle "USER"
                .collect(Collectors.toList());

        // Créer une boîte de dialogue pour afficher la liste des utilisateurs
        Dialog<List<User>> dialog = new Dialog<>();
        dialog.setTitle("Sélectionner des clients pour l'activité");
        dialog.setHeaderText("Choisissez un ou plusieurs clients pour l'activité : " + activity.getNomActivity());

        // Créer une ListView pour afficher les utilisateurs
        ListView<User> userListView = new ListView<>();
        userListView.getItems().addAll(userRoleUsers); // Ajouter les utilisateurs filtrés
        userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // Permettre la sélection multiple

        // Définir un cellFactory pour personnaliser l'affichage des utilisateurs
        userListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Créer un HBox pour afficher l'icône et le nom de l'utilisateur
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    // Ajouter une icône FontAwesome pour l'utilisateur
                    FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER);
                    userIcon.setSize("16px");
                    userIcon.setFill(Color.web("#4CAF50")); // Couleur verte pour l'icône

                    // Ajouter le nom de l'utilisateur
                    Label nameLabel = new Label(user.getFirstName() + " " + user.getLastName());
                    nameLabel.setStyle("-fx-font-family: 'Roboto', sans-serif; -fx-font-size: 14px; -fx-text-fill: #2C3E50;");

                    // Ajouter l'icône et le nom à l'HBox
                    hbox.getChildren().addAll(userIcon, nameLabel);

                    // Définir le contenu de la cellule
                    setGraphic(hbox);
                }
            }
        });

        // Appliquer un style CSS à la ListView
        userListView.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #dddddd; -fx-border-radius: 5px;");

        // Créer un bouton pour valider la sélection
        ButtonType confirmButtonType = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Ajouter la ListView à la boîte de dialogue
        dialog.getDialogPane().setContent(userListView);

        // Appliquer un style CSS à la boîte de dialogue
        dialog.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-radius: 5px;");

        // Gérer l'action de confirmation
        dialog.setResultConverter(buttonType -> {
            if (buttonType == confirmButtonType) {
                // Récupérer les utilisateurs sélectionnés
                return new ArrayList<>(userListView.getSelectionModel().getSelectedItems());
            }
            return null; // Retourner null si l'utilisateur clique sur "Annuler" ou la croix (X)
        });

        // Afficher la boîte de dialogue et récupérer les utilisateurs sélectionnés
        Optional<List<User>> result = dialog.showAndWait();
        result.ifPresent(selectedUsers -> {
            // Associer les utilisateurs sélectionnés à l'activité dans la base de données
            ActivityService activityService = new ActivityService();
            for (User user : selectedUsers) {
                activityService.associateUserToActivity(user.getId(), activity.getId());
            }
            System.out.println("Utilisateurs associés à l'activité " + activity.getNomActivity() + " avec succès.");
        });
    }

    // Méthode pour accepter une activité
    @FXML



    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
        updateButtonVisibility();
        btnAddActivity.setVisible(false);
    }
}