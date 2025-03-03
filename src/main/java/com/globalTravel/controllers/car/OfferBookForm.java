package com.globalTravel.controllers.car;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.car.Offer;
import com.globalTravel.models.car.Route;
import com.globalTravel.models.user.User;
import com.globalTravel.services.user.UserService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class OfferBookForm implements Navigatable, FrontNavigatable {

    @FXML private TextField startLocationField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private WebView mapWebView;
    @FXML private ListView<String> startSuggestions;
    @FXML private ListView<String> destinationSuggestions;
    @FXML private TextField selectedUserField;

    private WebEngine webEngine;
    private RouteMap routeMap;
    private double[] startCoords = null;
    private double[] destCoords = null;
    private final String apiKey = "cdd53807abc4440ea771e2beb6598c08";
    private DashBoard dashBoardController;
    private FrontOffice frontOfficeController;

    private Offer offer;
    private User selectedUser;

    @FXML
    public void initialize(Offer offer) {
        this.offer = offer;
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
    private void handleSelectUser() {
        // Fetch the list of users (replace this with your actual logic)
        List<User> users = fetchUsers();

        // Create a dialog to display the list of users
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Select User");
        dialog.setHeaderText("Choose a user from the list");

        // Set the button types
        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

        // Create a ListView to display the users
        ListView<User> userListView = new ListView<>();
        userListView.getItems().addAll(users);

        // Set a custom cell factory to display first name, last name, and image
        userListView.setCellFactory(param -> new ListCell<User>() {
            private final ImageView imageView = new ImageView();
            private final Label nameLabel = new Label();

            {
                // Set the size of the image
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                imageView.setPreserveRatio(true);

                // Apply modern styling to the cell
                setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4; -fx-border-color: #ddd; -fx-border-width: 1;");
                nameLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
            }

            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Set the user's image (replace with your actual image loading logic)
                    String imageUrl = user.getImage(); // Assuming getImage() returns the image URL or path
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        imageView.setImage(new Image(imageUrl));
                    } else {
                        imageView.setImage(new Image("/images/user-icon.png")); // Default image if no image is provided
                    }


                    // Set the user's name
                    nameLabel.setText(user.getFirstName() + " " + user.getLastName());

                    // Create an HBox to hold the image and name
                    HBox hbox = new HBox(10, imageView, nameLabel);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    // Set the HBox as the graphic for the cell
                    setGraphic(hbox);
                }
            }
        });

        dialog.getDialogPane().setContent(userListView);

        // Convert the result to a User object when the select button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == selectButtonType) {
                return userListView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        // Show the dialog and handle the result
        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            selectedUser = user;
            selectedUserField.setText(user.getFirstName() + " " + user.getLastName());
        });
    }
    UserService userService = new UserService();
    private List<User> fetchUsers() {
        // Replace this with your actual logic to fetch users from the database or service
        // For now, we'll return a dummy list
        List <User> users = userService.rechercher();

        return users.stream().filter(user -> user.getRoles().toLowerCase().equals("employee")).toList();

    }

    @FXML
    private void handleBooking() {
        if (startCoords != null && destCoords != null && selectedUser != null) {
            // Print start and destination coordinates
            System.out.println("Start Coordinates: [" + startCoords[0] + ", " + startCoords[1] + "]");
            System.out.println("Destination Coordinates: [" + destCoords[0] + ", " + destCoords[1] + "]");

            // Calculate distance using Haversine formula
            double distance = calculateDistance(startCoords[1], startCoords[0], destCoords[1], destCoords[0]);
            System.out.println("Calculated Distance: " + distance + " km");

            // Estimate travel time (assuming average speed of 60 km/h)
            double averageSpeed = 60; // in km/h
            double travelTime = distance / averageSpeed; // in hours
            System.out.println("Estimated Travel Time: " + travelTime + " hours");

            LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(timeField.getText()));
            Route route = new Route(dateTime, "[" + startCoords[0] + ", " + startCoords[1] + "]", "[" + destCoords[0] + ", " + destCoords[1] + "]");

            // Pass the selected user's ID to the car reservation constructor
            if (dashBoardController != null) {
                dashBoardController.navigateTo("dashboard/car/payment-form.fxml");
                ((PaymentForm) dashBoardController.getController()).initialize(route,selectedUser, offer);
            } else if (frontOfficeController != null) {
                frontOfficeController.navigateTo("dashboard/car/payment-form.fxml");
                ((PaymentForm) frontOfficeController.getController()).initialize(route, selectedUser, offer);
            }
        } else {
            System.out.println("Start or destination coordinates are not set, or no user is selected.");
        }
    }

    // Haversine formula to calculate the distance between two coordinates
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to kilometers

        return distance;
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