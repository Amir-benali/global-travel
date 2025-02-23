package com.globalTravel.controllers.car;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.car.PrivateCar;
import com.globalTravel.models.car.CarDriver;
import com.globalTravel.services.car.CarDriverService;
import com.globalTravel.services.car.PrivateCarService;
import com.globalTravel.utils.AzureBlobService;
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

public class CarCreateForm implements Navigatable {

    @FXML private VBox carImageErrorContainer;
    @FXML private Label selectedImageLabel;
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
    @FXML private ImageView driverImagePreview;

    private DashBoard dashBoardController;
    private PrivateCarService carService = new PrivateCarService();
    private CarDriverService driverService = new CarDriverService();
    private String selectedImageName;
    private String selectedImagePath;
    private File selectedImageFile;

    private Stage stage;
    private List<CarDriver> drivers;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

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

        // Image validation
        carImageErrorContainer.getChildren().clear(); // Clear previous error messages
        if (selectedImageFile == null) {
            markFieldAsInvalid(null, carImageErrorContainer, "Image selection is required.");
            isValid = false;
        }

        return isValid;
    }

    private void markFieldAsInvalid(Control field, VBox errorContainer, String errorMessage) {
        if (field != null) {
            field.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
        }
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
        carImageErrorContainer.getChildren().clear();
    }

    private void clearForm() {
        brandField.clear();
        modelField.clear();
        numSeatsField.clear();
        driverComboBox.getSelectionModel().clearSelection();
        selectedImageLabel.setText("No image selected");
        carImageErrorContainer.getChildren().clear();
    }

    @FXML
    private void handleSaveCar() {
        if (!validateForm()) {
            return;
        }
        try {
            handleCopyImage();

            CarDriver selectedDriver = driverComboBox.getValue();
            System.out.println("Selected Image Path: " + this.selectedImagePath);

            PrivateCar car = new PrivateCar(
                    brandField.getText().trim(),
                    modelField.getText().trim(),
                    Integer.parseInt(numSeatsField.getText().trim()),
                    selectedDriver,
                    ""
            );
            car.setImage(this.selectedImagePath);
            addCar(car);
            closeForm();
        } catch (Exception e) {
            System.err.println("Error saving car: " + e.getMessage());
        }
    }

    public void handleCopyImage() {
        if (selectedImageFile != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    String imagePath = AzureBlobService.uploadImage(selectedImageFile);
                    System.out.println(imagePath);
                    selectedImagePath = imagePath;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).join();
        }
    }

    private void addCar(PrivateCar car) throws IOException {
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

    public void handleChooseImage(javafx.event.ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        try {
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                this.selectedImageName = selectedFile.getName();
                this.selectedImagePath = selectedFile.getAbsolutePath();
                this.selectedImageFile = selectedFile;
                selectedImageLabel.setText(selectedFile.getName());
                driverImagePreview.setImage(new javafx.scene.image.Image(selectedFile.toURI().toString()));
                carImageErrorContainer.getChildren().clear(); // Clear image error if selected
            }
        } catch (Exception e) {
            System.err.println("Error selecting image: " + e.getMessage());
        }
    }
}
