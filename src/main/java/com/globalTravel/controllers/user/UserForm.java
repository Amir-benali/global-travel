package com.globalTravel.controllers.user;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.models.user.User;
import com.globalTravel.services.user.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class UserForm implements Navigatable {
    private DashBoard dashBoardController;
    private final UserService userService = new UserService();

    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private TextField emailField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField adresseField;
    @FXML
    private ComboBox<String> roleComboBox;

    private User currentUser;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    void handleBackToList(ActionEvent event) {
        if (dashBoardController != null) {
            dashBoardController.navigateTo("dashboard/user/user-table.fxml");
        }
    }

    @FXML
    public void initialize(User user) {
        currentUser = user;
        populateUserForm();
    }

    private void populateUserForm() {
        roleComboBox.getItems().addAll("Responsable", "Employee", "Admin");

        if (currentUser != null) {
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhoneNumber());
            lastNameField.setText(currentUser.getLastName());
            firstNameField.setText(currentUser.getFirstName());
            birthDatePicker.setValue(LocalDate.parse(currentUser.getDateNaissance().toString()));
            roleComboBox.setValue(currentUser.getRoles());
            adresseField.setText(currentUser.getAdresse());
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        if (showConfirmationDialog("Confirmation", "Êtes-vous sûr de vouloir annuler ?")) {
            if (dashBoardController != null) {
                dashBoardController.navigateTo("dashboard/user/user-table.fxml");
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (!validateUserForm()) return;

        String newEmail = emailField.getText();

        // Vérifier si l'email existe déjà et ne correspond pas à l'utilisateur actuel
        if (userService.emailExists(newEmail)) {
            User existingUser = userService.getUserByEmail(newEmail);
            if (existingUser != null && existingUser.getId() != currentUser.getId()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Cet email est déjà utilisé par un autre utilisateur.");
                return;
            }
        }

        if (showConfirmationDialog("Confirmation", "Voulez-vous vraiment mettre à jour cet utilisateur ?")) {
            currentUser.setEmail(newEmail);
            currentUser.setPhoneNumber(phoneField.getText());
            currentUser.setLastName(lastNameField.getText());
            currentUser.setFirstName(firstNameField.getText());
            currentUser.setRoles(roleComboBox.getValue());
            currentUser.setDateNaissance(Date.valueOf(birthDatePicker.getValue()));
            currentUser.setAdresse(adresseField.getText());

            userService.modifier(currentUser);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur mis à jour avec succès.");
            dashBoardController.navigateTo("dashboard/user/user-table.fxml");
        }
    }

    private boolean validateUserForm() {
        String email = emailField.getText();
        String phone = phoneField.getText();
        String lastName = lastNameField.getText();
        String firstName = firstNameField.getText();
        String role = roleComboBox.getValue();
        LocalDate birthDate = birthDatePicker.getValue();

        // Vérification des champs obligatoires
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || phone.isBlank() || birthDate == null || role == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Tous les champs sont obligatoires sauf l'adresse.");
            return false;
        }

        // Vérification email
        if (!Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$", email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez saisir une adresse email valide.");
            return false;
        }

        // Vérification numéro de téléphone (8 chiffres)
        if (!Pattern.matches("^\\d{8}$", phone)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le numéro de téléphone doit contenir exactement 8 chiffres.");
            return false;
        }

        // Vérification de la date de naissance
        if (!isValidBirthDate(birthDate)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Vous devez avoir au moins 18 ans et la date de naissance ne peut pas être dans le futur.");
            return false;
        }

        return true;
    }

    // Méthode pour valider la date de naissance
    private boolean isValidBirthDate(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        LocalDate minBirthDate = today.minusYears(18); // L'utilisateur doit avoir au moins 18 ans

        // Vérifier que la date de naissance n'est pas dans le futur et que l'utilisateur a au moins 18 ans
        return !birthDate.isAfter(today) && !birthDate.isAfter(minBirthDate);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}