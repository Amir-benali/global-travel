package com.globalTravel.controllers.backoffice;
import com.globalTravel.models.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class Navbar {

    @FXML
    private ImageView imgUser;

    @FXML
    private Label lbRole;




    public void handleSettings(ActionEvent actionEvent) throws IOException {

    }

    public void handleLogout(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/login.fxml"));
        Parent root = loader.load();
        lbRole.getScene().setRoot(root);
    }
    @FXML private MenuButton userMenuButton;
    public void setCurrentUser(User user) {
        if (user != null) {
            System.out.println("Navbar reçoit : " + user.getFirstName() + " " + user.getLastName());
            userMenuButton.setText(user.getFirstName() + " " + user.getLastName());
            if (user.getImage() != null) {
                imgUser.setImage(new Image(user.getImage()));
            }
        } else {
            System.out.println("Aucun utilisateur reçu.");
        }
    }

}
