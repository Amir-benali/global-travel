package com.globalTravel.controllers.car;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.car.CarDriver;
import com.globalTravel.services.car.CarDriverService;
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

public class DriverGrid implements Navigatable {
    private DashBoard dashBoardController;
    private CarDriverService driverService = new CarDriverService();

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private FlowPane driversGrid;

    private List<CarDriver> drivers;

    @FXML
    public void initialize() {
        loadDrivers();
    }

    private void loadDrivers() {
        driversGrid.getChildren().clear();
        drivers = getDrivers();

        for (CarDriver driver : drivers) {
            VBox driverCard = createDriverCard(driver);
            driversGrid.getChildren().add(driverCard);
        }
    }

    private VBox createDriverCard(CarDriver driver) {
        VBox card = new VBox(10);
        card.getStyleClass().add("driver-card");

        VBox driverInfo = new VBox(5);
        driverInfo.getStyleClass().add("driver-info");

        // Driver details
        Label firstNameLabel = new Label("First Name: " + driver.getFirstName());
        firstNameLabel.getStyleClass().add("driver-first-name");

        Label lastNameLabel = new Label("Last Name: " + driver.getLastName());
        lastNameLabel.getStyleClass().add("driver-last-name");

        Label phoneLabel = new Label("Phone: " + driver.getPhone());
        phoneLabel.getStyleClass().add("driver-phone");

        // Buttons
        Button updateButton = new Button("Update Driver");
        updateButton.setOnAction(e -> {
            try {
                navigateToUpdateDriver(driver);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        updateButton.getStyleClass().add("view-details-button");

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("view-details-button");
        deleteButton.setOnAction(e -> deleteDriver(driver));

        HBox buttonHbox = new HBox(3);
        buttonHbox.getChildren().addAll(updateButton, deleteButton);
        driverInfo.getChildren().addAll(firstNameLabel, lastNameLabel, phoneLabel, buttonHbox);

        card.getChildren().addAll(driverInfo);

        return card;
    }

    private void deleteDriver(CarDriver driver) {
        System.out.println("Deleting: " + driver);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Are you sure you want to delete this driver?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            driverService.supprimer(driver);
            loadDrivers();
        }
    }

    private void navigateToUpdateDriver(CarDriver driver) throws IOException {
        dashBoardController.navigateTo("dashboard/car/driver-update-form.fxml");
        DriverUpdateForm updateForm = (DriverUpdateForm) dashBoardController.getController();
        updateForm.initialize(driver);
    }

    private List<CarDriver> getDrivers() {
        return driverService.rechercher();
    }

    public void addDriver(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/car/driver-create-form.fxml");
    }

    public void navigateToCar(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/car/car-grid.fxml");

    }

    public void navigateToOffer(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/car/offer-grid.fxml");

    }
}