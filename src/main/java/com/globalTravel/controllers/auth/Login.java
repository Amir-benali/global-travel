package com.globalTravel.controllers.auth;

import com.globalTravel.utils.DataSource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private Connection conn;

    public Login() {
        conn = DataSource.getInstance().getConnection();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dashboard.fxml"));
            Parent root = loader.load();
            emailField.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le tableau de bord.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.WARNING);
            return;
        }

        String sql = "SELECT password FROM user WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                boolean isCorrectPassword = BCrypt.checkpw(password,storedPassword);

                if (isCorrectPassword){
                    showAlert("Succès", "Connexion réussie !", Alert.AlertType.INFORMATION);
                    navigateToDashboard();
                } else {
                    showAlert("Erreur", "Mot de passe incorrect.", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Erreur", "Utilisateur non trouvé.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Problème de connexion à la base de données.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleForgotPassword() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/reset-password.fxml"));
        Parent root = loader.load();
        emailField.getScene().setRoot(root);
    }

    @FXML
    private void handleSignUp() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/signup.fxml"));
        Parent root = loader.load();
        emailField.getScene().setRoot(root);
    }
}
