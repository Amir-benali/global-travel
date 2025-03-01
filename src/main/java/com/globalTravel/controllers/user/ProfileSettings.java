package com.globalTravel.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ProfileSettings {

    @FXML
    private ImageView profileImage; // Profile image view

    @FXML
    private TextField firstNameField; // First name field

    @FXML
    private TextField lastNameField; // Last name field

    @FXML
    private TextField emailField; // Email field

    @FXML
    private TextField phoneField; // Phone field

    @FXML
    private TextField roleField; // Role field (disabled)

    @FXML
    private Button uploadImageButton; // Upload image button

    @FXML
    private Button saveButton; // Save changes button

    @FXML
    private Button cancelButton; // Cancel button

    @FXML
    public void initialize() {
        // Set up button actions
        uploadImageButton.setOnAction(event -> handleImageUpload());
        saveButton.setOnAction(event -> handleSaveChanges());
        cancelButton.setOnAction(event -> handleCancel());
    }

    /**
     * Handle the image upload button click event.
     */
    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // Show the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            // Load the selected image into the profileImage view
            String imagePath = selectedFile.toURI().toString();
            profileImage.setImage(new javafx.scene.image.Image(imagePath));
            profileImage.setClip(new javafx.scene.shape.Circle(profileImage.getFitWidth() / 2, profileImage.getFitHeight() / 2, Math.min(profileImage.getFitWidth(), profileImage.getFitHeight()) / 2));
            System.out.println("Profile picture uploaded: " + imagePath);
        }
    }

    /**
     * Handle the save changes button click event.
     */
    private void handleSaveChanges() {
        // Save the changes to the user's profile
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        // Perform validation and save logic here
        System.out.println("Saving changes...");
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
    }

    /**
     * Handle the cancel button click event.
     */
    private void handleCancel() {
        // Reset the form fields or close the window
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        System.out.println("Changes canceled...");
    }
}