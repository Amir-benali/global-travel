package com.globalTravel.controllers.auth;

import com.globalTravel.utils.DataSource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class CaptchaController {
    @FXML private WebView captchaWebView;

    private static final String HCAPTCHA_SECRET = "ES_020263b5a237461f8952d3e2997834db";

    @FXML
    public void initialize() {
        WebEngine webEngine = captchaWebView.getEngine();
        webEngine.load("http://localhost:8081"); // Charger le Captcha
    }

    @FXML
    private void handleCaptchaValidation() {
        String captchaToken = getCaptchaToken();
        if (captchaToken == null || captchaToken.isEmpty() || !verifyCaptcha(captchaToken)) {
            showAlert("Erreur", "Veuillez valider le Captcha.", Alert.AlertType.ERROR);
        } else {
            insertUserIntoDatabase();
            navigateToLogin(); // Naviguer vers la page de login après validation réussie
        }
    }

    private void insertUserIntoDatabase() {
        // Récupération de la connexion
        Connection conn = DataSource.getInstance().getConnection();
        if (conn == null) {
            showAlert("Erreur", "Connexion à la base de données impossible.", Alert.AlertType.ERROR);
            return;
        }

        // Récupération des données du formulaire (tu devras passer ces valeurs depuis Signup.java)
        String firstName = Signup.userData.get("firstName");
        String lastName = Signup.userData.get("lastName");
        String email = Signup.userData.get("email");
        String gender = Signup.userData.get("gender");
        LocalDate birthDate = LocalDate.parse(Signup.userData.get("birthDate"));
        String phoneNumber = Signup.userData.get("phoneNumber");
        String hashedPassword = Signup.userData.get("hashedPassword");

        // Requête SQL pour insérer un utilisateur
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
            } else {
                showAlert("Erreur", "Échec de l'inscription.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Problème lors de l'insertion en base de données.", Alert.AlertType.ERROR);
        }
    }

    private void navigateToLogin() {
        try {
            // Charger la vue login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/login.fxml"));
            Parent root = loader.load();
            captchaWebView.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de connexion.", Alert.AlertType.ERROR);
        }
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

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void navigateBackToSignup() {
        try {
            // Charger la vue signup.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/signup.fxml"));
            Parent signupView = loader.load();

            // Obtenir la scène actuelle
            Scene currentScene = captchaWebView.getScene();
            currentScene.setRoot(signupView);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de signup.", Alert.AlertType.ERROR);
        }
    }
}