package com.globalTravel.controllers.car;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class CarDetails implements Navigatable {
    private DashBoard dashBoardController;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }
    @FXML
    private Label carBrand;

    @FXML
    private Label carDescription;

    @FXML
    private VBox carDetailsContainer;

    @FXML
    private Label carFuelType;

    @FXML
    private Label carModel;

    @FXML
    private Label carPrice;

    @FXML
    private Label carSeats;

    @FXML
    private Label carTitle;

    @FXML
    private Label carTransmission;

    @FXML
    private Label carYear;

    @FXML
    private TilePane imageGallery;

    @FXML
    private ImageView mainCarImage;

    @FXML
    void handleBackToList(ActionEvent event) {
        dashBoardController.navigateTo("dashboard/car/car-grid.fxml");

    }

    @FXML
    void handleBookNow(ActionEvent event) {

    }

}
