package com.globalTravel.controllers.car;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.models.car.CarDriver;
import com.globalTravel.services.car.CarDriverService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

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
        VBox card = new VBox(15);
        card.getStyleClass().addAll("car-offer-card", "modern-card");
        card.setPadding(new Insets(15));

        // Driver Image (Placeholder)
        ImageView driverImageView = new ImageView(new Image("/images/carlogo.png", 300, 200, true, true));
        driverImageView.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.2)));
        driverImageView.getStyleClass().add("car-image");

        // Driver Info Container
        VBox driverInfo = new VBox(10);
        driverInfo.getStyleClass().add("car-info");

        // Driver Full Name
        Label nameLabel = new Label(driver.getFirstName() + " " + driver.getLastName());
        nameLabel.getStyleClass().add("car-brand-model");

        // Driver Details Grid (2 Rows x 2 Columns)
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(10);

        detailsGrid.add(createIcon(FontAwesomeIcon.ID_CARD, Color.BLUE), 0, 0);
        detailsGrid.add(new Label(String.valueOf(driver.getId())), 1, 0);

        detailsGrid.add(createIcon(FontAwesomeIcon.PHONE, Color.GREEN), 0, 1);
        detailsGrid.add(new Label(driver.getPhone()), 1, 1);

        detailsGrid.getStyleClass().add("details-grid");

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button updateButton = new Button("Update Driver");
        updateButton.getStyleClass().addAll("modern-button", "update-button");
        updateButton.setOnAction(e -> {
            try {
                navigateToUpdateDriver(driver);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("modern-button", "delete-button");
        deleteButton.setOnAction(e -> deleteDriver(driver));

        buttonBox.getChildren().addAll(updateButton, deleteButton);

        // Assemble all components
        driverInfo.getChildren().addAll(nameLabel, detailsGrid);
        card.getChildren().addAll(driverImageView, driverInfo, buttonBox);

        // Add hover effect
        card.setOnMouseEntered(e -> card.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.3))));
        card.setOnMouseExited(e -> card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.1))));

        return card;
    }

    // Utility method to create FontAwesome icons with color
    private FontAwesomeIconView createIcon(FontAwesomeIcon icon, Color color) {
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setGlyphSize(20); // Increased size
        iconView.setFill(color); // Set color
        return iconView;
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