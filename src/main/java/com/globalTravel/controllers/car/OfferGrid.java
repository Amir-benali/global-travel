package com.globalTravel.controllers.car;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.car.Offer;
import com.globalTravel.services.car.OfferService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class OfferGrid implements Navigatable {
    private DashBoard dashBoardController;
    private OfferService offerService = new OfferService();
    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private FlowPane offersGrid;

    private List<Offer> offers;

    @FXML
    public void initialize() {
        loadOffers();
    }

    private void loadOffers() {
        offersGrid.getChildren().clear();
        offers = getOffers();

        for (Offer offer : offers) {
            VBox offerCard = createOfferCard(offer);
            offersGrid.getChildren().add(offerCard);
        }
        System.out.println("Offers: " + offers);
    }

    private VBox createOfferCard(Offer offer) {
        VBox card = new VBox(10);
        card.getStyleClass().add("offer-card");

        VBox offerInfo = new VBox(5);
        offerInfo.getStyleClass().add("offer-info");

        // Offer details
        Label descriptionLabel = new Label("Description: " + offer.getDescription());
        descriptionLabel.getStyleClass().add("offer-description");

        Label dateLabel = new Label("Date: " + offer.getDate());
        dateLabel.getStyleClass().add("offer-date");

        Label priceLabel = new Label("Price: " + offer.getPrice());
        priceLabel.getStyleClass().add("offer-price");

        Label routeLabel = new Label("Route: " + offer.getRoute().getId());
        routeLabel.getStyleClass().add("offer-route");

        Label carLabel = new Label("Car: " + offer.getCar().getBrand() + " " + offer.getCar().getModel());
        carLabel.getStyleClass().add("offer-car");

        // Buttons
        Button updateButton = new Button("Update Offer");
        updateButton.setOnAction(e -> {
            try {
                navigateToUpdateOffer(offer);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        updateButton.getStyleClass().add("view-details-button");

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("view-details-button");
        deleteButton.setOnAction(e -> deleteOffer(offer));

        HBox buttonHbox = new HBox(3);
        buttonHbox.getChildren().addAll(updateButton, deleteButton);
        offerInfo.getChildren().addAll(descriptionLabel, dateLabel, priceLabel, routeLabel, carLabel, buttonHbox);

        card.getChildren().addAll(offerInfo);

        return card;
    }

    private void deleteOffer(Offer offer) {
        System.out.println("Deleting: " + offer);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Are you sure you want to delete this offer?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            offerService.supprimer(offer);
            loadOffers();
        }
    }

    private void navigateToUpdateOffer(Offer offer) throws IOException {
        dashBoardController.navigateTo("dashboard/car/offer-update-form.fxml");
        OfferUpdateForm updateForm = (OfferUpdateForm) dashBoardController.getController();
        updateForm.initialize(offer);
    }

    private List<Offer> getOffers() {
        return offerService.rechercher();
    }

    public void addOffer(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/car/offer-create-form.fxml");
    }

    public void navigateToRoute(ActionEvent actionEvent) {
//        dashBoardController.navigateTo("dashboard/offer/route-grid.fxml");
    }

    public void navigateToCar(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/car/car-grid.fxml");
    }

    public void navigateToDriver(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/car/driver-grid.fxml");
    }
}