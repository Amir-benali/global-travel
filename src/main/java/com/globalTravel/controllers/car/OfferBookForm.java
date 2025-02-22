package com.globalTravel.controllers.car;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class OfferBookForm {

    @FXML private TextField startLocationField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private WebView mapWebView;
    @FXML private ListView<String> startSuggestions;
    @FXML private ListView<String> destinationSuggestions;

    private WebEngine webEngine;
    private RouteMap routeMap;

    private double[] startCoords = null; // Store start coordinates
    private double[] destCoords = null; // Store destination coordinates

    private final String apiKey = "cdd53807abc4440ea771e2beb6598c08"; // Replace with your OpenCage API key

    @FXML
    public void initialize() {
        routeMap = new RouteMap();
        webEngine = mapWebView.getEngine();

        // Load the map
        webEngine.loadContent(routeMap.getHtmlContent());

        // Set up a listener for when the page is fully loaded
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaController", routeMap);
                webEngine.executeScript("GetMap();");
            }
        });

        // Add listeners to address fields for suggestions
        startLocationField.textProperty().addListener((obs, oldVal, newVal) -> fetchSuggestions(newVal, startSuggestions));
        destinationField.textProperty().addListener((obs, oldVal, newVal) -> fetchSuggestions(newVal, destinationSuggestions));

        // Handle selection of suggestions
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
            // Encode the query to handle spaces and special characters
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            String url = String.format("https://api.opencagedata.com/geocode/v1/json?q=%s&key=%s&limit=5", encodedQuery, apiKey);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        // Parse the response and extract suggestions
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
        // Parse the JSON response to extract address suggestions
        return Arrays.stream(jsonResponse.split("\"formatted\":\"")) // Extract formatted addresses
                .skip(1) // Skip the first split result
                .map(line -> line.split("\"")[0]) // Extract the address
                .toArray(String[]::new);
    }

    private void fetchCoordinates(String address, boolean isStart) {
        try {
            // Encode the address to handle spaces and special characters
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
            String url = String.format("https://api.opencagedata.com/geocode/v1/json?q=%s&key=%s", encodedAddress, apiKey);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        double[] coords = parseCoordinates(response);
                        if (coords != null) {
                            javafx.application.Platform.runLater(() -> {
                                if (isStart) {
                                    startCoords = coords; // Store start coordinates
                                } else {
                                    destCoords = coords; // Store destination coordinates
                                }
                                updateMap(); // Update the map with both coordinates
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
        // Parse the JSON response to extract latitude and longitude
        String[] parts = jsonResponse.split("\"geometry\":\\{\"lat\":");
        if (parts.length > 1) {
            double lat = Double.parseDouble(parts[1].split(",")[0]);
            double lng = Double.parseDouble(parts[1].split("\"lng\":")[1].split("}")[0]);
            return new double[]{lng, lat}; // OpenCage returns [lat, lng], but we need [lng, lat]
        }
        return null;
    }

    private void updateMap() {
        if (startCoords != null && destCoords != null) {
            String script = String.format("getRoute([%f, %f], [%f, %f]);",
                    startCoords[0], startCoords[1], destCoords[0], destCoords[1]);
            webEngine.executeScript(script);
        }
    }

    @FXML
    private void handleBooking() {
        // Implement booking logic here
        System.out.println("Booking requested");
    }

    @FXML
    private void handleCancel() {
        // Implement cancel logic here
        System.out.println("Booking cancelled");
    }
}