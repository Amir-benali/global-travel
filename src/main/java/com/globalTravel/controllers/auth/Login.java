package com.globalTravel.controllers.auth;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.user.User;
import com.globalTravel.services.user.UserService;
import com.globalTravel.utils.DataSource;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
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

    private static User currentUser;

    public Login() {
        conn = DataSource.getInstance().getConnection();
        userService = new UserService();
        prefs = Preferences.userNodeForPackage(Login.class);
    }

    @FXML
    public void initialize() {
        // Make sure passwordField is visible by default
        passwordField.setVisible(true);
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        // Load saved email
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
            String role = (user.getRoles() != null) ? user.getRoles().toLowerCase() : "";

            if (!role.equals("ROLE_ADMIN")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontOffice/front-office.fxml"));
                Parent root = loader.load();
                FrontOffice frontOfficeController = loader.getController();
                frontOfficeController.setCurrentUser(user);
                emailField.getScene().setRoot(root);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dashboard.fxml"));
                Parent root = loader.load();
                DashBoard dashboardController = loader.getController();
                dashboardController.setCurrentUser(user);
                emailField.getScene().setRoot(root);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open dashboard.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = isPasswordVisible ? visiblePasswordField.getText().trim() : passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields.", Alert.AlertType.WARNING);
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
                        currentUser = user;

                        // Handle "Remember Me"
                        if (rememberMeCheckBox.isSelected()) {
                            prefs.put("email", email);
                        } else {
                            prefs.remove("email");
                        }
                        navigateToDashboard(user);
                    } else {
                        showAlert("Error", "User not found.", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Error", "Incorrect password.", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Error", "User not found.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection problem.", Alert.AlertType.ERROR);
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

        ResetPassword resetPasswordController = loader.getController();
        resetPasswordController.setEmail(emailField.getText().trim());

        emailField.getScene().setRoot(root);
    }

    @FXML
    private void handleSignUp() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/signup.fxml"));
        Parent root = loader.load();
        emailField.getScene().setRoot(root);
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    @FXML
    private void handleGoogleLogin() {
        final String clientId = "558069575999-7t1iedje0qqp7mpl0ls4rv8rvtd75tgf.apps.googleusercontent.com";
        final String clientSecret = "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";
        final String redirectUri = "http://localhost:8080/callback";

        final OAuth20Service service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .defaultScope("email profile")
                .callback(redirectUri)
                .build(GoogleApi20.instance());

        try {
            // 1. Open browser for authentication
            Desktop.getDesktop().browse(new URI(service.getAuthorizationUrl()));

            // 2. Setup callback server
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/callback", exchange -> {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    System.out.println("Query: " + query);

                    if (query != null && query.contains("code=")) {
                        String code = query.split("code=")[1].split("&")[0];
                        System.out.println("Received code: " + code);

                        // Response to browser
                        String response = "<html>"
                                + "<head>"
                                + "<style>"
                                + "body { font-family: 'Arial', sans-serif; text-align: center; padding: 20px; background-color: #f5f5f5; }"
                                + ".container { background: white; border-radius: 10px; padding: 30px; max-width: 400px; margin: 50px auto; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }"
                                + "h2 { color: #2E7D32; }"
                                + "p { color: #555; margin-bottom: 20px; }"
                                + ".icon { font-size: 50px; color: #2E7D32; margin-bottom: 20px; }"
                                + "</style>"
                                + "</head>"
                                + "<body>"
                                + "<div class='container'>"
                                + "<div class='icon'>✓</div>"
                                + "<h2>Login Successful</h2>"
                                + "<p>You can now close this window</p>"
                                + "<p>Redirecting to application...</p>"
                                + "<script>"
                                + "setTimeout(function() { window.close(); }, 1500);"  // Auto-close after 1.5s
                                + "</script>"
                                + "</div>"
                                + "</body>"
                                + "</html>";

                        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }

                        // Async processing
                        new Thread(() -> {
                            try {
                                System.out.println("Attempting to get token...");
                                OAuth2AccessToken accessToken = service.getAccessToken(code);
                                System.out.println("Token obtained: " + accessToken.getAccessToken());

                                // Get user info
                                OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v2/userinfo");
                                service.signRequest(accessToken, request);
                                Response userInfoResponse = service.execute(request);

                                if (userInfoResponse.getCode() != 200) {
                                    throw new IOException("Google error: " + userInfoResponse.getBody());
                                }

                                JSONObject userInfo = new JSONObject(userInfoResponse.getBody());
                                String email = userInfo.getString("email").toLowerCase().trim();
                                System.out.println("Email: " + email);

                                User user = userService.getUserByEmail(email);
                                if (user != null) {
                                    Platform.runLater(() -> {
                                        currentUser = user;
                                        navigateToDashboard(user);
                                    });
                                } else {
                                    Platform.runLater(() ->
                                            showAlert("Error", "No user found for: " + email, Alert.AlertType.ERROR));
                                }
                            } catch (Exception e) {
                                System.err.println("Error getting token:");
                                e.printStackTrace();
                                Platform.runLater(() ->
                                        showAlert("Error", "Authentication failed: " + e.getMessage(), Alert.AlertType.ERROR));
                            }
                        }).start();

                    } else {
                        String error = "Missing 'code' parameter";
                        exchange.sendResponseHeaders(400, error.getBytes().length);
                        exchange.getResponseBody().write(error.getBytes());
                        exchange.getResponseBody().close();
                    }
                } finally {
                    server.stop(0);
                }
            });

            server.start();
            System.out.println("Server listening on port 8080");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to start authentication: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}