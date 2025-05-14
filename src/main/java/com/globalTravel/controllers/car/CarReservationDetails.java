package com.globalTravel.controllers.car;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import com.globalTravel.models.car.Route;
import java.time.format.DateTimeFormatter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;
import com.google.gson.Gson;

public class CarReservationDetails {

    @FXML private Label startLocationLabel;
    @FXML private Label destinationLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label distanceLabel;
    @FXML private Label estimatedTimeLabel;
    @FXML private Label delayLabel;
    @FXML private Label expectedTimeAfterDelayLabel;
    @FXML private Label weatherConditionLabel; // New label for weather conditions
    @FXML private WebView mapWebView;

    private WebEngine webEngine;
    private RouteMap routeMap;

    private double[] startCoords;
    private double[] destCoords;
    private double distance;
    private double estimatedTime;

    private final String apiKey = "cdd53807abc4440ea771e2beb6598c08"; // Replace with your OpenCage API key
    private final String flaskApiUrl = "https://trip-delay-prediction-efdbdycze8g0f2d5.westeurope-01.azurewebsites.net/predict"; // Replace with your Flask API URL

    public void initialize(Route route) {
        // Extract coordinates from the route's location strings
        this.startCoords = parseCoordinates(route.getLocation_start());
        this.destCoords = parseCoordinates(route.getLocation_destination());

        // Calculate distance using Haversine formula
        this.distance = calculateDistance(startCoords[1], startCoords[0], destCoords[1], destCoords[0]);

        // Estimate travel time (assuming average speed of 60 km/h)
        double averageSpeed = 60; // in km/h
        this.estimatedTime = distance / averageSpeed; // in hours

        // Re-geocode coordinates to addresses
        regeocodeCoordinates(startCoords, true); // Re-geocode start location
        regeocodeCoordinates(destCoords, false); // Re-geocode destination location

        // Set date and time labels
        dateLabel.setText(route.getDate_start().toLocalDate().toString());
        timeLabel.setText(route.getDate_start().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        distanceLabel.setText(String.format("%.2f km", distance));
        estimatedTimeLabel.setText(formatTime(estimatedTime)); // Format time as x hours x minutes

        // Initialize map
        routeMap = new RouteMap();
        webEngine = mapWebView.getEngine();
        webEngine.loadContent(routeMap.getHtmlContent());

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaController", routeMap);
                webEngine.executeScript("GetMap();");
                updateMap();
            }
        });

        // Fetch delay prediction
        fetchDelayPrediction();
    }

    private void fetchDelayPrediction() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = new Gson().toJson(new PredictionRequest(startCoords[1], startCoords[0], destCoords[1], destCoords[0]));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(flaskApiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        PredictionResponse predictionResponse = new Gson().fromJson(response, PredictionResponse.class);
                        Platform.runLater(() -> {
                            // Display delay information
                            delayLabel.setText(String.format("This business trip might be delayed by %.0f minutes due to weather conditions.", predictionResponse.predicted_delay));

                            // Calculate total time after delay
                            double totalTimeInHours = estimatedTime + (predictionResponse.predicted_delay / 60);
                            expectedTimeAfterDelayLabel.setText("Expected time after delay: " + formatTime(totalTimeInHours));

                            // Display weather conditions
                            weatherConditionLabel.setText(String.format("Weather Conditions: Start - %s, Destination - %s",
                                    predictionResponse.start_weather_condition, predictionResponse.dest_weather_condition));
                        });
                    })
                    .exceptionally(e -> {
                        Platform.runLater(() -> {
                            delayLabel.setText("Delay prediction service is currently unavailable.");
                            expectedTimeAfterDelayLabel.setText("");
                            weatherConditionLabel.setText("");
                        });
                        return null;
                    });
        } catch (Exception e) {
            Platform.runLater(() -> {
                delayLabel.setText("Error fetching delay prediction.");
                expectedTimeAfterDelayLabel.setText("");
                weatherConditionLabel.setText("");
            });
        }
    }

    // Helper method to format time in x hours x minutes
    private String formatTime(double timeInHours) {
        int hours = (int) timeInHours;
        int minutes = (int) ((timeInHours - hours) * 60);
        return String.format("%d hours %d minutes", hours, minutes);
    }

    private double[] parseCoordinates(String location) {
        // Remove the square brackets and split the string by comma
        String[] parts = location.replace("[", "").replace("]", "").split(",");
        double lon = Double.parseDouble(parts[0].trim());
        double lat = Double.parseDouble(parts[1].trim());
        return new double[]{lon, lat};
    }

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

    private void regeocodeCoordinates(double[] coords, boolean isStart) {
        try {
            String url = String.format("https://api.opencagedata.com/geocode/v1/json?q=%f+%f&key=%s", coords[1], coords[0], apiKey);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        String address = parseAddressFromResponse(response);
                        javafx.application.Platform.runLater(() -> {
                            if (isStart) {
                                startLocationLabel.setText(address);
                            } else {
                                destinationLabel.setText(address);
                            }
                        });
                    })
                    .exceptionally(e -> {
                        System.err.println("Error re-geocoding coordinates: " + e.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            System.err.println("Error creating re-geocoding request: " + e.getMessage());
        }
    }

    private String parseAddressFromResponse(String jsonResponse) {
        // Parse the JSON response to extract the formatted address
        org.json.JSONObject json = new org.json.JSONObject(jsonResponse);
        org.json.JSONArray results = json.getJSONArray("results");
        if (results.length() > 0) {
            return results.getJSONObject(0).getString("formatted");
        }
        return "Address not found";
    }

    private void updateMap() {
        if (startCoords != null && destCoords != null) {
            String script = String.format("getRoute([%f, %f], [%f, %f]);", startCoords[0], startCoords[1], destCoords[0], destCoords[1]);
            webEngine.executeScript(script);
        }
    }

    private static class PredictionRequest {
        double start_lat;
        double start_lon;
        double dest_lat;
        double dest_lon;

        public PredictionRequest(double start_lat, double start_lon, double dest_lat, double dest_lon) {
            this.start_lat = start_lat;
            this.start_lon = start_lon;
            this.dest_lat = dest_lat;
            this.dest_lon = dest_lon;
        }
    }

    private static class PredictionResponse {
        double predicted_delay;
        String start_weather_condition;
        String dest_weather_condition;
    }
}