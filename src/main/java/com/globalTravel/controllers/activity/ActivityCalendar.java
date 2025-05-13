package com.globalTravel.controllers.activity;

import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.activity.Activity;
import com.globalTravel.services.activity.ActivityService;
import com.google.api.client.auth.oauth2.Credential;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Date;
import java.util.List;

public class ActivityCalendar implements FrontNavigatable {

    @FXML
    private VBox activityContainer; // VBox pour contenir les cartes d'activités

    @FXML
    private Button calendarButton; // Bouton pour afficher l'agenda

    private FrontOffice frontOfficeController;

    // Méthode pour charger les activités auxquelles l'utilisateur a été invité
    public void loadActivitiesForUser(int userId) {
        ActivityService activityService = new ActivityService();
        List<Activity> activities = activityService.getActivitiesForUser(userId);

        // Vider le conteneur actuel
        activityContainer.getChildren().clear();

        // Créer une carte pour chaque activité
        for (Activity activity : activities) {
            AnchorPane card = createActivityCard(activity);
            activityContainer.getChildren().add(card);
        }

        // Message de débogage pour vérifier les activités chargées
        System.out.println("Activités chargées pour l'utilisateur ID " + userId + " : " + activities.size());
    }

    // Méthode pour créer une carte d'activité
    private AnchorPane createActivityCard(Activity activity) {
        AnchorPane card = new AnchorPane();
        card.setPrefSize(540, 100); // Augmenter la hauteur pour un meilleur espacement
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Ajouter le nom de l'activité avec une icône
        FontAwesomeIconView activityIcon = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR_CHECK_ALT);
        activityIcon.setSize("20px");
        activityIcon.setFill(Color.web("#2196F3"));
        activityIcon.setLayoutX(10);
        activityIcon.setLayoutY(20);
        card.getChildren().add(activityIcon);

        Text activityName = new Text(activity.getNomActivity());
        activityName.setFont(Font.font("Arial", 18));
        activityName.setFill(Color.web("#333333"));
        activityName.setLayoutX(40); // Décaler le texte pour laisser de la place à l'icône
        activityName.setLayoutY(25);
        card.getChildren().add(activityName);

        // Ajouter la date de début avec une icône
        FontAwesomeIconView dateIcon = new FontAwesomeIconView(FontAwesomeIcon.CLOCK_ALT);
        dateIcon.setSize("16px");
        dateIcon.setFill(Color.web("#666666"));
        dateIcon.setLayoutX(10);
        dateIcon.setLayoutY(50);
        card.getChildren().add(dateIcon);

        Text startDate = new Text("Date: " + activity.getDateDebut());
        startDate.setFont(Font.font("Arial", 14));
        startDate.setFill(Color.web("#666666"));
        startDate.setLayoutX(40); // Décaler le texte pour laisser de la place à l'icône
        startDate.setLayoutY(55);
        card.getChildren().add(startDate);

        // Bouton "Ajouter à Google Agenda" avec une icône
        FontAwesomeIconView googleCalendarIcon = new FontAwesomeIconView(FontAwesomeIcon.GOOGLE);
        googleCalendarIcon.setSize("20px");
        googleCalendarIcon.setFill(Color.WHITE);

        Button addToGoogleCalendarButton = new Button();
        addToGoogleCalendarButton.setGraphic(googleCalendarIcon);
        addToGoogleCalendarButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 10;");
        addToGoogleCalendarButton.setLayoutX(380);
        addToGoogleCalendarButton.setLayoutY(20);
        addToGoogleCalendarButton.setOnAction(event -> addActivityToGoogleCalendar(activity));
        addToGoogleCalendarButton.setOnMouseEntered(e -> addToGoogleCalendarButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 10;"));
        addToGoogleCalendarButton.setOnMouseExited(e -> addToGoogleCalendarButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 10;"));
        card.getChildren().add(addToGoogleCalendarButton);

        // Bouton "Show Details" avec une icône
        FontAwesomeIconView detailsIcon = new FontAwesomeIconView(FontAwesomeIcon.INFO_CIRCLE);
        detailsIcon.setSize("20px");
        detailsIcon.setFill(Color.WHITE);

        Button showDetailsButton = new Button();
        showDetailsButton.setGraphic(detailsIcon);
        showDetailsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 10;");
        showDetailsButton.setLayoutX(420);
        showDetailsButton.setLayoutY(20);
        showDetailsButton.setOnAction(event -> showActivityDetails(activity));
        showDetailsButton.setOnMouseEntered(e -> showDetailsButton.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 10;"));
        showDetailsButton.setOnMouseExited(e -> showDetailsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 10;"));
        card.getChildren().add(showDetailsButton);

        // Bouton "Ajouter un commentaire" avec une icône
        FontAwesomeIconView commentIcon = new FontAwesomeIconView(FontAwesomeIcon.COMMENT_ALT);
        commentIcon.setSize("20px");
        commentIcon.setFill(Color.WHITE);

        Button addCommentButton = new Button();
        addCommentButton.setGraphic(commentIcon);
        addCommentButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 10;");
        addCommentButton.setLayoutX(460);
        addCommentButton.setLayoutY(20);
        addCommentButton.setOnAction(event -> openReviewForm(activity));
        addCommentButton.setOnMouseEntered(e -> addCommentButton.setStyle("-fx-background-color: #F57C00; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 10;"));
        addCommentButton.setOnMouseExited(e -> addCommentButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 10;"));
        card.getChildren().add(addCommentButton);

        return card;
    }

    // Méthode pour ouvrir le formulaire de création de commentaire et de note
    private void openReviewForm(Activity activity) {
        try {
            // Assurez-vous que le chemin est correct et correspond à l'emplacement du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/activity/activity-review-form.fxml"));
            Parent root = loader.load();

            ActivityReviewForm controller = loader.getController();
            controller.setActivity(activity);
            controller.setFrontOfficeController(frontOfficeController);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un commentaire et une note");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du formulaire de commentaire.");
        }
    }

    // Méthode pour ajouter l'activité à Google Agenda
    private void addActivityToGoogleCalendar(Activity activity) {
        try {
            // Authentifier l'utilisateur avec Google Calendar
            Credential credential = GoogleCalendarAuth.authorize();
            GoogleCalendarService googleCalendarService = new GoogleCalendarService(credential);

            // Convertir la date de début et de fin de l'activité en objets Date
            Date startDate = activity.getDateDebut(); // Assurez-vous que getDateDebut() retourne un objet Date
            Date endDate = activity.getDateFin(); // Assurez-vous que getDateFin() retourne un objet Date

            // Ajouter l'événement à Google Agenda
            googleCalendarService.addEvent(
                    activity.getNomActivity(), // Nom de l'activité
                    activity.getLocalisation(), // Lieu de l'activité
                    activity.getDescription(), // Description de l'activité
                    startDate, // Date de début
                    endDate // Date de fin
            );

            // Afficher une alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("L'activité a été ajoutée avec succès à Google Agenda !");
            alert.showAndWait();

            System.out.println("Activité ajoutée à Google Agenda : " + activity.getNomActivity());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ajout de l'activité à Google Agenda.");

            // Afficher une alerte d'erreur en cas d'échec
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'ajout de l'activité à Google Agenda.");
            alert.showAndWait();
        }
    }

    // Méthode pour afficher les détails de l'activité dans une nouvelle fenêtre
    private void showActivityDetails(Activity activity) {
        try {
            // Charger la nouvelle vue FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/activity/activity-details.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la nouvelle vue
            ActivityDetailsController controller = loader.getController();
            controller.initData(activity); // Initialiser les données de l'activité

            // Créer une nouvelle scène
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Détails de l'activité");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la vue des détails de l'activité.");
        }
    }

    // Méthode pour afficher l'agenda avec les dates des activités
    private void showCalendar(List<Activity> activities) {
        try {
            // Créer une nouvelle fenêtre pour l'agenda
            Stage stage = new Stage();
            AgendaView agendaView = new AgendaView(activities);
            agendaView.start(stage); // Afficher l'agenda dans la nouvelle fenêtre
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'affichage de l'agenda.");
        }
    }

    // Méthode d'initialisation appelée automatiquement lors du chargement de la vue
    @FXML
    public void initialize() {
        System.out.println("Initialisation de ActivityCalendar..."); // Log de débogage

        // Vérifiez que calendarButton n'est pas null avant de lui ajouter un événement
        if (calendarButton != null) {
            calendarButton.setOnAction(event -> {
                ActivityService activityService = new ActivityService();
                List<Activity> activities = activityService.getActivitiesForUser(frontOfficeController.getCurrentUser().getId());
                showCalendar(activities); // Ouvrir l'agenda dans une nouvelle fenêtre
            });
        } else {
            System.err.println("Erreur : calendarButton est null.");
        }
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
        int currentUserId = this.frontOfficeController.getCurrentUser().getId(); // Récupérer l'ID de l'utilisateur actuel
        loadActivitiesForUser(currentUserId); // Charger les activités pour l'utilisateur actuel
    }
}