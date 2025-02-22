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
                    "\n" +
                    "    <!-- Leaflet.js for map rendering -->\n" +
                    "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\" />\n" +
                    "    <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>\n" +
                    "\n" +
                    "    <script>\n" +
                    "        var map;\n" +
                    "        var routeLayer;\n" +
                    "        var startMarker;\n" +
                    "        var endMarker;\n" +
                    "\n" +
                    "        function GetMap() {\n" +
                    "            map = L.map('myMap').setView([36.81897, 10.1658], 7);\n" +
                    "\n" +
                    "            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                    "                attribution: '&copy; OpenStreetMap contributors'\n" +
                    "            }).addTo(map);\n" +
                    "\n" +
                    "            var start = [10.1658, 36.81897];\n" +
                    "            var end = [10.1658, 36.81897];\n" +
                    "\n" +
                    "            // Create custom icon for waypoints\n" +
                    "            var waypointIcon = L.icon({\n" +
                    "                iconUrl: '/images/waypoint.png',\n" +
                    "                iconSize: [25, 41], // size of the icon\n" +
                    "                iconAnchor: [12, 41], // point of the icon which will correspond to marker's location\n" +
                    "                popupAnchor: [1, -34] // point from which the popup should open relative to the iconAnchor\n" +
                    "            });\n" +
                    "\n" +
                    "            // Add start marker with label\n" +
                    "            startMarker = L.marker(start, { icon: waypointIcon }).addTo(map)\n" +
                    "                .bindTooltip('Vehicle Location', { permanent: true, className: 'my-labels', offset: [0, 0] });\n" +
                    "\n" +
                    "            // Add end marker with label\n" +
                    "            endMarker = L.marker(end, { icon: waypointIcon }).addTo(map)\n" +
                    "                .bindTooltip('Destination', { permanent: true, className: 'my-labels', offset: [0, 0] });\n" +
                    "\n" +
                    "            getRoute(start, end);\n" +
                    "        }\n" +
                    "\n" +
                    "        function getRoute(start, end) {\n" +
                    "            if (typeof javaController === 'undefined') {\n" +
                    "                console.error(\"javaController is not defined!\");\n" +
                    "                return;\n" +
                    "            }\n" +
                    "\n" +
                    "            var apiKey = javaController.getApiKey(); // Get API key from Java controller\n" +
                    "            var routeUrl = `https://api.openrouteservice.org/v2/directions/driving-car?api_key=${apiKey}&start=${start[0]},${start[1]}&end=${end[0]},${end[1]}`;\n" +
                    "\n" +
                    "            fetch(routeUrl)\n" +
                    "                .then(response => response.json())\n" +
                    "                .then(data => {\n" +
                    "                    console.log(\"API Response:\", data); // Debugging\n" +
                    "\n" +
                    "                    if (data.features && data.features.length > 0) {\n" +
                    "                        var routeCoordinates = data.features[0].geometry.coordinates.map(coord => [coord[1], coord[0]]); // Swap lat/lon\n" +
                    "                        displayRoute(routeCoordinates);\n" +
                    "                    } else {\n" +
                    "                        console.error(\"No route found in API response:\", data);\n" +
                    "                    }\n" +
                    "                })\n" +
                    "                .catch(error => console.error(\"Route API Fetch Error:\", error));\n" +
                    "        }\n" +
                    "\n" +
                    "        function displayRoute(coords) {\n" +
                    "            if (routeLayer) {\n" +
                    "                map.removeLayer(routeLayer);\n" +
                    "            }\n" +
                    "            routeLayer = L.polyline(coords, { color: 'blue', weight: 5 }).addTo(map);\n" +
                    "            map.fitBounds(routeLayer.getBounds());\n" +
                    "        }\n" +
                    "    </script>\n" +
                    "\n" +
                    "    <style>\n" +
                    "        html, body { width: 100%; height: 100%; margin: 0; padding: 0; }\n" +
                    "        #myMap { width: 100%; height: 100%; }\n" +
                    "        .my-labels {\n" +
                    "            background-color: white;\n" +
                    "            border: 1px solid #ccc;\n" +
                    "            padding: 2px 5px;\n" +
                    "            font-size: 12px;\n" +
                    "            white-space: nowrap;\n" +
                    "        }\n" +
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
        // You can add more logic here, such as showing a dialog or updating the UI
    }
}