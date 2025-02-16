package com.globalTravel.controllers.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import java.io.IOException;

public class Login {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private void navigateToDashboard() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dashboard.fxml"));
        try {
            Parent root=loader.load();
            usernameField.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        // Implement login logic here
        System.out.println("Login attempted with username: " + username);

        navigateToDashboard();
    }

    @FXML
    private void handleForgotPassword() throws IOException {
        System.out.println("forgot password");
        // Implement navigation to forgot password page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/reset-password.fxml"));
        Parent root= loader.load();
        usernameField.getScene().setRoot(root);
    }

    @FXML
    private void handleSignUp() throws IOException {
        // Implement navigation to sign up page
        System.out.println("Navigating to sign up page");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/signup.fxml"));
        Parent root= loader.load();
        usernameField.getScene().setRoot(root);
    }
}