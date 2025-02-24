package com.globalTravel.controllers;
import com.globalTravel.models.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class Navbar {

    @FXML
    private ImageView imgUser;

    @FXML
    private Label lbRole;

    @FXML
    private TextField searchField1;

    public void handleProfile(ActionEvent actionEvent) {
    }

    public void handleSettings(ActionEvent actionEvent) {
    }

    public void handleLogout(ActionEvent actionEvent) {
    }
    @FXML private MenuButton userMenuButton;
    public void setCurrentUser(User user) {
        if (user != null) {
            System.out.println("Navbar reçoit : " + user.getFirstName() + " " + user.getLastName());
            userMenuButton.setText(user.getFirstName() + " " + user.getLastName());
        } else {
            System.out.println("Aucun utilisateur reçu.");
        }
    }

}
