package com.globalTravel.controllers.car;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

public class CarGrid implements Navigatable {

    private DashBoard dashBoardController;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private FlowPane carOffersContainer;

    @FXML
    void navigateToDetails(ActionEvent event) {
        dashBoardController.navigateTo("dashboard/car/car-details.fxml");
    }

    public void addCar(ActionEvent actionEvent) {
    }
}
