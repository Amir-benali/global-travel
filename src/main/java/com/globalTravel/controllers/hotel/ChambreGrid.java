package com.globalTravel.controllers.hotel;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.hotel.Chambre;
import com.globalTravel.services.hotel.ChambreService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class ChambreGrid implements Navigatable {
    private DashBoard dashBoardController;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private FlowPane chambresGrid;

    private List<Chambre> chambres;

    @FXML
    public void initialize() {
        loadChambres();
    }

    private void loadChambres() {
        chambresGrid.getChildren().clear();
        chambres = getChambres();

        for (Chambre chambre : chambres) {
            VBox chambreCard = createChambreCard(chambre);
            chambresGrid.getChildren().add(chambreCard);
        }
    }

    private VBox createChambreCard(Chambre chambre) {
        VBox card = new VBox(10);
        card.getStyleClass().add("chambre-offer-card");

        VBox chambreInfo = new VBox(5);
        chambreInfo.getStyleClass().add("chambre-info");

        // Chambre details
        Label typeLabel = new Label("Type: " + chambre.getType_chambre_h());
        typeLabel.getStyleClass().add("chambre-type");

        Label priceLabel = new Label("Price: " + chambre.getPrix_nuit_h());
        priceLabel.getStyleClass().add("chambre-price");

        Label availabilityLabel = new Label("Availability: " + chambre.getDispo_h());
        availabilityLabel.getStyleClass().add("chambre-availability");

        Label optionsLabel = new Label("Options: " + chambre.getOption_h());
        optionsLabel.getStyleClass().add("chambre-options");

        // Buttons
        Button updateButton = new Button("Update Chambre");
        updateButton.setOnAction(e -> {
            try {
                navigateToUpdateChambre(chambre);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        updateButton.getStyleClass().add("view-details-button");

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("view-details-button");
        deleteButton.setOnAction(e -> deleteChambre(chambre));

        HBox buttonHbox = new HBox(3);
        buttonHbox.getChildren().addAll(updateButton, deleteButton);
        chambreInfo.getChildren().addAll(typeLabel, priceLabel, availabilityLabel, optionsLabel, buttonHbox);

        card.getChildren().add(chambreInfo);

        return card;
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

    private List<Chambre> getChambres() {
        ChambreService chambreService = new ChambreService();
        return chambreService.rechercher();
    }

    public void addChambre(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/hotel/chambre-create-form.fxml");
    }
}