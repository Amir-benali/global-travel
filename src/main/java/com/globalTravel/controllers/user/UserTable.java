package com.globalTravel.controllers.user;


import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class UserTable implements Navigatable {
    private DashBoard dashBoardController;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }
    public DashBoard getDashBoardController() {
        return this.dashBoardController ;
    }
    @FXML
    private TableColumn<?, ?> actionColumn;

    @FXML
    private TableColumn<?, ?> emailColumn;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableColumn<?, ?> phoneColumn;

    @FXML
    private TableColumn<?, ?> roleColumn;

    @FXML
    private TextField searchField;

    @FXML
    private CheckBox selectAllCheckbox;

    @FXML
    private Label selectionLabel;

    @FXML
    private TableView<?> userTable;

    @FXML
    void handleAddUser(ActionEvent event)  {

        if (dashBoardController != null) {
            dashBoardController.navigateTo("dashboard/user/user-form.fxml");
        }
    }

}
