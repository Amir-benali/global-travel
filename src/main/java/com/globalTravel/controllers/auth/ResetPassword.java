package com.globalTravel.controllers.auth;

import com.globalTravel.services.user.EmailService;
import com.globalTravel.utils.DataSource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.*;
import java.util.Calendar;

public class ResetPassword {

    @FXML private TextField emailField;

    private Connection conn;
    public void setEmail(String email) {
        emailField.setText(email);
    }

    public ResetPassword() {
        conn = DataSource.getInstance().getConnection();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer votre adresse email.", Alert.AlertType.WARNING);
            return;
        }

        // Vérifier si l'email existe dans la base de données
        String sql = "SELECT id FROM user WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Générer un token de réinitialisation
                String resetToken = generateResetToken(email);

                // Envoyer un email de réinitialisation
                sendResetEmail(email, resetToken);

                // Ouvrir la page de confirmation de réinitialisation
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/reset-password-confirm.fxml"));
                Parent root = loader.load();

                // Ne pas passer le token à la page de confirmation
                // ResetPasswordConfirm controller = loader.getController();
                // controller.setToken(resetToken); // Supprimé

                emailField.getScene().setRoot(root);
            } else {
                showAlert("Erreur", "Aucun compte trouvé avec cette adresse email.", Alert.AlertType.ERROR);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Problème de connexion à la base de données.", Alert.AlertType.ERROR);
        }
    }

    private String generateResetToken(String email) {
        // Générer un token unique
        String token = "reset-token-" + System.currentTimeMillis();

        // Ajouter une date d'expiration (1 heure)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1); // Expire dans 1 heure
        Timestamp expiryDate = new Timestamp(calendar.getTimeInMillis());

        // Mettre à jour la base de données avec le token et la date d'expiration
        String sql = "UPDATE user SET reset_token = ?, reset_token_expiry = ? WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            pstmt.setTimestamp(2, expiryDate);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Problème de mise à jour du token dans la base de données.", Alert.AlertType.ERROR);
        }

        return token;
    }

    private void sendResetEmail(String email, String resetToken) {
        EmailService.sendResetEmail(email, resetToken);
    }

    @FXML
    private void handleBackToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/login.fxml"));
        Parent root = loader.load();
        emailField.getScene().setRoot(root);
    }
}