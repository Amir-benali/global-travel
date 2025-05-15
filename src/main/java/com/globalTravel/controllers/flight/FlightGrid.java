package com.globalTravel.controllers.flight;

            import com.globalTravel.controllers.backoffice.DashBoard;
            import com.globalTravel.controllers.backoffice.Navigatable;
            import com.globalTravel.controllers.frontoffice.FrontNavigatable;
            import com.globalTravel.controllers.frontoffice.FrontOffice;
            import com.globalTravel.models.flight.Flight;
            import com.globalTravel.services.flight.AirlineService;
            import com.globalTravel.services.flight.FlightService;
            import javafx.event.ActionEvent;
            import javafx.fxml.FXML;
            import javafx.scene.Node;
            import javafx.scene.control.*;
            import javafx.scene.image.Image;
            import javafx.scene.image.ImageView;
            import javafx.scene.layout.*;

            import java.util.List;

            public class FlightGrid implements Navigatable, FrontNavigatable {
                @FXML private Button btnNavigateToTicket;
                @FXML private Button btnAddFlight;
                @FXML private Button btnNavigateToAirline;
                @FXML private Label lblFlightList;
                private DashBoard dashBoardController;
                private final FlightService flightService = new FlightService();
                private FrontOffice frontOfficeController;

                public void setDashBoardController(DashBoard dashBoardController) {
                    this.dashBoardController = dashBoardController;
                }

                @FXML
                private VBox flightsGrid;

                @FXML
                public void initialize() {
                    loadFlights();
                    String text = getNewLabelText();
                    lblFlightList.setText(text);

                }

                private String getNewLabelText() {
                    return "Our Flights";
                }

                private void loadFlights() {
                    flightsGrid.getChildren().clear();
                    List<Flight> flights = flightService.rechercher();
                    for (Flight flight : flights) {
                        HBox flightCard = createFlightRow(flight);
                        flightsGrid.getChildren().add(flightCard);
                    }
                }

                private void buttonsVisibility() {
                    for (Node node : flightsGrid.getChildren()) {
                        if (node instanceof HBox flightCard) {
                            for (Node child : flightCard.getChildren()) {
                                if (child instanceof VBox buttonBox && child.getId() != null && child.getId().equals("buttonBox")) {
                                    for (Node button : buttonBox.getChildren()) {
                                        if (button instanceof Button btn) {
                                            if (btn.getText().equals("Update Flight") || btn.getText().equals("Delete Flight")) {
                                                btn.setVisible(false);
                                            }
                                        }
                                    }
                                    if (frontOfficeController != null) {
                                        Button bookButton = new Button("Book Now");
                                        bookButton.setOnAction(e -> handleBookFlight((Flight) flightCard.getUserData()));
                                        buttonBox.getChildren().add(bookButton);
                                    }
                                }
                            }
                        }
                    }
                }

                private HBox createFlightRow(Flight flight) {
                    HBox row = new HBox(30);
                    row.getStyleClass().add("flight-row");
                    row.setUserData(flight);

                    ImageView airlineLogoView = new ImageView(new Image(getClass().getResource("/images/flight.png").toExternalForm()));
                    airlineLogoView.setFitWidth(100);
                    airlineLogoView.setFitHeight(75);
                    airlineLogoView.setPreserveRatio(true);

                    VBox titleBox = new VBox(5);
                    Label routeLabel = new Label(flight.getDeparture_airport() + " → " + flight.getArrival_airport());
                    routeLabel.getStyleClass().add("flight-title");

                    Label countries = new Label(flight.getDeparture_country() + " → " + flight.getArrival_country());
                    countries.getStyleClass().add("flight-countries");

                    AirlineService airlineService = new AirlineService();
                    Label airlineNameLabel = new Label(airlineService.getAirlineNameById(flight.getAirlineId()));
                    airlineNameLabel.getStyleClass().add("flight-airline-name");

                    Label priceLabel = new Label("Price: $" + String.format("%.2f", flight.getBase_price()));
                    priceLabel.getStyleClass().add("flight-price");

                    titleBox.getChildren().addAll(routeLabel, countries, airlineNameLabel, priceLabel);

                    VBox infoBox = new VBox(5);
                    HBox infoColumns = new HBox(20);

                    VBox column1 = new VBox(5);
                    Label flightNumberLabel = new Label("Flight Number: " + flight.getFlight_number());
                    Label departureTimeLabel = new Label("Departure Time: " + flight.getDeparture_time());
                    Label arrivalTimeLabel = new Label("Arrival Time: " + flight.getArrival_time());
                    Label durationLabel = new Label("Duration: " + flight.getDuration() + " H");

                    column1.getChildren().addAll(flightNumberLabel, departureTimeLabel, arrivalTimeLabel, durationLabel);

                    VBox column2 = new VBox(5);
                    Label seatsLabel = new Label("Seats: " + flight.getSeatsNumber());
                    Label flightStatusLabel = new Label("Status: " + flight.getStatus());

                    column2.getChildren().addAll(seatsLabel, flightStatusLabel);

                    infoColumns.getChildren().addAll(column1, column2);
                    infoBox.getChildren().add(infoColumns);

                    VBox buttonBox = new VBox(5);
                    buttonBox.setId("buttonBox");
                    Button updateButton = new Button();
                    updateButton.setText("Update Flight");
                    ImageView updateIcon = new ImageView(new Image(getClass().getResource("/images/update_flight.png").toExternalForm()));
                    updateIcon.setFitWidth(20);
                    updateIcon.setFitHeight(20);
                    updateButton.setStyle("-fx-background-color: #1b97e3;");
                    updateButton.setGraphic(updateIcon);
                    updateButton.setOnAction(e -> handleUpdateFlight(flight));
                    Tooltip updateTooltip = new Tooltip("Update Flight");
                    Tooltip.install(updateButton, updateTooltip);

                    Button deleteButton = new Button();
                    deleteButton.setText("Delete Flight");
                    ImageView deleteIcon = new ImageView(new Image(getClass().getResource("/images/delete_flight.png").toExternalForm()));
                    deleteIcon.setFitWidth(20);
                    deleteIcon.setFitHeight(20);
                    deleteButton.setStyle("-fx-background-color: rgba(248,50,50,0.87);");
                    deleteButton.setGraphic(deleteIcon);
                    Tooltip deleteTooltip = new Tooltip("Delete Flight");
                    Tooltip.install(deleteButton, deleteTooltip);

                    deleteButton.setOnAction(e -> handleDeleteFlight(flight));

                    buttonBox.getChildren().addAll(updateButton, deleteButton);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    row.getChildren().addAll(airlineLogoView, titleBox, infoBox, spacer, buttonBox);

                    return row;
                }

                private void handleViewDetails(Flight flight) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Flight Details");
                    alert.setHeaderText("Flight Information");
                    alert.setContentText("Flight Number: " + flight.getFlight_number() + "\n" +
                            "Airline ID: " + flight.getAirlineId() + "\n" +
                            "Departure Country: " + flight.getDeparture_country() + "\n" +
                            "Arrival Country: " + flight.getArrival_country() + "\n" +
                            "Departure Airport: " + flight.getDeparture_airport() + "\n" +
                            "Arrival Airport: " + flight.getArrival_airport() + "\n" +
                            "Departure Time: " + flight.getDeparture_time() + "\n" +
                            "Arrival Time: " + flight.getArrival_time() + "\n" +
                            "Duration: " + flight.getDuration() + " minutes\n" +
                            "Available Seats: " + flight.getSeatsNumber() + "\n" +
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
                    if (dashBoardController != null) {
                        dashBoardController.navigateTo("dashboard/flight/flight-update-form.fxml");
                        FlightUpdateForm controller = (FlightUpdateForm) dashBoardController.getController();
                        controller.initialize(flight);
                    } else {
                        System.err.println("DashBoardController is not set.");
                    }
                }

                public void addFlight(ActionEvent actionEvent) {
                    if (dashBoardController != null) {
                        dashBoardController.navigateTo("dashboard/flight/flight-create-form.fxml");
                    } else {
                        System.err.println("DashBoardController is not set.");
                    }
                }

                public void navigateToAirline(ActionEvent actionEvent) {
                    if (dashBoardController != null) {
                        dashBoardController.navigateTo("dashboard/flight/airline-grid.fxml");
                    } else {
                        System.err.println("DashBoardController is not set.");
                    }
                }

                public void navigateToTickets(ActionEvent actionEvent) {
                    if (dashBoardController != null) {
                        dashBoardController.navigateTo("dashboard/flight/ticket-grid.fxml");
                    } else {
                        System.err.println("DashBoardController is not set.");
                    }
                }


                @Override
                public void setFrontOfficeController(FrontOffice frontOfficeController) {
                    this.frontOfficeController = frontOfficeController;
                    btnAddFlight.setVisible(false);
                    btnNavigateToTicket.setVisible(false);
                    btnNavigateToAirline.setVisible(false);
                    lblFlightList.setText(getNewLabelText());
                    buttonsVisibility();


                }

                public void handleBookFlight(Flight flight) {
                    if (frontOfficeController != null) {
                        frontOfficeController.navigateTo("frontOffice/flight/booking-form.fxml");
                        BookingController controller = (BookingController) frontOfficeController.getController();
                        controller.initialize(flight, frontOfficeController.getCurrentUser());
                    } else {
                        System.err.println("FrontOfficeController is not set.");
                    }
                }
            }