package com.globalTravel.controllers.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Signup {
    @FXML
    private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox termsCheckBox;

    @FXML
    private void handleSignup() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        boolean agreedToTerms = termsCheckBox.isSelected();

        // Implement signup logic here
        System.out.println("Signup attempted for user: " + username);
        System.out.println("Full Name: " + fullName);
        System.out.println("Email: " + email);
        System.out.println("Agreed to Terms: " + agreedToTerms);
    }

    @FXML
    private void handleTermsAndConditions() {
        // Implement navigation to terms and conditions page
        System.out.println("Navigating to Terms and Conditions page");
    }

    @FXML
    private void handleLogin() throws IOException {
        // Implement navigation to login page
        System.out.println("Navigating to Login page");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/login.fxml"));
        Parent root= loader.load();
        emailField.getScene().setRoot(root);
    }
}
