package com.globalTravel.controllers.flight;

        import com.globalTravel.controllers.DashBoard;
        import com.globalTravel.controllers.Navigatable;
        import com.globalTravel.models.flight.Flight;
        import com.globalTravel.services.flight.FlightService;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.scene.control.Alert;
        import javafx.scene.control.Button;
        import javafx.scene.control.ButtonType;
        import javafx.scene.control.Label;
        import javafx.scene.image.Image;
        import javafx.scene.image.ImageView;
        import javafx.scene.layout.FlowPane;
        import javafx.scene.layout.HBox;
        import javafx.scene.layout.VBox;

        import java.util.List;

        public class FlightGrid implements Navigatable {
            private DashBoard dashBoardController;
            private final FlightService flightService = new FlightService();

            public void setDashBoardController(DashBoard dashBoardController) {
                this.dashBoardController = dashBoardController;
            }

            @FXML
            private FlowPane flightsGrid;

            @FXML
            public void initialize() {
                loadFlights();
            }

            private void loadFlights() {
                flightsGrid.getChildren().clear();
                List<Flight> flights = flightService.rechercher();
                for (Flight flight : flights) {
                    VBox flightCard = createFlightCard(flight);
                    flightsGrid.getChildren().add(flightCard);
                }
            }

            private VBox createFlightCard(Flight flight) {
                VBox card = new VBox(10);
                card.getStyleClass().add("flight-offer-card");

                ImageView airlineLogoView = new ImageView(new Image("/images/logo.jpg"));
                airlineLogoView.setFitWidth(200);
                airlineLogoView.setFitHeight(150);
                airlineLogoView.setPreserveRatio(true);

                VBox flightInfo = new VBox(5);
                flightInfo.getStyleClass().add("flight-info");

                Label routeLabel = new Label(flight.getDeparture_airport() + " to " + flight.getArrival_airport());
                routeLabel.getStyleClass().add("flight-title");

                Label priceLabel = new Label("Price: $" + String.format("%.2f", flight.getBase_price()));
                priceLabel.getStyleClass().add("flight-price");

                Button viewDetailsButton = new Button("View Details");
                viewDetailsButton.getStyleClass().add("view-details-button");
                viewDetailsButton.setOnAction(e -> handleViewDetails(flight));

                Button deleteButton = new Button("Delete");
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setOnAction(e -> handleDeleteFlight(flight));

                Button updateButton = new Button("Update");
                updateButton.getStyleClass().add("update-button");
                updateButton.setOnAction(e -> handleUpdateFlight(flight));

                HBox buttonHbox = new HBox(3);
                buttonHbox.getChildren().addAll(viewDetailsButton, updateButton, deleteButton);
                flightInfo.getChildren().addAll(routeLabel, priceLabel, buttonHbox);

                card.getChildren().addAll(airlineLogoView, flightInfo);

                return card;
            }

            private void handleViewDetails(Flight flight) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Flight Details");
                alert.setHeaderText("Flight Information");
                alert.setContentText("Flight Number: " + flight.getFlight_number() + "\n" +
                        "Airline ID: " + flight.getAirline_id() + "\n" +
                        "Departure Airport: " + flight.getDeparture_airport() + "\n" +
                        "Arrival Airport: " + flight.getArrival_airport() + "\n" +
                        "Departure Time: " + flight.getDeparture_time() + "\n" +
                        "Arrival Time: " + flight.getArrival_time() + "\n" +
                        "Duration: " + flight.getDuration() + " minutes\n" +
                        "Available Seats: " + flight.getAvailable_seats() + "\n" +
                        "Price: $" + flight.getBase_price() + "\n" +
                        "Status: " + flight.getStatus());
                alert.showAndWait();
            }

            private void handleDeleteFlight(Flight flight) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Flight");
                alert.setHeaderText("Are you sure you want to delete this flight?");
                alert.setContentText("Flight Number: " + flight.getFlight_number());

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        flightService.supprimer(flight);
                        loadFlights();
                    }
                });
            }

            private void handleUpdateFlight(Flight flight) {
                dashBoardController.navigateTo("dashboard/flight/flight-update-form.fxml", flight);
            }

            public void addFlight(ActionEvent actionEvent) {
                dashBoardController.navigateTo("dashboard/flight/flight-create-form.fxml");
            }
        }