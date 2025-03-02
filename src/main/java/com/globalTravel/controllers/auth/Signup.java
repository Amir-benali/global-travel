package com.globalTravel.controllers.auth;

import com.globalTravel.utils.DataSource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Request;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


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
    @FXML private WebView captchaWebView;


    private Connection conn;

    private static final String HCAPTCHA_SITEKEY = "dfbe2378-f644-45ae-81ca-4838f5720434";
    private static final String HCAPTCHA_SECRET = "ES_020263b5a237461f8952d3e2997834db";


    // Clé API pour Email Validation
    private static final String EMAIL_API_KEY = "7fdf78ad74a44970b1a59a49087dbddc";

    // URL de l'API de validation d'email
    private static final String EMAIL_API_URL = "https://emailvalidation.abstractapi.com/v1/";

    public Signup() {
        conn = DataSource.getInstance().getConnection();
    }

    @FXML
    public void initialize() {
        if (genderComboBox.getItems().isEmpty()) {
            genderComboBox.getItems().addAll("Homme", "Femme");
        }

        // Chargement du widget hCaptcha
        WebEngine webEngine = captchaWebView.getEngine();
        String captchaHTML = "<html><body><script src='https://js.hcaptcha.com/1/api.js' async defer></script>"
                + "<form action='' method='POST'><div class='h-captcha' data-sitekey='" + HCAPTCHA_SITEKEY + "'></div></form></body></html>";
        webEngine.loadContent(captchaHTML);
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

        // Vérification du format du numéro de téléphone (8 chiffres)
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

        // Vérification de l'email via l'API
        if (!validateEmailWithAPI(email)) {
            showAlert("Erreur", "L'email n'est pas valide ou n'est pas délivrable.", Alert.AlertType.ERROR);
            return;
        }

        String captchaToken = getCaptchaToken();
        if (captchaToken == null || captchaToken.isEmpty() || !verifyCaptcha(captchaToken)) {
            showAlert("Erreur", "Veuillez valider le Captcha.", Alert.AlertType.ERROR);
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

    // Méthode pour valider le format de l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    // Méthode pour valider le format du numéro de téléphone (8 chiffres)
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{8}");
    }

    // Méthode pour valider le mot de passe
    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*[A-Z].*") && password.matches(".*\\d.*");
    }

    // Méthode pour vérifier si l'utilisateur existe déjà
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

    // Méthode pour valider l'email via l'API
    private boolean validateEmailWithAPI(String email) {
        try {
            String apiUrl = EMAIL_API_URL + "?api_key=" + EMAIL_API_KEY + "&email=" + email;
            Content content = Request.get(apiUrl).execute().returnContent();

            // Parse the JSON response
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(content.asString());

            // Extracting the relevant fields from the response
            boolean isValidFormat = (boolean) ((JSONObject) jsonResponse.get("is_valid_format")).get("value");
            boolean isDeliverable = "DELIVERABLE".equals(jsonResponse.get("deliverability"));
            boolean isDisposableEmail = (boolean) ((JSONObject) jsonResponse.get("is_disposable_email")).get("value");

            // Return true only if the email is valid, deliverable, and not disposable
            return isValidFormat && isDeliverable && !isDisposableEmail;

        } catch (IOException e) {
            System.out.println("Email validation error: " + e.getMessage());
            return false;
        } catch (ParseException e) {
            System.out.println("Error parsing email validation response: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Unexpected error during email validation: " + e.getMessage());
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

    private String getCaptchaToken() {
        return (String) captchaWebView.getEngine().executeScript("document.querySelector('textarea[name=h-captcha-response]').value");
    }

    private boolean verifyCaptcha(String token) {
        try {
            String response = Request.post("https://api.hcaptcha.com/siteverify")
                    .bodyForm(Form.form()
                            .add("secret", HCAPTCHA_SECRET)
                            .add("response", token)
                            .build())
                    .execute().returnContent().asString();

            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response);
            return (boolean) jsonResponse.get("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }




}