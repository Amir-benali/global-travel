package com.globalTravel.controllers.car;

import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.car.CarReservation;
import com.globalTravel.models.car.Offer;
import com.globalTravel.models.car.Route;
import com.globalTravel.models.car.TypeCarReservation;
import com.globalTravel.services.car.CarReservationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.HBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class OfferReservationGrid implements FrontNavigatable {

    @FXML private TableView<CarReservation> reservationsTable;
    @FXML private TableColumn<CarReservation, Integer> idColumn;
    @FXML private TableColumn<CarReservation, Date> dateColumn;
    @FXML private TableColumn<CarReservation, TypeCarReservation> statusColumn;
    @FXML private TableColumn<CarReservation, String> routeColumn;
    @FXML private TableColumn<CarReservation, String> offerColumn;
    @FXML private TableColumn<CarReservation, String> userColumn;
    @FXML private TableColumn<CarReservation, Void> actionsColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<TypeCarReservation> statusFilter;
    @FXML private Label pageInfo;
    @FXML private Button prevButton;
    @FXML private Button nextButton;

    private CarReservationService carReservationService = new CarReservationService();
    private ObservableList<CarReservation> reservations = FXCollections.observableArrayList();
    private FilteredList<CarReservation> filteredReservations;

    private int currentPage = 1;
    private int itemsPerPage = 5;
    private int totalPages = 1;
    private FrontOffice frontOfficeController;

    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        loadReservations();
        updatePagination();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status")); // Bind to status property

        // Custom cell factory for status column to display styled flairs
        statusColumn.setCellFactory(column -> new TableCell<>() {
            private final Label statusLabel = new Label();

            {
                // Set a fixed size for the label to ensure all flairs are the same size
                statusLabel.setMinWidth(110); // Adjust the width as needed
                statusLabel.setMaxWidth(110); // Adjust the width as needed
                statusLabel.setPrefWidth(110); // Adjust the width as needed
                statusLabel.setStyle("-fx-padding: 3px 8px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-font-size: 12px; -fx-alignment: center;");
            }

            @Override
            protected void updateItem(TypeCarReservation status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    statusLabel.setText(status.toString());
                    switch (status) {
                        case PENDING:
                            statusLabel.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 3px 8px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-font-size: 12px; -fx-alignment: center;");
                            break;
                        case CONFIRMED:
                            statusLabel.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 3px 8px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-font-size: 12px; -fx-alignment: center;");
                            break;
                        case CANCELED:
                            statusLabel.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 3px 8px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-font-size: 12px; -fx-alignment: center;");
                            break;
                        case FAILED:
                            statusLabel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 3px 8px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-font-size: 12px; -fx-alignment: center;");
                            break;
                    }
                    setGraphic(statusLabel); // Use the label as the graphic for the cell
                }
            }
        });
        routeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoute().getLocation_start() + " to " +
                        cellData.getValue().getRoute().getLocation_destination()));

        offerColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOffer().getDescription()));

        userColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser().getFirstName() + " " +
                        cellData.getValue().getUser().getLastName()));

        setupActionsColumn();

        filteredReservations = new FilteredList<>(reservations);
        reservationsTable.setItems(filteredReservations);
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailsButton = new Button("Show Details");

            {
                detailsButton.setOnAction(event -> {
                    CarReservation reservation = getTableView().getItems().get(getIndex());
                    handleShowDetails(reservation);
                });
                detailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        });
    }

    private void setupFilters() {
        statusFilter.getItems().addAll(TypeCarReservation.values());
        statusFilter.setOnAction(e -> applyFilters());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        filteredReservations.setPredicate(reservation -> {
            boolean matchesSearch = searchField.getText().isEmpty() ||
                    reservation.getUser().getFirstName().toLowerCase().contains(searchField.getText().toLowerCase()) ||
                    reservation.getUser().getLastName().toLowerCase().contains(searchField.getText().toLowerCase()) ||
                    reservation.getRoute().getDate_start().toString().toLowerCase().contains(searchField.getText().toLowerCase());

            boolean matchesStatus = statusFilter.getValue() == null ||
                    reservation.getStatus() == statusFilter.getValue();

            return matchesSearch && matchesStatus;
        });

        updatePagination();
    }

    private void loadReservations() {
        Task<List<CarReservation>> loadTask = new Task<>() {
            @Override
            protected List<CarReservation> call() throws Exception {
                List<CarReservation> reservations = carReservationService.rechercher();
                for (CarReservation reservation : reservations) {
                    System.out.println("Reservation ID: " + reservation.getId() + ", Status: " + reservation.getStatus());
                }
                return reservations;
            }
        };

        loadTask.setOnSucceeded(event -> {
            reservations.setAll(loadTask.getValue());
            updatePagination();
        });

        loadTask.setOnFailed(event -> {
            Throwable exception = loadTask.getException();
            showErrorAlert("Failed to load reservations", exception.getMessage());
        });

        new Thread(loadTask).start();
    }

    private void updatePagination() {
        int totalItems = (int) filteredReservations.stream().count();
        totalPages = (totalItems + itemsPerPage - 1) / itemsPerPage;
        pageInfo.setText(String.format("Page %d of %d", currentPage, totalPages));
        prevButton.setDisable(currentPage == 1);
        nextButton.setDisable(currentPage == totalPages);

        int fromIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);

        reservationsTable.setItems(FXCollections.observableArrayList(
                filteredReservations.subList(fromIndex, toIndex)));
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePagination();
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updatePagination();
        }
    }

    @FXML
    private void handleAddReservation() {
        // TODO: Implement add reservation functionality
        System.out.println("Add reservation clicked");
    }

    private void handleShowDetails(CarReservation reservation) {
        frontOfficeController.navigateTo("dashboard/car/car-reservation-details.fxml");
        ((CarReservationDetails) frontOfficeController.getController()).initialize(reservation.getRoute());
        // Implement logic to show details of the reservation
        System.out.println("Showing details for reservation: " + reservation.getId());
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
    }
}