package com.globalTravel.controllers.auth;

import com.globalTravel.utils.DataSource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Signup {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox termsCheckBox;

    private Connection conn;

    public Signup() {
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
    private void handleSignup() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        boolean agreedToTerms = termsCheckBox.isSelected();


        // Vérification des champs vides
        if (fullName.isEmpty() || email.isEmpty() ||  password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont obligatoires.", Alert.AlertType.WARNING);
            return;
        }

        // Vérification du format de full name
        if (!(fullName.trim().contains(" ") && fullName.trim().split("\s+").length >= 2)){
            showAlert("Erreur", "Veuillez entrer un fullname valide.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification du format de l'email
        if (!isValidEmail(email)) {
            showAlert("Erreur", "Veuillez entrer un email valide.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification du mot de passe
        if (!isValidPassword(password)) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 6 caractères, une lettre majuscule et un chiffre.", Alert.AlertType.ERROR);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification des conditions d'utilisation
        if (!agreedToTerms) {
            showAlert("Erreur", "Vous devez accepter les conditions d'utilisation.", Alert.AlertType.WARNING);
            return;
        }

        // Vérifier si l'utilisateur existe déjà
        if (userExists(email)) {
            showAlert("Erreur", "Le nom d'utilisateur ou l'email est déjà utilisé.", Alert.AlertType.ERROR);
            return;
        }

        String[] parts = fullName.trim().split("\s+", 2);
        String firstName = parts[0];
        String lastName = parts[1];

        // Hash du mot de passe avec BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Insertion dans la base de données
        String sql = "INSERT INTO user (firstname,lastname, email, password, roles, statut) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, hashedPassword);
            pstmt.setString(5, "USER"); // Par défaut, nouvel utilisateur est un "USER"
            pstmt.setString(6, "Actif"); // Statut par défaut

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Succès", "Compte créé avec succès !", Alert.AlertType.INFORMATION);
                navigateToLogin();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec de l'inscription. Essayez encore.", Alert.AlertType.ERROR);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*[A-Z].*") && password.matches(".*\\d.*");
    }

    private boolean userExists(String email) {
        String sql = "SELECT id FROM user WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // True si l'utilisateur existe déjà
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void navigateToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/login.fxml"));
        Parent root = loader.load();
        emailField.getScene().setRoot(root);
    }

    @FXML
    private void handleTermsAndConditions() {
        showAlert("Information", "Affichage des conditions d'utilisation...", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleLogin() throws IOException {
        navigateToLogin();
    }
}
