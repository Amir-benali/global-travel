package com.globalTravel.controllers.flight;

    import com.globalTravel.controllers.DashBoard;
    import com.globalTravel.models.flight.Flight;
    import com.globalTravel.models.flight.FlightStatus;
    import com.globalTravel.services.flight.FlightService;
    import com.globalTravel.services.flight.AirlineService;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.stage.FileChooser;
    import javafx.stage.Stage;

    import java.io.File;
    import java.sql.Timestamp;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.LocalTime;
    import java.util.Arrays;
    import java.util.List;

public class FlightCreateForm {

        @FXML
        private ComboBox<Integer> airlineIdField;
        @FXML
        private ComboBox<String> statusComboBox;
        @FXML
        private TextField flightNumberField;
        @FXML
        private TextField departureAirportField;
        @FXML
        private TextField arrivalAirportField;
        @FXML
        private DatePicker departureDatePicker;
        @FXML
        private Spinner<Integer> departureHourSpinner;
        @FXML
        private Spinner<Integer> departureMinuteSpinner;
        @FXML
        private DatePicker arrivalDatePicker;
        @FXML
        private Spinner<Integer> arrivalHourSpinner;
        @FXML
        private Spinner<Integer> arrivalMinuteSpinner;
        @FXML
        private TextField durationField;
        @FXML
        private TextField availableSeatsField;
        @FXML
        private TextField priceField;
        @FXML
        private Label selectedImageLabel;
        @FXML
        private ImageView airlineLogoPreview;
        @FXML
        private Button backButton; // New back button

        private File selectedLogoFile;
        private Stage stage;
        private final FlightService flightService = new FlightService();
        private final AirlineService airlineService = new AirlineService();
        private DashBoard dashBoardController; // Reference to DashBoard controller


        public void setStage(Stage stage) {
            this.stage = stage;
        }

        public void setDashBoardController(DashBoard dashBoardController) {
            this.dashBoardController = dashBoardController;
        }

        @FXML
        public void initialize() {
            System.out.println("Initializing FlightForm...");

            FlightCreateForm flightCreateForm = new FlightCreateForm();
            flightCreateForm.setDashBoardController(dashBoardController);

            statusComboBox.getItems().setAll(Arrays.stream(FlightStatus.values())
                    .map(Enum::name)
                    .toList());

            List<Integer> airlineIds = airlineService.getAllAirlineIds();
            airlineIdField.getItems().setAll(airlineIds);

            // Set the departure date picker to start from today
            departureDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(LocalDate.now()));
                }
            });

            // Set the arrival date picker to start from the selected departure date
            departureDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                arrivalDatePicker.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(empty || date.isBefore(newValue.plusDays(1)));
                    }
                });
            });
        }

        private boolean validateInput() {
            String errorMessage = "";

            if (flightNumberField.getText().isEmpty()) errorMessage += "Numéro de vol requis.\n";
            if (airlineIdField.getValue() == null) errorMessage += "Compagnie aérienne requise.\n";
            if (departureAirportField.getText().isEmpty()) errorMessage += "Aéroport de départ requis.\n";
            if (arrivalAirportField.getText().isEmpty()) errorMessage += "Aéroport d'arrivée requis.\n";
            if (departureDatePicker.getValue() == null) errorMessage += "Date de départ requise.\n";
            if (arrivalDatePicker.getValue() == null) errorMessage += "Date d'arrivée requise.\n";
            if (statusComboBox.getValue() == null) errorMessage += "Statut du vol requis.\n";

            try {
                Integer.parseInt(durationField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Durée invalide.\n";
            }

            try {
                Integer.parseInt(availableSeatsField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Nombre de sièges invalide.\n";
            }

            try {
                Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Prix invalide.\n";
            }

            if (!errorMessage.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur de saisie", errorMessage);
                return false;
            }
            return true;
        }

        private void showAlert(Alert.AlertType type, String title, String message) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        @FXML
        private void handleChooseImage() {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Airline Logo");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                selectedLogoFile = selectedFile;
                selectedImageLabel.setText(selectedFile.getName());
                airlineLogoPreview.setImage(new Image(selectedFile.toURI().toString()));
            }
        }

        @FXML
        private void handleSaveFlight() {
            if (!validateInput()) return;

            try {
                String flightNumber = flightNumberField.getText();
                if (flightService.isFlightNumberExists(flightNumber)) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Le numéro de vol existe déjà.");
                    return;
                }

                FlightStatus status = FlightStatus.valueOf(statusComboBox.getValue());
                LocalDate departureDate = departureDatePicker.getValue();
                LocalDate arrivalDate = arrivalDatePicker.getValue();
                LocalDateTime departureDateTime = LocalDateTime.of(departureDate, LocalTime.of(departureHourSpinner.getValue(), departureMinuteSpinner.getValue()));
                LocalDateTime arrivalDateTime = LocalDateTime.of(arrivalDate, LocalTime.of(arrivalHourSpinner.getValue(), arrivalMinuteSpinner.getValue()));

                Flight flight = new Flight(
                        flightNumber,
                        airlineIdField.getValue(),
                        departureAirportField.getText(),
                        arrivalAirportField.getText(),
                        Timestamp.valueOf(departureDateTime),
                        Timestamp.valueOf(arrivalDateTime),
                        Integer.parseInt(durationField.getText()),
                        Integer.parseInt(availableSeatsField.getText()),
                        Double.parseDouble(priceField.getText()),
                        status
                );

                flightService.ajouter(flight);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Vol ajouté avec succès.");
                clearForm();
                closeForm();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement du vol: " + e.getMessage());
            }
        }

        private void clearForm() {
            flightNumberField.clear();
            airlineIdField.getSelectionModel().clearSelection();
            departureAirportField.clear();
            arrivalAirportField.clear();
            departureDatePicker.setValue(null);
            arrivalDatePicker.setValue(null);
            departureHourSpinner.getValueFactory().setValue(0);
            departureMinuteSpinner.getValueFactory().setValue(0);
            arrivalHourSpinner.getValueFactory().setValue(0);
            arrivalMinuteSpinner.getValueFactory().setValue(0);
            durationField.clear();
            availableSeatsField.clear();
            priceField.clear();
            selectedImageLabel.setText("No image selected");
            airlineLogoPreview.setImage(null);
            statusComboBox.getSelectionModel().clearSelection();
        }

        @FXML
        private void handleCancel() {
            clearForm();
            closeForm();
        }


        private void closeForm() {
            if (stage != null) {
                stage.close();
            }
        }
    }