package com.globalTravel.controllers;

    import com.globalTravel.controllers.flight.FlightUpdateForm;
    import com.globalTravel.controllers.flight.TicketUpdateForm;
    import com.globalTravel.models.flight.Flight;
    import com.globalTravel.models.flight.Ticket;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.control.Button;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.Parent;
    import java.io.IOException;

    public class DashBoard {

        @FXML
        private BorderPane mainContainer;  // Root layout container

        @FXML
        private Button btnSideActivity;

        @FXML
        private Button btnSideCar;

        @FXML
        private Button btnSideDashboard;

        @FXML
        private Button btnSideFlight;

        @FXML
        private Button btnSideHotel;

        @FXML
        private Button btnSideUser;

        private Button currentlySelectedButton; // Track the currently selected button
        private Object controller;

        public void navigateTo(String fxmlFile) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
                Parent view = loader.load();

                // Set reference to this DashBoard in the new controller (if applicable)
                Object controller = loader.getController();
                if (controller instanceof Navigatable) {
                    ((Navigatable) controller).setDashBoardController(this);
                }
                this.controller = controller;

                mainContainer.setCenter(view);  // Replace center content dynamically
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Helper method to handle button selection
        private void handleButtonSelection(Button clickedButton) {
            // Remove the "selected" style class from the previously selected button
            if (currentlySelectedButton != null) {
                currentlySelectedButton.getStyleClass().remove("selected");
            }

            // Add the "selected" style class to the clicked button
            clickedButton.getStyleClass().add("selected");

            // Update the currently selected button
            currentlySelectedButton = clickedButton;
        }

        @FXML
        void navigateToActivity(ActionEvent event) {
            handleButtonSelection(btnSideActivity);
            navigateTo("dashboard/activity/activity-grid.fxml");
        }

        @FXML
        void navigateToCar(ActionEvent event) {
            handleButtonSelection(btnSideCar);
            navigateTo("dashboard/car/car-grid.fxml");
        }

        @FXML
        void navigateToDashboard(ActionEvent event) {
            handleButtonSelection(btnSideDashboard);
            navigateTo("dashboard/dashboard-content.fxml");
        }

        @FXML
        void navigateToFlight(ActionEvent event) {
            handleButtonSelection(btnSideFlight);
            navigateTo("dashboard/flight/flight-grid.fxml");
        }

        @FXML
        void navigateToHotel(ActionEvent event) {
            handleButtonSelection(btnSideHotel);
            navigateTo("dashboard/hotel/hotel-grid.fxml");
        }

        @FXML
        void navigateToUser(ActionEvent event) {
            handleButtonSelection(btnSideUser);
            navigateTo("dashboard/user/user-table.fxml");
        }

        @FXML
        public void initialize() {
            // Set the initial selected button (e.g., "User" button)
            handleButtonSelection(btnSideUser);

            // Load the initial content
            navigateTo("dashboard/user/user-table.fxml");
        }

        public void Logout(ActionEvent actionEvent) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/login.fxml"));
            Parent root= loader.load();
            mainContainer.getScene().setRoot(root);
        }

        public Object getController() {
            return controller;
        }

        public void setController(Object controller) {
            this.controller = controller;
        }
        public void navigateToUpdate(String fxmlPath, Object object) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlPath));
                Parent root = loader.load();

                if (fxmlPath.equals("dashboard/flight/flight-update-form.fxml")) {
                    FlightUpdateForm controller = loader.getController();
                    controller.setFlightToEdit((Flight) object);
                }

                //if (fxmlPath.equals("dashboard/flight/ticket-update-form.fxml")) {
                  //  TicketUpdateForm controller = loader.getController();
                    //controller.setTicketToEdit((Ticket) object);
               // }

                //if (fxmlPath.equals("dashboard/flight/airline-update-form.fxml")) {
                  //  AirlineUpdateForm controller = loader.getController();
                    //controller.setAirlineToEdit((Airline) object);
                //}

                mainContainer.setCenter(root);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }