package com.globalTravel.controllers.frontoffice;

import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.models.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;

import java.io.IOException;

public class FrontOffice implements FrontNavigatable {

    @FXML private ImageView ImgUser;
    @FXML private Button activityTrackingButton;
    @FXML private Button hotelReservationsButton;
    @FXML private Button activitiesButton;
    @FXML private Button dashboardButton;
    @FXML private Label userProfileName;
    @FXML
    private BorderPane mainContainer;  // Root layout container

    // Navigation Buttons
    @FXML
    private Button flightManagementButton;
    @FXML
    private Button carManagementButton;
    @FXML
    private Button hotelManagementButton;
    @FXML
    private Button routeTrackingButton;
    @FXML
    private Button ticketManagementButton;
    @FXML
    private Button paymentButton;

    @FXML
    private MenuButton userProfileButton;

    private Button currentlySelectedButton; // Track the currently selected button

    private Object controller;
    private User currentUser;
    private FrontOffice frontOfficeController;

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    @FXML
    public void initialize() {
        // Load the default content (FrontOfficeContent)
        navigateTo("frontoffice/front-office-content.fxml");

        // Set up navigation buttons
        setupNavigationButtons();
    }

    private void setupNavBar() {
        if (currentUser == null) {
            return;
        }
        userProfileName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        if (currentUser.getImage() != null) {
            ImgUser.setImage(new Image(currentUser.getImage()));

         }
}

    /**
     * Navigate to a specific FXML view.
     *
     * @param fxmlFile The path to the FXML file.
     */
    public void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            Parent view = loader.load();
            Object controller = loader.getController();
            if (controller instanceof FrontNavigatable) {
                ((FrontNavigatable) controller).setFrontOfficeController(this);
            }
            setController(controller);
            mainContainer.setCenter(view);  // Replace center content dynamically
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to handle button selection.
     *
     * @param clickedButton The button that was clicked.
     */
    private void handleButtonSelection(Button clickedButton) {
        if (currentlySelectedButton != null) {
            currentlySelectedButton.getStyleClass().remove("selected");
        }
        clickedButton.getStyleClass().add("selected");
        currentlySelectedButton = clickedButton;
    }

    /**
     * Set up event handlers for navigation buttons.
     */
    private void setupNavigationButtons() {
        dashboardButton.setOnAction(e -> navigateToDashboard());
        flightManagementButton.setOnAction(e -> navigateToFlightManagement());
        carManagementButton.setOnAction(e -> navigateToCarManagement());
        hotelManagementButton.setOnAction(e -> navigateToHotelManagement());
        routeTrackingButton.setOnAction(e -> navigateToRouteTracking());

        ticketManagementButton.setOnAction(e -> navigateToTicketManagement());
        userProfileButton.setOnAction(e -> openUserProfile());
        activityTrackingButton.setOnAction(e -> navigateToActivityList());
        hotelReservationsButton.setOnAction(e -> navigateToHotelReservations());

        paymentButton.setOnAction(e -> navigateToPayments());

    }

    private void navigateToDashboard() {
        handleButtonSelection(dashboardButton);
        navigateTo("frontoffice/front-office-content.fxml");
    }

    private void navigateToHotelReservations() {
        handleButtonSelection(hotelReservationsButton);
        navigateTo("dashboard/hotel/list-reservation-h.fxml");
    }

    private void navigateToActivityList() {
        handleButtonSelection(activityTrackingButton);
        navigateTo("dashboard/activity/activity-calendar.fxml");
    }

    // Navigation Methods
    private void navigateToFlightManagement() {
        handleButtonSelection(flightManagementButton);
        navigateTo("dashboard/flight/flight-grid.fxml");
        // Implement navigation logic here
    }

    private void navigateToCarManagement() {
        System.out.println("Navigating to Car Management");
        handleButtonSelection(carManagementButton);
        navigateTo("dashboard/car/offer-grid.fxml");

        // Implement navigation logic here
    }

    private void navigateToHotelManagement() {
        System.out.println("Navigating to Hotel Management");
        handleButtonSelection(hotelManagementButton);
        navigateTo("dashboard/hotel/hotel-grid.fxml");
        // Implement navigation logic here
    }

    private void navigateToRouteTracking() {
        System.out.println("Navigating to Route Tracking");
        handleButtonSelection(routeTrackingButton);
        navigateTo("dashboard/car/offer-reservation-grid.fxml");
        // Implement navigation logic here
    }

    private void navigateToTicketManagement() {
        System.out.println("Navigating to Ticket Management");
        handleButtonSelection(ticketManagementButton);
        navigateTo("dashboard/flight/ticket-grid.fxml");
        // Implement navigation logic here
    }

    private void navigateToPayments() {
        System.out.println("Navigating to Payments");
        handleButtonSelection(paymentButton);
        navigateTo("dashboard/user/user-payment-grid.fxml");
        // Implement navigation logic here
    }
    public void navigateToActivity( ) {
        handleButtonSelection(activitiesButton);
        navigateTo("dashboard/activity/activity-grid.fxml");
    }



    private void openUserProfile() {
        System.out.println("Opening User Profile");
        // Implement user profile logic here
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        setupNavBar();
    }


    public void logout(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/login.fxml"));
        Parent root = loader.load();
        mainContainer.getScene().setRoot(root);
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController; 
    }

    public void navigateToSettings(ActionEvent actionEvent) {
        navigateTo("user-settings/profile-settings.fxml");


    }
}

