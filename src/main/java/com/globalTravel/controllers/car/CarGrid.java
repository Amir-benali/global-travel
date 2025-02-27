package com.globalTravel.controllers.car;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.car.PrivateCar;
import com.globalTravel.models.car.CarDriver;
import com.globalTravel.services.car.PrivateCarService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
            Node carCard = createCarCard(car);
            carsGrid.getChildren().add(carCard);
        }
    }


    private Node createCarCard(PrivateCar car) {
        VBox card = new VBox(15);
        card.getStyleClass().addAll("car-offer-card", "modern-card");
        card.setPadding(new Insets(15));

        // Car Image
        String imagePath = (!car.getImage().isEmpty()) ? car.getImage() : "/images/carlogo.png";
        Image carImage;

        try {
            if (imagePath.startsWith("http") || imagePath.startsWith("file:")) {
                carImage = new Image(imagePath, 300, 200, false, true);
            } else {
                carImage = new Image(getClass().getResource(imagePath).toExternalForm(), 300, 200, false, true);
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + imagePath);
            carImage = new Image(getClass().getResource("/images/carlogo.png").toExternalForm(), 300, 200, false, true);
        }

        ImageView carImageView = new ImageView(carImage);
        carImageView.setFitWidth(300);
        carImageView.setFitHeight(200);
        carImageView.setPreserveRatio(false);
        carImageView.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.2)));
        carImageView.getStyleClass().add("car-image");

        // Car Info Container
        VBox carInfo = new VBox(10);
        carInfo.getStyleClass().add("car-info");

        // Car Brand and Model
        Label brandModelLabel = new Label(car.getBrand() + " " + car.getModel());
        brandModelLabel.getStyleClass().add("car-brand-model");

        // Car Details Grid (2x2 Layout)
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(10);

        // Row 1
        detailsGrid.add(createIcon(FontAwesomeIcon.NAVICON, Color.BLUE), 0, 0);
        detailsGrid.add(new Label("ID: " + car.getId()), 1, 0);
        detailsGrid.add(createIcon(FontAwesomeIcon.USER, Color.GREEN), 2, 0);
        detailsGrid.add(new Label("Seats: " + car.getNum_place()), 3, 0);

        // Row 2
        detailsGrid.add(createIcon(FontAwesomeIcon.CAR, Color.ORANGE), 0, 1);
        detailsGrid.add(new Label("Driver: " + ((car.getCarDriver() != null) ? car.getCarDriver().getFirstName() + " " + car.getCarDriver().getLastName() : "N/A")), 1, 1);
        detailsGrid.add(createIcon(FontAwesomeIcon.CREDIT_CARD, Color.RED), 2, 1);
        detailsGrid.add(new Label("DR ID: " + ((car.getCarDriver() != null) ? car.getCarDriver().getId() : "N/A")), 3, 1);

        detailsGrid.getStyleClass().add("details-grid");

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button updateButton = new Button("Update");
        updateButton.getStyleClass().addAll("modern-button", "update-button");
        updateButton.setOnAction(e -> {
            try {
                navigateToUpdateCar(car);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("modern-button", "delete-button");
        deleteButton.setOnAction(e -> deleteCar(car));

        buttonBox.getChildren().addAll(updateButton, deleteButton);

        // Assemble all components
        carInfo.getChildren().addAll(brandModelLabel, detailsGrid);
        card.getChildren().addAll(carImageView, carInfo, buttonBox);

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