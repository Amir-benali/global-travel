package com.globalTravel.controllers.user;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserForm implements Navigatable {
    private DashBoard dashBoardController;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private Label formTitle;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<?> roleComboBox;

    @FXML
    void handleBackToList(ActionEvent event) {
        if (dashBoardController != null) {
            System.out.println(dashBoardController);
            dashBoardController.navigateTo("dashboard/user/user-table.fxml");
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {

    }

    @FXML
    void handleSave(ActionEvent event) {

    }

}
