package com.globalTravel.controllers.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ResetPassword {

    @FXML private TextField emailField;

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText();
        // Implement password reset logic here
        System.out.println("Password reset requested for email: " + email);
    }

    @FXML
    private void handleBackToLogin() throws IOException {
        // Implement navigation back to login page
        System.out.println("Navigating back to login page");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/login.fxml"));
        Parent root= loader.load();
        emailField.getScene().setRoot(root);
    }
}