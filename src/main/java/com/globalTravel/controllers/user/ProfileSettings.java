package com.globalTravel.controllers.user;

import com.globalTravel.controllers.auth.Login;
import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navbar;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.user.User;
import com.globalTravel.services.user.UserService;
import com.globalTravel.utils.AzureBlobService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class ProfileSettings implements FrontNavigatable, Navigatable {

    @FXML
    private ImageView profileImage;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField roleField;

    @FXML
    private Button uploadImageButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final UserService userService = new UserService();
    private User currentUser;
    private File selectedImageFile;
    private Navbar navbarController; // Référence au contrôleur de la navbar
    private DashBoard dashBoardController;
    private FrontOffice frontOfficeController;

    @FXML
    public void initialize() {
        loadUserData();

        uploadImageButton.setOnAction(event -> handleImageUpload());
        saveButton.setOnAction(event -> handleSaveChanges());
        cancelButton.setOnAction(event -> handleCancel());
    }

    // Méthode pour définir le contrôleur de la navbar
    public void setNavbarController(Navbar navbarController) {
        this.navbarController = navbarController;
    }

    private void loadUserData() {
        currentUser = Login.getCurrentUser();

        if (currentUser != null) {
            firstNameField.setText(currentUser.getFirstName());
            lastNameField.setText(currentUser.getLastName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhoneNumber());
            roleField.setText(currentUser.getRoles());

            if (currentUser.getImage() != null && !currentUser.getImage().isEmpty()) {
                profileImage.setImage(new Image(currentUser.getImage()));
                profileImage.setClip(new Circle(profileImage.getFitWidth() / 2, profileImage.getFitHeight() / 2, Math.min(profileImage.getFitWidth(), profileImage.getFitHeight()) / 2));

        }
        } else {
            System.out.println("⚠ Aucun utilisateur connecté !");
        }
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        selectedImageFile = fileChooser.showOpenDialog(new Stage());

        if (selectedImageFile != null) {
            profileImage.setImage(new Image(selectedImageFile.toURI().toString()));
            profileImage.setClip(new Circle(profileImage.getFitWidth() / 2, profileImage.getFitHeight() / 2, Math.min(profileImage.getFitWidth(), profileImage.getFitHeight()) / 2));

        }
    }

    @FXML
    private void handleSaveChanges() {
        if (!validateInputs()) {
            return;
        }

        // Mettre à jour les informations de l'utilisateur
        currentUser.setFirstName(firstNameField.getText());
        currentUser.setLastName(lastNameField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setPhoneNumber(phoneField.getText());

        // Mettre à jour l'image si une nouvelle image a été sélectionnée
        if (selectedImageFile != null) {
            try {
                String imageUrl = AzureBlobService.uploadImage(selectedImageFile);
                currentUser.setImage(imageUrl); // Sauvegarder l'URL de l'image dans la base de données
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors du téléversement de l'image.");
                return;
            }

        }

        // Sauvegarder les modifications dans la base de données
        try {
            userService.modifier(currentUser);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre profil a été mis à jour avec succès !");

            if (dashBoardController != null) {
                dashBoardController.setCurrentUser(currentUser); // Rafraîchir le tableau de bord
                dashBoardController.navigateTo("dashboard/user/user-table.fxml");
            }
            if (frontOfficeController != null) {
                frontOfficeController.setCurrentUser(currentUser); // Rafraîchir le front office
                frontOfficeController.navigateTo("frontoffice/front-office-content.fxml");
            }
            // Mettre à jour la navbar avec les nouvelles informations
            if (navbarController != null) {
                navbarController.setCurrentUser(currentUser); // Rafraîchir la navbar
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la mise à jour du profil.");
        }

    }

    @FXML
    private void handleCancel() {
        loadUserData();
    }

    private boolean validateInputs() {
        // Vérification des champs vides
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || emailField.getText().isEmpty() || phoneField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return false;
        }

        // Vérification du format de l'email
        if (!isValidEmail(emailField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un email valide.");
            return false;
        }

        // Vérification du format du numéro de téléphone (8 chiffres)
        if (!isValidPhoneNumber(phoneField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un numéro de téléphone valide (8 chiffres).");
            return false;
        }

        return true;
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



    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
    }
}