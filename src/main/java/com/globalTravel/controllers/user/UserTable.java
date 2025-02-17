package com.globalTravel.controllers.user;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.user.Employee;
import com.globalTravel.models.user.User;
import com.globalTravel.services.user.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class UserTable implements Navigatable {
    private DashBoard dashBoardController;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> genreColumn;
    @FXML private TableColumn<User, Date> dateNaissanceColumn;
    @FXML private TableColumn<User, String> adresseColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> phoneNumberColumn;
    @FXML private TableColumn<User, String> rolesColumn;
    @FXML private TableColumn<User, String> statutColumn;
    @FXML private TableColumn<User, String> posteColumn;
    @FXML private TableColumn<User, Void> actionColumn; // New Action Column
    @FXML private TextField searchField;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private UserService userService= new UserService();

    private ObservableList<User> users ;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    public DashBoard getDashBoardController() {
        return this.dashBoardController;
    }

    @FXML
    public void initialize() throws SQLException {
        // Bind columns to Employee attributes
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        dateNaissanceColumn.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roles"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
//        posteColumn.setCellValueFactory(new PropertyValueFactory<>("poste"));
        // Set up the Action Column
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        editButton.getStyleClass().add("view-details-button");
                        deleteButton.getStyleClass().add("view-details-button");
                        // Edit Button Action
                        editButton.setOnAction((ActionEvent event) -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleEditUser(user);
                        });

                        // Delete Button Action
                        deleteButton.setOnAction((ActionEvent event) -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleDeleteUser(user);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(editButton, deleteButton);
                            buttons.setSpacing(5);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        });
        // load users
        users = FXCollections.observableArrayList(userService.rechercher());
        userTable.setItems(users);

        // Enable/disable edit and delete buttons based on selection
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        ObservableList<User> filteredUsers = FXCollections.observableArrayList();

        for (User user : users) {
            if (user.getFirstName().toLowerCase().contains(keyword) ||
                    user.getEmail().toLowerCase().contains(keyword)) {
                filteredUsers.add(user);
            }
        }
        userTable.setItems(filteredUsers);
    }

    private void handleEditUser(User user) {
        if (user != null && dashBoardController != null) {
            dashBoardController.navigateTo("dashboard/user/user-form.fxml");
            UserForm controller = (UserForm) dashBoardController.getController();
            controller.initialize(user);
        }
    }

    private void handleDeleteUser(User user) {
        if (user == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'utilisateur");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + user.getFirstName() + " " + user.getLastName() + " ?");

        // Récupérer la réponse de l'utilisateur
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Supprimer l'utilisateur si l'utilisateur confirme
            userService.supprimer(user);

            // Recharger la liste après suppression
            users = FXCollections.observableArrayList(userService.rechercher());
            userTable.setItems(users);
        }
    }



}