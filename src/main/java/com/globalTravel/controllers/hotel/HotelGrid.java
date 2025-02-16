package com.globalTravel.controllers.hotel;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.services.hotel.HotelService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The HotelGrid class represents a user interface component that displays a grid of hotels
 * and allows users to interact with hotel data. It implements the Navigatable interface, providing
 * navigation capabilities within the application.
 *
 * Responsibilities of this class include:
 * - Loading and displaying a list of hotels in a grid format.
 * - Creating individual hotel cards with hotel details and actions such as update, view, and delete.
 * - Navigating to other views or forms within the application for updating or adding hotels.
 */
public class HotelGrid implements Navigatable {
    private DashBoard dashBoardController;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private FlowPane hotelsGrid;

    private List<Hotel> hotels;

    @FXML
    public void initialize() {
        loadHotels();
    }

    private void loadHotels() {
        hotelsGrid.getChildren().clear();
        hotels = getHotels();

        for (Hotel hotel : hotels) {
            VBox hotelCard = createHotelCard(hotel);
            hotelsGrid.getChildren().add(hotelCard);
        }
    }

    private VBox createHotelCard(Hotel hotel) {
        VBox card = new VBox(10);
        card.getStyleClass().add("hotel-offer-card");

        VBox hotelInfo = new VBox(5);
        hotelInfo.getStyleClass().add("hotel-info");

        // Hotel details
        Label nameLabel = new Label("Hotel: " + hotel.getNom_h());
        nameLabel.getStyleClass().add("hotel-title");

        Label addressLabel = new Label("Address: " + hotel.getAdresse_h());
        addressLabel.getStyleClass().add("hotel-address");

        Label cityLabel = new Label("City: " + hotel.getVille_h());
        cityLabel.getStyleClass().add("hotel-city");

        Label categoryLabel = new Label("Category: " + hotel.getCategorie_h() + " Stars");
        categoryLabel.getStyleClass().add("hotel-category");

        // Buttons
        Button updateButton = new Button("Update Hotel");
        updateButton.setOnAction(e -> {
            try {
                navigateToUpdateHotel(hotel);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        updateButton.getStyleClass().add("view-details-button");

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("view-details-button");
        deleteButton.setOnAction(e -> deleteHotel(hotel));

        HBox buttonHbox = new HBox(3);
        buttonHbox.getChildren().addAll(updateButton, deleteButton);
        hotelInfo.getChildren().addAll(nameLabel, addressLabel, cityLabel, categoryLabel, buttonHbox);

        card.getChildren().add(hotelInfo);

        return card;
    }

    private void deleteHotel(Hotel hotel) {
        if (hotel == null) {
            // Afficher une boîte de dialogue d'erreur directement
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Veuillez sélectionner un hôtel à supprimer.");
            errorAlert.showAndWait();
            return;
        }

        // Confirmation avant suppression
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Supprimer l'hôtel ?");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'hôtel : " + hotel.getNom_h() + " ?");

        // Attendre la réponse de l'utilisateur
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Appeler le service pour supprimer l'hôtel
                    HotelService hotelService = new HotelService();
                    hotelService.supprimer(hotel);
                    System.out.println("Hôtel supprimé avec succès : " + hotel);
                    loadHotels();
                    // Afficher une boîte de dialogue de confirmation directement
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Succès");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("L'hôtel a été supprimé avec succès !");
                    successAlert.showAndWait();

                    // Rafraîchir la liste des hôtels (si nécessaire)
                    // refreshHotelList();
                } catch (Exception e) {
                    System.err.println("Erreur lors de la suppression de l'hôtel : " + e.getMessage());

                    // Afficher une boîte de dialogue d'erreur directement
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Erreur lors de la suppression de l'hôtel !");
                    errorAlert.showAndWait();
                }
            }
        });
    }

    private void navigateToUpdateHotel(Hotel hotel) throws IOException {
        dashBoardController.navigateTo("dashboard/hotel/hotel-update-form.fxml");
        ((HotelUpdateForm) dashBoardController.getController()).initialize(hotel);
    }

    private List<Hotel> getHotels() {
        HotelService hotelService = new HotelService();
        return hotelService.rechercher();
    }


    /**
     * Navigates to the hotel form view to add a new hotel.
     *
     * @param actionEvent the action event that triggered this method
     */
    public void addHotel(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/hotel/hotel-create-form.fxml");
    }

    public void navigateToChambre(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/hotel/chambre-grid.fxml");
    }
}