package com.globalTravel.controllers.car;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.models.car.CarDriver;
import com.globalTravel.services.car.CarDriverService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Optional;

public class DriverCreateForm implements Navigatable {

    @FXML private Label formTitleLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private Button saveButton;
    @FXML private VBox firstNameErrorContainer;
    @FXML private VBox lastNameErrorContainer;
    @FXML private VBox phoneErrorContainer;

    private DashBoard dashBoardController;
    private CarDriverService driverService = new CarDriverService();
    private Stage stage;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        System.out.println("Initializing CarDriverForm...");
    }

    private boolean validateForm() {
        boolean isValid = true;
        clearFieldStyles();

        if (firstNameField.getText().trim().isEmpty()) {
            markFieldAsInvalid(firstNameField, firstNameErrorContainer, "First Name is required.");
            isValid = false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            markFieldAsInvalid(lastNameField, lastNameErrorContainer, "Last Name is required.");
            isValid = false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            markFieldAsInvalid(phoneField, phoneErrorContainer, "Phone is required.");
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
        firstNameField.setStyle("");
        lastNameField.setStyle("");
        phoneField.setStyle("");

        firstNameErrorContainer.getChildren().clear();
        lastNameErrorContainer.getChildren().clear();
        phoneErrorContainer.getChildren().clear();
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
    }

    @FXML
    private void handleSaveDriver() {
        if (!validateForm()) {
            return;
        }
        try {
            CarDriver driver = new CarDriver(
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    phoneField.getText().trim()
            );
            addDriver(driver);
            closeForm();
        } catch (Exception e) {
            System.err.println("Error saving driver: " + e.getMessage());
        }
    }

    private void addDriver(CarDriver driver) {
        System.out.println("Adding new driver: " + driver);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to add this driver?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK) {
            return;
        }
        driverService.ajouter(driver);
        dashBoardController.navigateTo("dashboard/car/driver-grid.fxml");
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