package com.globalTravel.controllers.car;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.car.Offer;
import com.globalTravel.models.car.Route;
import com.globalTravel.models.car.PrivateCar;
import com.globalTravel.services.car.OfferService;
import com.globalTravel.services.car.RouteService;
import com.globalTravel.services.car.PrivateCarService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OfferUpdateForm implements Navigatable {

    @FXML private Label formTitleLabel;
    @FXML private TextField descriptionField;
    @FXML private DatePicker dateField;
    @FXML private TextField priceField;
    @FXML private ComboBox<Route> routeComboBox;
    @FXML private ComboBox<PrivateCar> carComboBox;
    @FXML private Button saveButton;
    @FXML private VBox descriptionErrorContainer;
    @FXML private VBox dateErrorContainer;
    @FXML private VBox priceErrorContainer;
    @FXML private VBox routeErrorContainer;
    @FXML private VBox carErrorContainer;

    private DashBoard dashBoardController;
    private OfferService offerService = new OfferService();
    private RouteService routeService = new RouteService();
    private PrivateCarService carService = new PrivateCarService();
    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    private Offer offerToEdit;
    private Stage stage;
    private List<Route> routes;
    private List<PrivateCar> cars;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
        routeComboBox.getItems().setAll(routes);
        routeComboBox.setCellFactory(comboBox -> new ListCell<>() {
            @Override
            protected void updateItem(Route route, boolean empty) {
                super.updateItem(route, empty);
//                setText(empty || route == null ? null : route.getId());
            }
        });
        routeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Route route, boolean empty) {
                super.updateItem(route, empty);
//                setText(empty || route == null ? null : route.getId());
            }
        });
    }

    public void setCars(List<PrivateCar> cars) {
        this.cars = cars;
        carComboBox.getItems().setAll(cars);
        carComboBox.setCellFactory(comboBox -> new ListCell<>() {
            @Override
            protected void updateItem(PrivateCar car, boolean empty) {
                super.updateItem(car, empty);
                setText(empty || car == null ? null : car.getBrand() + " " + car.getModel());
            }
        });
        carComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(PrivateCar car, boolean empty) {
                super.updateItem(car, empty);
                setText(empty || car == null ? null : car.getBrand() + " " + car.getModel());
            }
        });
    }

    @FXML
    public void initialize(Offer offerToEdit) {
        System.out.println("Initializing OfferForm...");
        if (offerToEdit != null) {
            this.offerToEdit = offerToEdit;
        }
        setRoutes(routeService.rechercher());
        setCars(carService.rechercher());
        populateForm();
    }

    private void populateForm() {
        descriptionField.setText(offerToEdit.getDescription());
        dateField.setValue(offerToEdit.getDate().toLocalDate());
        priceField.setText(String.valueOf(offerToEdit.getPrice()));
        routeComboBox.setValue(offerToEdit.getRoute());
        carComboBox.setValue(offerToEdit.getCar());
    }

    private boolean validateForm() {
        boolean isValid = true;
        clearFieldStyles();

        if (descriptionField.getText().trim().isEmpty()) {
            markFieldAsInvalid(descriptionField, descriptionErrorContainer, "Description is required.");
            isValid = false;
        }
        if (dateField.getValue() == null) {
            markFieldAsInvalid(dateField, dateErrorContainer, "Date is required.");
            isValid = false;
        }
        if (priceField.getText().trim().isEmpty()) {
            markFieldAsInvalid(priceField, priceErrorContainer, "Price is required.");
            isValid = false;
        } else {
            try {
                float price = Float.parseFloat(priceField.getText().trim());
                if (price <= 0) {
                    markFieldAsInvalid(priceField, priceErrorContainer, "Price must be greater than 0.");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                markFieldAsInvalid(priceField, priceErrorContainer, "Price must be a valid number.");
                isValid = false;
            }
        }
        if (routeComboBox.getValue() == null) {
            markFieldAsInvalid(routeComboBox, routeErrorContainer, "Route is required.");
            isValid = false;
        }
        if (carComboBox.getValue() == null) {
            markFieldAsInvalid(carComboBox, carErrorContainer, "Car is required.");
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
        descriptionField.setStyle("");
        dateField.setStyle("");
        priceField.setStyle("");
        routeComboBox.setStyle("");
        carComboBox.setStyle("");

        descriptionErrorContainer.getChildren().clear();
        dateErrorContainer.getChildren().clear();
        priceErrorContainer.getChildren().clear();
        routeErrorContainer.getChildren().clear();
        carErrorContainer.getChildren().clear();
    }

    @FXML
    private void handleSaveOffer() {
        if (!validateForm()) {
            return;
        }
        try {
            Route selectedRoute = routeComboBox.getValue();
            PrivateCar selectedCar = carComboBox.getValue();
            Offer offer = new Offer(
                    offerToEdit.getId(),
                    descriptionField.getText().trim(),
                    dateField.getValue().atStartOfDay(),
                    Float.parseFloat(priceField.getText().trim()),
                    selectedRoute,
                    selectedCar
            );
            updateOffer(offer);
            closeForm();
        } catch (Exception e) {
            System.err.println("Error updating offer: " + e.getMessage());
        }
    }

    private void updateOffer(Offer offer) {
        System.out.println("Updating offer: " + offer);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to update this offer?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK) {
            return;
        }
        offerService.modifier(offer);
        dashBoardController.navigateTo("dashboard/offer/offer-grid.fxml");
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
}