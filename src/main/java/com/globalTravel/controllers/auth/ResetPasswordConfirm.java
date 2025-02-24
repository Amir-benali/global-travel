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
import java.sql.*;

public class ResetPasswordConfirm {

    @FXML private TextField tokenField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private Connection conn;

    public ResetPasswordConfirm() {
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
    private void handleResetPasswordConfirm() {
        String token = tokenField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (token.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.WARNING);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas.", Alert.AlertType.ERROR);
            return;
        }

        // Vérifier si le token est valide et n'a pas expiré
        String sql = "SELECT reset_token_expiry FROM user WHERE reset_token = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Timestamp expiryDate = rs.getTimestamp("reset_token_expiry");

                // Vérifier si le token a expiré
                if (expiryDate != null && expiryDate.after(new Timestamp(System.currentTimeMillis()))) {
                    // Token valide, mettre à jour le mot de passe
                    String updateSql = "UPDATE user SET password = ?, reset_token = NULL, reset_token_expiry = NULL WHERE reset_token = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                        updateStmt.setString(1, hashedPassword);
                        updateStmt.setString(2, token);

                        int rowsUpdated = updateStmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            showAlert("Succès", "Votre mot de passe a été réinitialisé.", Alert.AlertType.INFORMATION);
                            handleBackToLogin();
                        } else {
                            showAlert("Erreur", "Token invalide.", Alert.AlertType.ERROR);
                        }
                    }
                } else {
                    showAlert("Erreur", "Token expiré.", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Erreur", "Token invalide.", Alert.AlertType.ERROR);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Problème de connexion à la base de données.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/login.fxml"));
        Parent root = loader.load();
        tokenField.getScene().setRoot(root);
    }
}