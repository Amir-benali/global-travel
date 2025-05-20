package com.globalTravel.controllers.car;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.car.Offer;
import com.globalTravel.models.car.Route;
import com.globalTravel.models.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class OfferBookForm implements Navigatable, FrontNavigatable {

    @FXML private TextField startLocationField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private WebView mapWebView;
    @FXML private ListView<String> startSuggestions;
    @FXML private ListView<String> destinationSuggestions;
    @FXML private FlowPane seatsContainer;
    @FXML private VBox employeesContainer;
    @FXML private Label totalPriceLabel;

    private WebEngine webEngine;
    private RouteMap routeMap;
    private double[] startCoords = null;
    private double[] destCoords = null;
    private final String apiKey = "cdd53807abc4440ea771e2beb6598c08";
    private DashBoard dashBoardController;
    private FrontOffice frontOfficeController;

    private Offer offer;
    private ArrayList<User> employees;
    private ArrayList<String> reservedSeats;

    @FXML
    public void initialize(Offer offer, ArrayList<String> seats, ArrayList<User> emp) {
        this.offer = offer;
        this.employees = emp;
        this.reservedSeats = seats;

        initializeMap();
        setupAddressSuggestions();
        setupBookingSummary();
    }

    private void initializeMap() {
        routeMap = new RouteMap();
        webEngine = mapWebView.getEngine();
        webEngine.loadContent(routeMap.getHtmlContent());

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaController", routeMap);
                webEngine.executeScript("GetMap();");
            }
        });
    }

    private void setupAddressSuggestions() {
        startLocationField.textProperty().addListener((obs, oldVal, newVal) -> fetchSuggestions(newVal, startSuggestions));
        destinationField.textProperty().addListener((obs, oldVal, newVal) -> fetchSuggestions(newVal, destinationSuggestions));

        startSuggestions.setOnMouseClicked(e -> {
            String selected = startSuggestions.getSelectionModel().getSelectedItem();
            if (selected != null) {
                startLocationField.setText(selected);
                startSuggestions.setVisible(false);
                fetchCoordinates(selected, true);
            }
        });

        destinationSuggestions.setOnMouseClicked(e -> {
            String selected = destinationSuggestions.getSelectionModel().getSelectedItem();
            if (selected != null) {
                destinationField.setText(selected);
                destinationSuggestions.setVisible(false);
                fetchCoordinates(selected, false);
            }
        });
    }

    private void setupBookingSummary() {
        // Display selected seats
        seatsContainer.getChildren().clear();
        for (String seat : reservedSeats) {
            Label seatLabel = new Label(seat);
            seatLabel.setStyle("-fx-background-color: #e3f2fd; -fx-padding: 3 6 3 6; -fx-border-radius: 3;");
            seatsContainer.getChildren().add(seatLabel);
        }

        // Display assigned employees
        employeesContainer.getChildren().clear();
        for (User employee : employees) {
            HBox employeeBox = new HBox(10);
            employeeBox.setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 5;");

            ImageView imageView = new ImageView();
            imageView.setFitWidth(30);
            imageView.setFitHeight(30);

            try {
                Image image = new Image(employee.getImage());
                imageView.setImage(image);
            } catch (Exception e) {
                imageView.setImage(new Image("/images/user-icon.png"));
            }

            Label nameLabel = new Label(employee.getFirstName() + " " + employee.getLastName());
            employeeBox.getChildren().addAll(imageView, nameLabel);
            employeesContainer.getChildren().add(employeeBox);
        }

        // Set total price
        totalPriceLabel.setText(String.format("%.2f $", calculateTotalPrice()));
    }

    private double calculateTotalPrice() {
        double basePrice = offer != null ? offer.getPrice() : 0.0;
        return basePrice * reservedSeats.size();
    }

    private void fetchSuggestions(String query, ListView<String> suggestions) {
        if (query.length() < 3) {
            suggestions.setVisible(false);
            return;
        }

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = String.format("https://api.opencagedata.com/geocode/v1/json?q=%s&key=%s&limit=5", encodedQuery, apiKey);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        String[] addresses = parseAddressSuggestions(response);
                        javafx.application.Platform.runLater(() -> {
                            suggestions.getItems().clear();
                            suggestions.getItems().addAll(addresses);
                            suggestions.setVisible(true);
                        });
                    })
                    .exceptionally(e -> {
                        System.err.println("Error fetching suggestions: " + e.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            System.err.println("Error encoding query: " + e.getMessage());
        }
    }

    private String[] parseAddressSuggestions(String jsonResponse) {
        JSONObject json = new JSONObject(jsonResponse);
        JSONArray results = json.getJSONArray("results");

        String[] addresses = new String[results.length()];
        for (int i = 0; i < results.length(); i++) {
            addresses[i] = results.getJSONObject(i).getString("formatted");
        }
        return addresses;
    }

    private void fetchCoordinates(String address, boolean isStart) {
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = String.format("https://api.opencagedata.com/geocode/v1/json?q=%s&key=%s", encodedAddress, apiKey);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        double[] coords = parseCoordinates(response);
                        if (coords != null) {
                            javafx.application.Platform.runLater(() -> {
                                if (isStart) startCoords = coords;
                                else destCoords = coords;
                                updateMap();
                            });
                        }
                    })
                    .exceptionally(e -> {
                        System.err.println("Error fetching coordinates: " + e.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            System.err.println("Error encoding address: " + e.getMessage());
        }
    }

    private double[] parseCoordinates(String jsonResponse) {
        JSONObject json = new JSONObject(jsonResponse);
        JSONArray results = json.getJSONArray("results");
        if (results.length() > 0) {
            JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
            return new double[]{geometry.getDouble("lng"), geometry.getDouble("lat")};
        }
        return null;
    }

    private void updateMap() {
        if (startCoords != null && destCoords != null) {
            String script = String.format("getRoute([%f, %f], [%f, %f]);", startCoords[0], startCoords[1], destCoords[0], destCoords[1]);
            webEngine.executeScript(script);
        }
    }

    @FXML
    private void handleBooking() {
        if (startCoords == null || destCoords == null) {
            showAlert("Missing Information", "Please select both start location and destination");
            return;
        }

        if (datePicker.getValue() == null || timeField.getText().isEmpty()) {
            showAlert("Missing Information", "Please select date and time");
            return;
        }

        try {
            LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(timeField.getText()));
            Route route = new Route(dateTime,
                    "[" + startCoords[0] + ", " + startCoords[1] + "]",
                    "[" + destCoords[0] + ", " + destCoords[1] + "]");

            double distance = calculateDistance(startCoords[1], startCoords[0], destCoords[1], destCoords[0]);
            double totalPrice = calculateTotalPrice() + (distance * 0.5); // Add distance fee

            if (dashBoardController != null) {
                dashBoardController.navigateTo("dashboard/car/payment-form.fxml");
                ((PaymentForm) dashBoardController.getController()).initialize(route, employees, offer,reservedSeats);
            } else if (frontOfficeController != null) {
                frontOfficeController.navigateTo("dashboard/car/payment-form.fxml");
                ((PaymentForm) frontOfficeController.getController()).initialize(route, employees, offer,reservedSeats);
            }
        } catch (Exception e) {
            showAlert("Invalid Time", "Please enter time in HH:MM format");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @FXML
    private void handleCancel() {
        if (dashBoardController != null) {
            dashBoardController.navigateTo("dashboard/car/offer-details.fxml");
        } else if (frontOfficeController != null) {
            frontOfficeController.navigateTo("dashboard/car/offer-details.fxml");
        }
    }

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
    }
}