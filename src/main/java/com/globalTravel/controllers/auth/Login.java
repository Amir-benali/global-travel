package com.globalTravel.controllers.auth;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.models.user.User;
import com.globalTravel.services.user.UserService;
import com.globalTravel.utils.DataSource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class Login {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private ImageView togglePasswordIcon;
    @FXML private CheckBox rememberMeCheckBox;

    private Connection conn;
    private UserService userService;
    private Preferences prefs;
    private boolean isPasswordVisible = false;

    public Login() {
        conn = DataSource.getInstance().getConnection();
        userService = new UserService();
        prefs = Preferences.userNodeForPackage(Login.class);
    }

    @FXML
    public void initialize() {
        // Assurez-vous que le champ passwordField est visible au démarrage
        passwordField.setVisible(true);
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        // Charger l'email sauvegardé
        String savedEmail = prefs.get("email", "");
        if (!savedEmail.isEmpty()) {
            emailField.setText(savedEmail);
            rememberMeCheckBox.setSelected(true);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dashboard.fxml"));
            Parent root = loader.load();
            DashBoard dashboardController = loader.getController();
            dashboardController.setCurrentUser(user);
            emailField.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le tableau de bord.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = isPasswordVisible ? visiblePasswordField.getText().trim() : passwordField.getText().trim();

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
                boolean isCorrectPassword = BCrypt.checkpw(password, storedPassword);

                if (isCorrectPassword) {
                    User user = userService.getUserByEmail(email);
                    if (user != null) {
                        if (rememberMeCheckBox.isSelected()) {
                            prefs.put("email", email);
                        } else {
                            prefs.remove("email");
                        }
                        navigateToDashboard(user);
                    } else {
                        showAlert("Erreur", "Utilisateur introuvable.", Alert.AlertType.ERROR);
                    }
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
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            togglePasswordIcon.setImage(new Image(getClass().getResourceAsStream("/images/eye-closed.png")));
        } else {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordIcon.setImage(new Image(getClass().getResourceAsStream("/images/eye-outline.png")));
        }
        isPasswordVisible = !isPasswordVisible;
    }

    @FXML
    private void handleForgotPassword() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/reset-password.fxml"));
        Parent root = loader.load();

        // Passer l'email à la page de réinitialisation de mot de passe
        ResetPassword resetPasswordController = loader.getController();
        resetPasswordController.setEmail(emailField.getText().trim()); // Pré-remplir le champ email

        emailField.getScene().setRoot(root);
    }

    @FXML
    private void handleSignUp() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/signup.fxml"));
        Parent root = loader.load();
        emailField.getScene().setRoot(root);
    }
}


