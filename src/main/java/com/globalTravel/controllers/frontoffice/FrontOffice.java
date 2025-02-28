package com.globalTravel.controllers.frontoffice;

import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.models.user.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class FrontOffice {

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
    private Button notificationsButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button userProfileButton;

    private Button currentlySelectedButton; // Track the currently selected button

    private Object controller;
    private User currentUser;

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
        flightManagementButton.setOnAction(e -> navigateToFlightManagement());
        carManagementButton.setOnAction(e -> navigateToCarManagement());
        hotelManagementButton.setOnAction(e -> navigateToHotelManagement());
        routeTrackingButton.setOnAction(e -> navigateToRouteTracking());
        ticketManagementButton.setOnAction(e -> navigateToTicketManagement());
        paymentButton.setOnAction(e -> navigateToPayments());
        notificationsButton.setOnAction(e -> showNotifications());
        settingsButton.setOnAction(e -> openSettings());
        userProfileButton.setOnAction(e -> openUserProfile());
    }

    // Navigation Methods
    private void navigateToFlightManagement() {
        handleButtonSelection(flightManagementButton);
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
        // Implement navigation logic here
    }

    private void navigateToRouteTracking() {
        System.out.println("Navigating to Route Tracking");
        handleButtonSelection(routeTrackingButton);
        // Implement navigation logic here
    }

    private void navigateToTicketManagement() {
        System.out.println("Navigating to Ticket Management");
        handleButtonSelection(ticketManagementButton);
        // Implement navigation logic here
    }

    private void navigateToPayments() {
        System.out.println("Navigating to Payments");
        handleButtonSelection(paymentButton);

        // Implement navigation logic here
    }

    private void showNotifications() {
        System.out.println("Showing Notifications");
        // Implement notification logic here
    }

    private void openSettings() {
        System.out.println("Opening Settings");
        // Implement settings logic here
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
    }
}

