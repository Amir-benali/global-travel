package com.globalTravel.controllers.car;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.car.PrivateCar;
import com.globalTravel.models.car.CarDriver;
import com.globalTravel.services.car.CarDriverService;
import com.globalTravel.services.car.PrivateCarService;
import com.globalTravel.utils.AzureBlobService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CarUpdateForm implements Navigatable {

    @FXML private Label selectedImageLabel;
    @FXML private ImageView driverImagePreview;
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

    private String selectedImageName;
    private String selectedImagePath;
    private File selectedImageFile;


    private DashBoard dashBoardController;
    private PrivateCarService carService = new PrivateCarService();
    private CarDriverService driverService = new CarDriverService();
    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    private PrivateCar carToEdit;
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
    public void initialize(PrivateCar carToEdit) {
        System.out.println("Initializing CarForm...");
        if (carToEdit != null) {
            this.carToEdit = carToEdit;
        }
        setDrivers(driverService.rechercher());
        populateForm();
    }

    private void populateForm() {
        brandField.setText(carToEdit.getBrand());
        modelField.setText(carToEdit.getModel());
        numSeatsField.setText(String.valueOf(carToEdit.getNum_place()));
        driverComboBox.setValue(carToEdit.getCarDriver());
        driverImagePreview.setImage(new javafx.scene.image.Image(carToEdit.getImage()));
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

    @FXML
    private void handleSaveCar() {
        if (!validateForm()) {
            return;
        }
        try {
            handleCopyImage();
            CarDriver selectedDriver = driverComboBox.getValue();
            PrivateCar car = new PrivateCar(
                    carToEdit.getId(),
                    brandField.getText().trim(),
                    modelField.getText().trim(),
                    Integer.parseInt(numSeatsField.getText().trim()),
                    selectedDriver,""
            );
            if (selectedImagePath != null) {
                car.setImage(selectedImagePath);
            }
            else {
                car.setImage(carToEdit.getImage());
            }
            updateCar(car);
            closeForm();
        } catch (Exception e) {
            System.err.println("Error updating car: " + e.getMessage());
        }
    }

    private void updateCar(PrivateCar car) {
        System.out.println("Updating car: " + car);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to update this car?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK) {

            return;
        }
        carService.modifier(car);
        dashBoardController.navigateTo("dashboard/car/car-grid.fxml");

    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private void closeForm() {
        if (stage != null) {
            stage.close();
        }
    }
    public void handleCopyImage() {
        if (selectedImageFile != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    String imagePath = AzureBlobService.uploadImage(selectedImageFile);
                    System.out.println(imagePath);
                    selectedImagePath=imagePath;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).join();

        }
    }

    public void handleChooseImage(javafx.event.ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        try {
            java.io.File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                this.selectedImageName = selectedFile.getName();
                this.selectedImagePath = selectedFile.getAbsolutePath();
                System.out.println("Selected file: " + selectedFile.getName());
                this.selectedImageFile = selectedFile;
                this.selectedImageLabel.setText(selectedFile.getName());
                this.driverImagePreview.setImage(new javafx.scene.image.Image(selectedFile.toURI().toString()));
            } else {
                System.out.println("Image selection canceled.");
            }
        } catch (Exception e) {
            System.err.println("Error occurred while selecting an image: " + e.getMessage());
        }
    }

}
