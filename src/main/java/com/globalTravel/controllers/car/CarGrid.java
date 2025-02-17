package com.globalTravel.controllers.car;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.car.PrivateCar;
import com.globalTravel.models.car.CarDriver;
import com.globalTravel.services.car.PrivateCarService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CarGrid implements Navigatable {
    private DashBoard dashBoardController;
    private PrivateCarService carService = new PrivateCarService();
    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private FlowPane carsGrid;

    private List<PrivateCar> cars;

    @FXML
    public void initialize() {
        loadCars();
    }

    private void loadCars() {
        carsGrid.getChildren().clear();
        cars = getCars();

        for (PrivateCar car : cars) {
            VBox carCard = createCarCard(car);
            carsGrid.getChildren().add(carCard);
        }
    }


    private VBox createCarCard(PrivateCar car) {
        VBox card = new VBox(10);
        card.getStyleClass().add("car-offer-card");

        VBox carInfo = new VBox(5);
        carInfo.getStyleClass().add("car-info");

        ImageView carLogoView = new ImageView(new Image("/images/carlogo.png", 200, 150, true, true));

        // Car details
        Label brandLabel = new Label("Brand: " + car.getBrand());
        brandLabel.getStyleClass().add("car-brand");

        Label modelLabel = new Label("Model: " + car.getModel());
        modelLabel.getStyleClass().add("car-model");

        Label seatsLabel = new Label("Seats: " + car.getNum_place());
        seatsLabel.getStyleClass().add("car-seats");

        Label driverLabel = new Label("Driver: " + car.getCarDriver().getFirstName() + " " + car.getCarDriver().getLastName());
        driverLabel.getStyleClass().add("car-driver");

        // Buttons
        Button updateButton = new Button("Update Car");
        updateButton.setOnAction(e -> {
            try {
                navigateToUpdateCar(car);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        updateButton.getStyleClass().add("view-details-button");

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("view-details-button");
        deleteButton.setOnAction(e -> deleteCar(car));

        HBox buttonHbox = new HBox(3);
        buttonHbox.getChildren().addAll(updateButton, deleteButton);
        carInfo.getChildren().addAll(carLogoView,brandLabel, modelLabel, seatsLabel, driverLabel, buttonHbox);

        card.getChildren().addAll(carInfo);

        return card;
    }

    private void deleteCar(PrivateCar car) {
        System.out.println("Deleting: " + car);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Are you sure you want to delete this car?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            carService.supprimer(car);
            loadCars();
        }
    }

    private void navigateToUpdateCar(PrivateCar car) throws IOException {
        dashBoardController.navigateTo("dashboard/car/car-update-form.fxml");
        CarUpdateForm updateForm = (CarUpdateForm) dashBoardController.getController();
        updateForm.initialize(car);
    }

    private List<PrivateCar> getCars() {
        return carService.rechercher();
    }


    public void addCar(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/car/car-create-form.fxml");
    }

    public void navigateToDriver(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/car/driver-grid.fxml");
    }

    public void navigateToOffer(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/car/offer-grid.fxml");

    }
}