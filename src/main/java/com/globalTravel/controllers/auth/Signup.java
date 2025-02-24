package com.globalTravel.controllers.auth;

import com.globalTravel.utils.DataSource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class Signup {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField phoneField;
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
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String gender = genderComboBox.getValue();
        LocalDate birthDate = birthDatePicker.getValue();
        String phoneNumber = phoneField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        boolean agreedToTerms = termsCheckBox.isSelected();

        // Vérification des champs vides
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || gender == null || birthDate == null || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont obligatoires.", Alert.AlertType.WARNING);
            return;
        }

        // Vérification de la date de naissance
        if (!isValidBirthDate(birthDate)) {
            showAlert("Erreur", "Vous devez avoir au moins 18 ans et la date de naissance ne peut pas être dans le futur.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification du format de l'email
        if (!isValidEmail(email)) {
            showAlert("Erreur", "Veuillez entrer un email valide.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification du format du numéro de téléphone
        if (!isValidPhoneNumber(phoneNumber)) {
            showAlert("Erreur", "Veuillez entrer un numéro de téléphone valide (8 chiffres).", Alert.AlertType.ERROR);
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
            showAlert("Erreur", "L'email est déjà utilisé.", Alert.AlertType.ERROR);
            return;
        }

        // Hash du mot de passe avec BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Insertion dans la base de données
        String sql = "INSERT INTO user (firstname, lastname, email, genre, date_naissance, phone_number, password, roles, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, gender);
            pstmt.setDate(5, java.sql.Date.valueOf(birthDate));
            pstmt.setString(6, phoneNumber);
            pstmt.setString(7, hashedPassword);
            pstmt.setString(8, "USER"); // Rôle par défaut
            pstmt.setString(9, "Actif"); // Statut par défaut

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

    // Méthode pour valider la date de naissance
    private boolean isValidBirthDate(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        LocalDate minBirthDate = today.minusYears(18); // L'utilisateur doit avoir au moins 18 ans

        // Vérifier que la date de naissance n'est pas dans le futur et que l'utilisateur a au moins 18 ans
        return !birthDate.isAfter(today) && !birthDate.isAfter(minBirthDate);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{8}");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*[A-Z].*") && password.matches(".*\\d.*");
    }

    private boolean userExists(String email) {
        String sql = "SELECT id FROM user WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void navigateToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/login.fxml"));
        Parent root = loader.load();
        emailField.getScene().setRoot(root);
    }

    public void handleTermsAndConditions(ActionEvent actionEvent) {
    }

    public void handleLogin(ActionEvent actionEvent) {
    }
}