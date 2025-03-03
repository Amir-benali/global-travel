package com.globalTravel.controllers.auth;

import com.globalTravel.utils.DataSource;
import fi.iki.elonen.NanoHTTPD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.mindrot.jbcrypt.BCrypt;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Signup extends NanoHTTPD {
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

    public static HashMap<String, String> userData = new HashMap<>();

    private Connection conn;

    private static final String HCAPTCHA_SITEKEY = "dfbe2378-f644-45ae-81ca-4838f5720434";
    private static final String HCAPTCHA_SECRET = "ES_020263b5a237461f8952d3e2997834db";

    // Clé API pour Email Validation
    private static final String EMAIL_API_KEY = "7fdf78ad74a44970b1a59a49087dbddc";
    private static final String EMAIL_API_URL = "https://emailvalidation.abstractapi.com/v1/";

    // Local server for hCaptcha
    private static final int CAPTCHA_SERVER_PORT = 8081;

    public Signup() throws IOException {
        super(CAPTCHA_SERVER_PORT); // Start the local server for hCaptcha
        conn = DataSource.getInstance().getConnection();
        startServer();
    }

    private void startServer() {
        if (isPortInUse(CAPTCHA_SERVER_PORT)) {
            terminateProcessUsingPort(CAPTCHA_SERVER_PORT);
        }
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            System.out.println("hCaptcha server running on port " + CAPTCHA_SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isPortInUse(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    private void terminateProcessUsingPort(int port) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // For Windows
                Process process = Runtime.getRuntime().exec("netstat -ano | findstr :" + port);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("LISTENING")) {
                        String[] parts = line.trim().split("\\s+");
                        String pid = parts[parts.length - 1];
                        Runtime.getRuntime().exec("taskkill /PID " + pid + " /F");
                        System.out.println("Terminated process with PID: " + pid);
                    }
                }
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                // For Unix/Linux/Mac
                Process process = Runtime.getRuntime().exec("lsof -t -i:" + port);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String pid = reader.readLine();
                if (pid != null) {
                    Runtime.getRuntime().exec("kill -9 " + pid);
                    System.out.println("Terminated process with PID: " + pid);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        // Serve the hCaptcha widget HTML
        String htmlContent = "<html><body><script src='https://js.hcaptcha.com/1/api.js' async defer></script>" +
                "<form action='' method='POST'><div class='h-captcha' data-sitekey='" + HCAPTCHA_SITEKEY + "'></div></form></body></html>";
        return newFixedLengthResponse(htmlContent);
    }

    @FXML
    public void initialize() {
        // Load the hCaptcha widget from the local server
        // WebEngine webEngine = captchaWebView.getEngine();
        // webEngine.load("http://localhost:" + CAPTCHA_SERVER_PORT);
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

        if (!isValidBirthDate(birthDate)) {
            showAlert("Erreur", "Vous devez avoir au moins 18 ans.", Alert.AlertType.ERROR);
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Erreur", "Veuillez entrer un email valide.", Alert.AlertType.ERROR);
            return;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            showAlert("Erreur", "Numéro de téléphone invalide (8 chiffres).", Alert.AlertType.ERROR);
            return;
        }

        if (!isValidPassword(password)) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 6 caractères, une lettre majuscule et un chiffre.", Alert.AlertType.ERROR);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas.", Alert.AlertType.ERROR);
            return;
        }

        if (!agreedToTerms) {
            showAlert("Erreur", "Vous devez accepter les conditions d'utilisation.", Alert.AlertType.WARNING);
            return;
        }

        if (userExists(email)) {
            showAlert("Erreur", "L'email est déjà utilisé.", Alert.AlertType.ERROR);
            return;
        }

        if (!validateEmailWithAPI(email)) {
            showAlert("Erreur", "L'email n'est pas valide ou n'est pas délivrable.", Alert.AlertType.ERROR);
            return;
        }

        Signup.userData.put("firstName", firstName);
        Signup.userData.put("lastName", lastName);
        Signup.userData.put("email", email);
        Signup.userData.put("gender", gender);
        Signup.userData.put("birthDate", birthDate.toString());
        Signup.userData.put("phoneNumber", phoneNumber);
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Signup.userData.put("hashedPassword", hashedPassword);

        // Si tout est valide, on passe à la page Captcha
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/captcha.fxml"));
            Parent root = loader.load();
            emailField.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page Captcha.", Alert.AlertType.ERROR);
        }
    }

    // Méthode pour valider la date de naissance
    private boolean isValidBirthDate(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        LocalDate minBirthDate = today.minusYears(18); // L'utilisateur doit avoir au moins 18 ans
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

    // Method to stop the hCaptcha server
    public void stopCaptchaServer() {
        try {
            if (this.isAlive()) {
                this.stop();
                System.out.println("hCaptcha server stopped.");
            }
        } catch (Exception e) {
            System.err.println("Error stopping hCaptcha server: " + e.getMessage());
        }
    }

    // Call this method when the application is closing or when captcha validation is complete
    public void onApplicationClose() {
        stopCaptchaServer();
        // Other cleanup code...
    }
}