package com.globalTravel.controllers.flight;

        import com.globalTravel.controllers.backoffice.DashBoard;
        import com.globalTravel.controllers.backoffice.Navigatable;
        import com.globalTravel.models.flight.Airline;
        import com.globalTravel.services.flight.AirlineService;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
        import javafx.scene.layout.HBox;
        import javafx.util.Callback;

        import java.util.List;

        public class AirlineGrid implements Navigatable {
            private DashBoard dashBoardController;
            private final AirlineService airlineService = new AirlineService();

            public void setDashBoardController(DashBoard dashBoardController) {
                this.dashBoardController = dashBoardController;
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
            private TableColumn<Airline, Void> actionColumn;

            @FXML
            public void initialize() {
                idColumn.setCellValueFactory(new PropertyValueFactory<>("airline_id"));
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("airline_name"));
                codeColumn.setCellValueFactory(new PropertyValueFactory<>("airline_code"));
                countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
                actionColumn.setCellFactory(new Callback<>() {
                    @Override
                    public TableCell<Airline, Void> call(final TableColumn<Airline, Void> param) {
                        return new TableCell<>() {
                            private final Button editButton = new Button("Edit");
                            private final Button deleteButton = new Button("Delete");

                            {
                                editButton.getStyleClass().add("view-details-button");
                                deleteButton.getStyleClass().add("view-details-button");
                                // Edit Button Action
                                editButton.setOnAction((ActionEvent event) -> {
                                    Airline airline = getTableView().getItems().get(getIndex());
                                    handleEditAirline(airline);
                                });

                                // Delete Button Action
                                deleteButton.setOnAction((ActionEvent event) -> {
                                    Airline airline = getTableView().getItems().get(getIndex());
                                    handleDeleteAirline(airline);
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    HBox buttons = new HBox(editButton, deleteButton);
                                    buttons.setSpacing(5);
                                    setGraphic(buttons);
                                }
                            }
                        };
                    }
                });
                loadAirlines();
            }



            private void handleEditAirline(Airline airline) {
                dashBoardController.navigateTo("dashboard/flight/airline-update-form.fxml");
                AirlineUpdateForm controller = (AirlineUpdateForm) dashBoardController.getController();
                controller.setAirlineToEdit(airline);
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