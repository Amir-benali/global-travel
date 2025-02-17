package com.globalTravel.controllers.user;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.user.User;
import com.globalTravel.services.user.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Date;
import java.time.LocalDate;

public class UserForm implements Navigatable {
    private DashBoard dashBoardController;
    private UserService userService = new UserService();
    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private TextField emailField;

    @FXML
    private Label formTitle;

    @FXML
    private TextField lastNameField;
    @FXML
    private TextField firstNameField;


    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<String> roleComboBox;

    private User currentUser;
    @FXML
    void handleBackToList(ActionEvent event) {
        if (dashBoardController != null) {
            System.out.println(dashBoardController);
            dashBoardController.navigateTo("dashboard/user/user-table.fxml");
        }
    }

    @FXML
    public void initialize(User user){
        currentUser= user;
        populateUserForm();
    }

    private void populateUserForm() {
        roleComboBox.getItems().addAll("Responsable","Employee","Admin");
        if(currentUser != null){
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhoneNumber());
            lastNameField.setText(currentUser.getLastName());
            firstNameField.setText(currentUser.getFirstName());
            birthDatePicker.setValue(LocalDate.parse(currentUser.getDateNaissance().toString()));
            roleComboBox.setValue(currentUser.getRoles());
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {

    }

    @FXML
    void handleSave(ActionEvent event) {
        validateUserForm();
        currentUser.setEmail(emailField.getText());
        currentUser.setPhoneNumber(phoneField.getText());
        currentUser.setLastName(lastNameField.getText());
        currentUser.setFirstName(firstNameField.getText());
        currentUser.setRoles(roleComboBox.getValue());
        currentUser.setDateNaissance(Date.valueOf(birthDatePicker.getValue()));
        userService.modifier(currentUser);

        dashBoardController.navigateTo("dashboard/user/user-table.fxml");


    }

    private void validateUserForm() {
    }


}
