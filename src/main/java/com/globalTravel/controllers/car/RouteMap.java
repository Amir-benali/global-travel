package com.globalTravel.controllers.car;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class RouteMap {

    @FXML
    private WebView webView;

    private WebEngine webEngine;

    private String apiKey = "5b3ce3597851110001cf62482c7f9379af9f439789ddb44c6fe4debe"; // Replace with your API key

    @FXML
    public void initialize() {
        webEngine = webView.getEngine();

        // Set up a listener for when the page is fully loaded
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                // Expose the Java object to JavaScript AFTER the page is loaded
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaController", this);
                System.out.println("javaController exposed to JavaScript.");

                // Initialize the map once the page is loaded
                webEngine.executeScript("GetMap();");
            }
        });

        // Load the HTML content
        webEngine.loadContent(getHtmlContent());
    }

    public String getHtmlContent() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Route Map with OpenRouteService</title>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\" />\n" +
                "    <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>\n" +
                "    <script>\n" +
                "        var map;\n" +
                "        var routeLayer;\n" +
                "        var startMarker, endMarker;\n" +
                "        function GetMap() {\n" +
                "            map = L.map('myMap').setView([36.81897, 10.1658], 7);\n" +
                "            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "                attribution: '&copy; OpenStreetMap contributors'\n" +
                "            }).addTo(map);\n" +
                "        }\n" +
                "        function getRoute(start, end) {\n" +
                "            if (typeof javaController === 'undefined') {\n" +
                "                console.error(\"javaController is not defined!\");\n" +
                "                return;\n" +
                "            }\n" +
                "            var apiKey = javaController.getApiKey();\n" +
                "            var routeUrl = `https://api.openrouteservice.org/v2/directions/driving-car?api_key=${apiKey}&start=${start[0]},${start[1]}&end=${end[0]},${end[1]}`;\n" +
                "            fetch(routeUrl)\n" +
                "                .then(response => response.json())\n" +
                "                .then(data => {\n" +
                "                    if (data.features && data.features.length > 0) {\n" +
                "                        var routeCoordinates = data.features[0].geometry.coordinates.map(coord => [coord[1], coord[0]]);\n" +
                "                        displayWaypoints(start, end);\n" +
                "                        displayRoute(routeCoordinates);\n" +
                "                    } else {\n" +
                "                        console.error(\"No route found in API response:\", data);\n" +
                "                    }\n" +
                "                })\n" +
                "                .catch(error => console.error(\"Route API Fetch Error:\", error));\n" +
                "        }\n" +
                "        function displayWaypoints(start, end) {\n" +
                "            if (startMarker) map.removeLayer(startMarker);\n" +
                "            if (endMarker) map.removeLayer(endMarker);\n" +
                "            startMarker = L.circleMarker([start[1], start[0]], { radius: 8, fillColor: 'red', color: 'red', weight: 2, fillOpacity: 0.8 }).addTo(map).bindTooltip('Vehicle will start here', { permanent: true, className: 'my-labels', offset: [0, 0] });\n" +
                "            endMarker = L.circleMarker([end[1], end[0]], { radius: 8, fillColor: 'blue', color: 'blue', weight: 2, fillOpacity: 0.8 }).addTo(map).bindTooltip('Destination', { permanent: true, className: 'my-labels', offset: [0, 0] });\n" +
                "        }\n" +
                "        function displayRoute(coords) {\n" +
                "            if (routeLayer) { map.removeLayer(routeLayer); }\n" +
                "            routeLayer = L.polyline(coords, { color: 'blue', weight: 5 }).addTo(map);\n" +
                "            map.fitBounds(routeLayer.getBounds());\n" +
                "        }\n" +
                "    </script>\n" +
                "    <style>\n" +
                "        html, body { width: 100%; height: 100%; margin: 0; padding: 0; }\n" +
                "        #myMap { width: 100%; height: 100%; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"myMap\"></div>\n" +
                "</body>\n" +
                "</html>";
    }

    public String getApiKey() {
        return apiKey;
    }

    public void onDestinationReached() {
        System.out.println("Destination reached!");
    }
}
