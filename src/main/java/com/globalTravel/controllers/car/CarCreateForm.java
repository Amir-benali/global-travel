package com.globalTravel.controllers.car;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.car.PrivateCar;
import com.globalTravel.models.car.CarDriver;
import com.globalTravel.services.car.CarDriverService;
import com.globalTravel.services.car.PrivateCarService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class CarCreateForm implements Navigatable {

    @FXML private Label formTitleLabel;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField numSeatsField;
    @FXML private ComboBox<CarDriver> driverComboBox;
    @FXML private Button saveButton;
    @FXML private VBox brandErrorContainer;
    @FXML private VBox modelErrorContainer;
    @FXML private VBox numSeatsErrorContainer;
    @FXML private VBox driverErrorContainer;

    private DashBoard dashBoardController;
    private PrivateCarService carService = new PrivateCarService();
    private CarDriverService driverService = new CarDriverService();
    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }
    private Stage stage;
    private List<CarDriver> drivers;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setDrivers(List<CarDriver> drivers) {
        this.drivers = drivers;
        driverComboBox.getItems().setAll(drivers);
        driverComboBox.setCellFactory(comboBox -> new ListCell<>() {
            @Override
            protected void updateItem(CarDriver driver, boolean empty) {
                super.updateItem(driver, empty);
                setText(empty || driver == null ? null : driver.getFirstName() + " " + driver.getLastName());
            }
        });
        driverComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CarDriver driver, boolean empty) {
                super.updateItem(driver, empty);
                setText(empty || driver == null ? null : driver.getFirstName() + " " + driver.getLastName());
            }
        });
    }

    @FXML
    public void initialize() {
        System.out.println("Initializing CarForm...");
        setDrivers(driverService.rechercher());
    }

    private boolean validateForm() {
        boolean isValid = true;
        clearFieldStyles();

        if (brandField.getText().trim().isEmpty()) {
            markFieldAsInvalid(brandField, brandErrorContainer, "Brand is required.");
            isValid = false;
        }
        if (modelField.getText().trim().isEmpty()) {
            markFieldAsInvalid(modelField, modelErrorContainer, "Model is required.");
            isValid = false;
        }
        if (numSeatsField.getText().trim().isEmpty()) {
            markFieldAsInvalid(numSeatsField, numSeatsErrorContainer, "Number of seats is required.");
            isValid = false;
        } else {
            try {
                int seats = Integer.parseInt(numSeatsField.getText().trim());
                if (seats <= 0) {
                    markFieldAsInvalid(numSeatsField, numSeatsErrorContainer, "Number of seats must be greater than 0.");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                markFieldAsInvalid(numSeatsField, numSeatsErrorContainer, "Number of seats must be a valid number.");
                isValid = false;
            }
        }
        if (driverComboBox.getValue() == null) {
            markFieldAsInvalid(driverComboBox, driverErrorContainer, "Driver is required.");
            isValid = false;
        }
        return isValid;
    }

    private void markFieldAsInvalid(Control field, VBox errorContainer, String errorMessage) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
        Text errorText = new Text(errorMessage);
        errorText.setFill(Color.RED);
        errorContainer.getChildren().add(errorText);
    }

    private void clearFieldStyles() {
        brandField.setStyle("");
        modelField.setStyle("");
        numSeatsField.setStyle("");
        driverComboBox.setStyle("");

        brandErrorContainer.getChildren().clear();
        modelErrorContainer.getChildren().clear();
        numSeatsErrorContainer.getChildren().clear();
        driverErrorContainer.getChildren().clear();
    }

    private void clearForm() {
        brandField.clear();
        modelField.clear();
        numSeatsField.clear();
        driverComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSaveCar() {
        if (!validateForm()) {
            return;
        }
        try {
            CarDriver selectedDriver = driverComboBox.getValue();
            PrivateCar car = new PrivateCar(
                    brandField.getText().trim(),
                    modelField.getText().trim(),
                    Integer.parseInt(numSeatsField.getText().trim()),
                    selectedDriver
            );
            addCar(car);
            closeForm();
        } catch (Exception e) {
            System.err.println("Error saving car: " + e.getMessage());
        }
    }

    private void addCar(PrivateCar car) {
        System.out.println("Adding new car: " + car);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to add this car?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK) {

            return;
        }
        carService.ajouter(car);
        dashBoardController.navigateTo("dashboard/car/car-grid.fxml");




    }

    @FXML
    private void handleCancel() {
        clearForm();
        closeForm();
    }

    private void closeForm() {
        if (stage != null) {
            stage.close();
        }
    }
}