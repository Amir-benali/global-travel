package com.globalTravel.controllers.flight;

        import com.globalTravel.controllers.DashBoard;
        import com.globalTravel.controllers.Navigatable;
        import com.globalTravel.models.flight.Airline;
        import com.globalTravel.services.flight.AirlineService;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.scene.control.Alert;
        import javafx.scene.control.ButtonType;
        import javafx.scene.control.TableColumn;
        import javafx.scene.control.TableView;
        import javafx.scene.control.cell.PropertyValueFactory;

        import java.util.List;

        public class AirlineGrid implements Navigatable {
            private DashBoard dashBoardController;
            private final AirlineService airlineService = new AirlineService();

            public void setDashBoardController(DashBoard dashBoardController) {
            }

            @FXML
            private TableView<Airline> airlineTable;
            @FXML
            private TableColumn<Airline, Integer> idColumn;
            @FXML
            private TableColumn<Airline, String> nameColumn;
            @FXML
            private TableColumn<Airline, String> codeColumn;
            @FXML
            private TableColumn<Airline, String> countryColumn;

            @FXML
            public void initialize() {
                idColumn.setCellValueFactory(new PropertyValueFactory<>("airline_id"));
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("airline_name"));
                codeColumn.setCellValueFactory(new PropertyValueFactory<>("airline_code"));
                countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));

                loadAirlines();
            }

            private void loadAirlines() {
                List<Airline> airlines = airlineService.getAllAirlines();
                airlineTable.getItems().setAll(airlines);
            }

            private void handleViewDetails(Airline airline) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Airline Details");
                alert.setHeaderText("Airline Information");
                alert.setContentText("Airline ID: " + airline.getAirline_id() + "\n" +
                        "Name: " + airline.getAirline_name() + "\n" +
                        "Code: " + airline.getAirline_code() + "\n" +
                        "Country: " + airline.getCountry());
                alert.showAndWait();
            }

            private void handleDeleteAirline(Airline airline) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Airline");
                alert.setHeaderText("Are you sure you want to delete this airline?");
                alert.setContentText("Airline ID: " + airline.getAirline_id());

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        airlineService.supprimer(airline);
                        loadAirlines();
                    }
                });
            }

            private void handleUpdateAirline(Airline airline) {
                //dashBoardController.navigateTo("dashboard/flight/airline-update-form.fxml", airline);
            }

            public void addAirline(ActionEvent actionEvent) {
                dashBoardController.navigateTo("dashboard/flight/airline-create-form.fxml");
            }
        }