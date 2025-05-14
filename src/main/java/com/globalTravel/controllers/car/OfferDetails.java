package com.globalTravel.controllers.car;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.car.Offer;
import com.globalTravel.models.car.PrivateCar;
import com.globalTravel.models.car.Route;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.time.format.DateTimeFormatter;

public class OfferDetails implements Navigatable, FrontNavigatable {
    private DashBoard dashBoardController;
    private FrontOffice frontOfficeController;
    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
    }
    @FXML private ImageView offerImage;
    @FXML private Label priceLabel;
    @FXML private Label dateLabel;
    @FXML private Label routeLabel;
    @FXML private Label idLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label routeFromLabel;
    @FXML private Label routeToLabel;
    @FXML private Label routeDistanceLabel;
    @FXML private Label carBrandLabel;
    @FXML private Label carModelLabel;
    @FXML private Label carSeatsLabel;
    @FXML private Label carDriverLabel;
    @FXML private Button backButton;
    @FXML private Button bookButton;

    private Offer offer;

    @FXML
    public void initialize(Offer offer) {
        setOffer(offer);

        backButton.setOnAction(event -> onBackClicked());
        bookButton.setOnAction(event -> onBookClicked());
        if(frontOfficeController != null) {
            backButton.setVisible(false);
            bookButton.setVisible(true);
        }
        else {
            backButton.setVisible(true);
            bookButton.setVisible(false);
        }
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        updateOfferDetails();
    }

    private void updateOfferDetails() {
        if (offer != null) {
            priceLabel.setText(String.format("$%.2f", offer.getPrice()));
            dateLabel.setText(offer.getDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' HH:mm")));
            idLabel.setText(String.valueOf(offer.getId()));
            descriptionLabel.setText(offer.getDescription());

            Route route = offer.getRoute();
            if (route != null) {
                routeLabel.setText(route.getLocation_start() + " to " + route.getLocation_destination());
                routeFromLabel.setText(route.getLocation_start());
                routeToLabel.setText(route.getLocation_destination());
                routeDistanceLabel.setText("10" + " km");
            }
            else {
                routeLabel.setText("No route assigned");
                routeFromLabel.setText("No route assigned");
                routeToLabel.setText("No route assigned");
                routeDistanceLabel.setText("No route assigned");
            }

            PrivateCar car = offer.getCar();
            if (car != null) {
                carBrandLabel.setText(car.getBrand());
                carModelLabel.setText(car.getModel());
                carSeatsLabel.setText(String.valueOf(car.getNum_place()));
                carDriverLabel.setText((car.getCarDriver()!= null) ? car.getCarDriver().getFirstName() + " " + car.getCarDriver().getLastName(): "No driver assigned");
            }
            else {
                carBrandLabel.setText("No car assigned");
                carModelLabel.setText("No car assigned");
                carSeatsLabel.setText("No car assigned");
                carDriverLabel.setText("No driver assigned");
            }

            // Load a specific image for the offer if available
            // offerImage.setImage(new Image("/images/specific-offer-image.jpg"));
        }
    }


    private void onBackClicked() {
        // TODO: Implement navigation back to previous screen
        System.out.println("Back button clicked");
    }

    private void onBookClicked() {
        // TODO: Implement booking functionality
        frontOfficeController.navigateTo("dashboard/car/offer-book-form.fxml");
        ((OfferBookForm) frontOfficeController.getController()).initialize(offer);

    }


}