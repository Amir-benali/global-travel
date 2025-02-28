package com.globalTravel.controllers.flight;

        import com.globalTravel.controllers.backoffice.DashBoard;
        import com.globalTravel.controllers.backoffice.Navigatable;
        import com.globalTravel.models.flight.Flight;
        import com.globalTravel.services.flight.FlightService;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.scene.control.*;
        import javafx.scene.image.Image;
        import javafx.scene.image.ImageView;
        import javafx.scene.layout.*;

        import java.util.List;

        public class FlightGrid implements Navigatable {
            private DashBoard dashBoardController;
            private final FlightService flightService = new FlightService();

            public void setDashBoardController(DashBoard dashBoardController) {
                this.dashBoardController = dashBoardController;
            }

            @FXML
            private VBox flightsGrid;

            @FXML
            public void initialize() {
                loadFlights();
            }

            private void loadFlights() {
                flightsGrid.getChildren().clear();
                List<Flight> flights = flightService.rechercher();
                for (Flight flight : flights) {
                    HBox flightCard = createFlightRow(flight);
                    flightsGrid.getChildren().add(flightCard);
                }
            }

            private HBox createFlightRow(Flight flight) {
                HBox row = new HBox(30);
                row.getStyleClass().add("flight-row");

                // Flight image
                ImageView airlineLogoView = new ImageView(new Image(getClass().getResource("/images/flight.png").toExternalForm()));
                airlineLogoView.setFitWidth(100);
                airlineLogoView.setFitHeight(75);
                airlineLogoView.setPreserveRatio(true);

                // Flight title and airline name and departure+arrival countries
                VBox titleBox = new VBox(5);
                Label routeLabel = new Label(flight.getDeparture_airport() + " → " + flight.getArrival_airport());
                routeLabel.getStyleClass().add("flight-title");

                Label countries=new Label(flight.getDeparture_country()+" → "+flight.getArrival_country());
                countries.getStyleClass().add("flight-countries");

                Label airlineNameLabel = new Label(flight.getAirline_name());
                airlineNameLabel.getStyleClass().add("flight-airline-name");

                Label priceLabel = new Label("Price: $" + String.format("%.2f", flight.getBase_price()));
                priceLabel.getStyleClass().add("flight-price");

                titleBox.getChildren().addAll(routeLabel, countries, airlineNameLabel, priceLabel);

                // Flight information in two columns
                VBox infoBox = new VBox(5);
                HBox infoColumns = new HBox(20);

                VBox column1 = new VBox(5);
                Label flightNumberLabel = new Label("Flight Number: " + flight.getFlight_number());
                Label departureTimeLabel = new Label("Departure Time: " + flight.getDeparture_time());
                Label arrivalTimeLabel = new Label("Arrival Time: " + flight.getArrival_time());
                Label durationLabel = new Label("Duration: " + flight.getDuration() + " H");

                column1.getChildren().addAll(flightNumberLabel, departureTimeLabel, arrivalTimeLabel, durationLabel);

                VBox column2 = new VBox(5);
                Label seatsLabel = new Label("Available Seats: " + flight.getAvailable_seats());
                Label flightStatusLabel = new Label("Status: " + flight.getStatus());

                column2.getChildren().addAll(seatsLabel, flightStatusLabel);

                infoColumns.getChildren().addAll(column1, column2);
                infoBox.getChildren().add(infoColumns);

                // Buttons with icons
                VBox buttonBox = new VBox(5);
                Button updateButton = new Button();
                ImageView updateIcon = new ImageView(new Image(getClass().getResource("/images/update_flight.png").toExternalForm()));
                updateIcon.setFitWidth(20); // Set the desired width
                updateIcon.setFitHeight(20); // Set the desired height
                updateButton.setStyle("-fx-background-color: #1b97e3;");
                updateButton.setGraphic(updateIcon);
                updateButton.setOnAction(e -> handleUpdateFlight(flight));
                Tooltip updateTooltip = new Tooltip("Update Flight");
                Tooltip.install(updateButton, updateTooltip);

                Button deleteButton = new Button();
                ImageView deleteIcon = new ImageView(new Image(getClass().getResource("/images/delete_flight.png").toExternalForm()));
                deleteIcon.setFitWidth(20); // Set the desired width
                deleteIcon.setFitHeight(20); // Set the desired height
                deleteButton.setStyle("-fx-background-color: rgba(248,50,50,0.87);");
                deleteButton.setGraphic(deleteIcon);
                Tooltip deleteTooltip = new Tooltip("Delete Flight");
                Tooltip.install(deleteButton, deleteTooltip);

                deleteButton.setOnAction(e -> handleDeleteFlight(flight));

                buttonBox.getChildren().addAll(updateButton, deleteButton);

                // Spacer to push buttons to the end
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Assemble the row
                row.getChildren().addAll(airlineLogoView, titleBox, infoBox,spacer, buttonBox);

                return row;
            }
            private void handleViewDetails(Flight flight) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Flight Details");
                alert.setHeaderText("Flight Information");
                alert.setContentText("Flight Number: " + flight.getFlight_number() + "\n" +
                        "Airline ID: " + flight.getAirline_name() + "\n" +
                        "Departure Country: " + flight.getDeparture_country() + "\n" +
                        "Arrival Country: " + flight.getArrival_country() + "\n" +
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
                dashBoardController.navigateTo("dashboard/flight/flight-update-form.fxml");
                FlightUpdateForm controller = (FlightUpdateForm) dashBoardController.getController();
                controller.initialize(flight);

            }

            public void addFlight(ActionEvent actionEvent) {
                dashBoardController.navigateTo("dashboard/flight/flight-create-form.fxml");
            }

            public void navigateToAirline(ActionEvent actionEvent) {
                dashBoardController.navigateTo("dashboard/flight/airline-grid.fxml");
            }

            public void navigateToTickets(ActionEvent actionEvent) {
                dashBoardController.navigateTo("dashboard/flight/ticket-grid.fxml");
            }
        }