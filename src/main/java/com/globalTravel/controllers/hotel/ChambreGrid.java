package com.globalTravel.controllers.hotel;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.hotel.Chambre;
import com.globalTravel.services.hotel.ChambreService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.List;

public class ChambreGrid implements Navigatable, FrontNavigatable {

    @FXML
    private Button btnAddChamber;

    @FXML
    private FlowPane chambresGrid;

    private DashBoard dashBoardController;
    private FrontOffice frontOfficeController;
    private int hotelId;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    public void initialize() {
        if (chambresGrid == null) {
            System.err.println("chambresGrid n'est pas initialisé !");
        } else {
            loadChambres();
        }
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
        loadChambres();
    }

    private void loadChambres() {
        chambresGrid.getChildren().clear();

        List<Chambre> chambres;
        if (hotelId > 0) {
            chambres = getChambresByHotelId(hotelId);
        } else {
            chambres = getChambres();
        }

        for (Chambre chambre : chambres) {
            VBox chambreCard = createChambreCard(chambre);
            chambresGrid.getChildren().add(chambreCard);
        }
    }

    private List<Chambre> getChambresByHotelId(int hotelId) {
        ChambreService chambreService = new ChambreService();
        return chambreService.rechercherParHotelId(hotelId);
    }

    private VBox createChambreCard(Chambre chambre) {
        VBox card = new VBox(15); // Espacement entre les éléments
        card.getStyleClass().add("chambre-card");
        card.setPadding(new Insets(20)); // Padding interne
        card.setStyle(
                "-fx-background-color: #ffffff;" + // Fond blanc
                        "-fx-border-color: #cccccc;" + // Bordure grise
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 10px;" + // Coins arrondis
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);" // Ombre légère
        );

        // Titre de la carte (type de chambre)
        Label typeLabel = new Label(chambre.getType_chambre_h());
        typeLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #333333;"
        );

        // Icône de lit
        FontAwesomeIconView bedIcon = new FontAwesomeIconView(FontAwesomeIcon.BED);
        bedIcon.setFill(Color.DARKORANGE);
        bedIcon.setGlyphSize(24);
        typeLabel.setGraphic(bedIcon);

        // Grille pour les détails de la chambre
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15); // Espacement horizontal
        detailsGrid.setVgap(10); // Espacement vertical
        detailsGrid.setPadding(new Insets(10));

        // Ajout des icônes et des informations
        detailsGrid.add(createIcon(FontAwesomeIcon.DOLLAR, Color.GREEN), 0, 0);
        detailsGrid.add(new Label("Prix: " + chambre.getPrix_nuit_h() + "€/nuit"), 1, 0);

        detailsGrid.add(createIcon(FontAwesomeIcon.CALENDAR, Color.BLUE), 0, 1);
        detailsGrid.add(new Label("Disponibilité: " + chambre.getDispo_h()), 1, 1);

        detailsGrid.add(createIcon(FontAwesomeIcon.LIST, Color.ORANGE), 0, 2);
        detailsGrid.add(new Label("Options: " + chambre.getOption_h()), 1, 2);

        // Boutons en bas de la carte
        HBox buttonBox = new HBox(10); // Espacement entre les boutons
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        if (frontOfficeController == null) {
            // Bouton de mise à jour
            Button updateButton = new Button("Modifier");
            updateButton.setStyle(
                    "-fx-background-color: #4CAF50;" + // Fond vert
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-padding: 8px 16px;" +
                            "-fx-border-radius: 5px;"
            );
            updateButton.setOnAction(e -> {
                try {
                    navigateToUpdateChambre(chambre);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            // Bouton de suppression
            Button deleteButton = new Button("Supprimer");
            deleteButton.setStyle(
                    "-fx-background-color: #ff4444;" + // Fond rouge
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-padding: 8px 16px;" +
                            "-fx-border-radius: 5px;"
            );
            deleteButton.setOnAction(e -> deleteChambre(chambre));

            buttonBox.getChildren().addAll(updateButton, deleteButton);
        } else {
            // Bouton de réservation
            Button reserveButton = new Button("Réserver");
            reserveButton.setStyle(
                    "-fx-background-color: #2196F3;" + // Fond bleu
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-padding: 8px 16px;" +
                            "-fx-border-radius: 5px;"
            );
            reserveButton.setOnAction(e -> navigateToReservation(chambre));

            buttonBox.getChildren().add(reserveButton);
        }

        // Ajout des éléments à la carte
        card.getChildren().addAll(typeLabel, detailsGrid, buttonBox);

        // Effet au survol
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #f9f9f9;" + // Fond légèrement gris au survol
                        "-fx-border-color: #999999;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        ));

        return card;
    }

    private FontAwesomeIconView createIcon(FontAwesomeIcon icon, Color color) {
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setGlyphSize(20);
        iconView.setFill(color);
        return iconView;
    }

    private void navigateToReservation(Chambre chambre) {
        if (frontOfficeController != null) {
            frontOfficeController.navigateTo("dashboard/hotel/hotel-reservations.fxml");
        if (dashBoardController != null)
            dashBoardController.navigateTo("dashboard/hotel/hotel-reservations.fxml");
        }

    }

    private void deleteChambre(Chambre chambre) {
        if (chambre == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please select a chambre to delete.");
            errorAlert.showAndWait();
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText("Delete Chambre?");
        confirmationAlert.setContentText("Are you sure you want to delete the chambre: " + chambre.getType_chambre_h() + "?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    ChambreService chambreService = new ChambreService();
                    chambreService.supprimer(chambre);
                    System.out.println("Chambre deleted successfully: " + chambre);
                    loadChambres();
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Chambre deleted successfully!");
                    successAlert.showAndWait();
                } catch (Exception e) {
                    System.err.println("Error deleting chambre: " + e.getMessage());
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Error deleting chambre!");
                    errorAlert.showAndWait();
                }
            }
        });
    }

    private void navigateToUpdateChambre(Chambre chambre) throws IOException {
        dashBoardController.navigateTo("dashboard/hotel/chambre-update-form.fxml");
        ((ChambreUpdateForm) dashBoardController.getController()).initialize(chambre);
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
        if (chambresGrid != null) {
            loadChambres();
        } else {
            System.err.println("chambresGrid n'est pas initialisé !");
        }
        btnAddChamber.setVisible(false);
    }

    private List<Chambre> getChambres() {
        ChambreService chambreService = new ChambreService();
        return chambreService.rechercher();
    }

    public void addChambre(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/hotel/chambre-create-form.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}