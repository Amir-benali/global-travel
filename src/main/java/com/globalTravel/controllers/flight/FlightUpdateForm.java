package com.globalTravel.controllers.flight;

                                   import com.globalTravel.models.flight.Flight;
                                   import com.globalTravel.models.flight.FlightStatus;
                                   import com.globalTravel.services.flight.AirlineService;
                                   import com.globalTravel.services.flight.FlightService;
                                   import javafx.application.Platform;
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

public class FlightUpdateForm {

                                       @FXML private TextField flightNumberField; // Add this line
                                       @FXML private ComboBox<Integer> airlineIdField;
                                       @FXML private TextField departureAirportField;
                                       @FXML private TextField arrivalAirportField;
                                       @FXML private DatePicker departureDatePicker;
                                       @FXML private Spinner<Integer> departureHourSpinner;
                                       @FXML private Spinner<Integer> departureMinuteSpinner;
                                       @FXML private DatePicker arrivalDatePicker;
                                       @FXML private Spinner<Integer> arrivalHourSpinner;
                                       @FXML private Spinner<Integer> arrivalMinuteSpinner;
                                       @FXML private TextField durationField;
                                       @FXML private TextField availableSeatsField;
                                       @FXML private ComboBox<String> statusComboBox;
                                       @FXML private TextField priceField;
                                       @FXML private Label selectedImageLabel;
                                       @FXML private ImageView airlineLogoPreview;
                                       @FXML private Button saveButton;

                                        private AirlineService airlineService = new AirlineService();
                                       private File selectedLogoFile;
                                       private FlightService flightService = new FlightService();
                                       private Flight flightToEdit;
                                       private Stage stage;

                                       public void setStage(Stage stage) {
                                           this.stage = stage;
                                       }

                                       @FXML
                                       public void initialize(Flight flight) {
                                           flightToEdit = flight;

                                           List<Integer> airlineIds = airlineService.getAllAirlineIds();
                                           airlineIdField.getItems().setAll(airlineIds);
                                           statusComboBox.getItems().setAll(Arrays.stream(FlightStatus.values())
                                                   .map(Enum::name)
                                                   .toList());

                                           departureDatePicker.setDayCellFactory(picker -> new DateCell() {
                                               @Override
                                               public void updateItem(LocalDate date, boolean empty) {
                                                   super.updateItem(date, empty);
                                                   setDisable(empty || date.isBefore(LocalDate.now()));
                                               }
                                           });

                                           departureDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                                               arrivalDatePicker.setDayCellFactory(picker -> new DateCell() {
                                                   @Override
                                                   public void updateItem(LocalDate date, boolean empty) {
                                                       super.updateItem(date, empty);
                                                       setDisable(empty || date.isBefore(newValue.plusDays(1)));
                                                   }
                                               });
                                           });

                                           if (flightToEdit != null) {
                                               populateForm();
                                           }
                                       }

                                       public void setFlightToEdit(Flight flightToEdit) {
                                           this.flightToEdit = flightToEdit;
                                           if (flightToEdit != null) {
                                               populateForm();
                                           }
                                       }

                                       private void populateForm() {
                                           Platform.runLater(() -> {
                                               flightNumberField.setText(flightToEdit.getFlight_number());
                                               airlineIdField.setValue(flightToEdit.getAirline_id());
                                               departureAirportField.setText(flightToEdit.getDeparture_airport());
                                               arrivalAirportField.setText(flightToEdit.getArrival_airport());
                                               departureDatePicker.setValue(flightToEdit.getDeparture_time().toLocalDateTime().toLocalDate());
                                               departureHourSpinner.getValueFactory().setValue(flightToEdit.getDeparture_time().toLocalDateTime().getHour());
                                               departureMinuteSpinner.getValueFactory().setValue(flightToEdit.getDeparture_time().toLocalDateTime().getMinute());
                                               arrivalDatePicker.setValue(flightToEdit.getArrival_time().toLocalDateTime().toLocalDate());
                                               arrivalHourSpinner.getValueFactory().setValue(flightToEdit.getArrival_time().toLocalDateTime().getHour());
                                               arrivalMinuteSpinner.getValueFactory().setValue(flightToEdit.getArrival_time().toLocalDateTime().getMinute());
                                               durationField.setText(String.valueOf(flightToEdit.getDuration()));
                                               availableSeatsField.setText(String.valueOf(flightToEdit.getAvailable_seats()));
                                               priceField.setText(String.valueOf(flightToEdit.getBase_price()));
                                               statusComboBox.setValue(flightToEdit.getStatus().name());
                                           });
                                       }

                                       private boolean validateInput() {
                                           String errorMessage = "";

                                           if (flightNumberField.getText().isEmpty()) errorMessage += "Flight number is required.\n";
                                           if (airlineIdField.getValue() == null) errorMessage += "Airline ID is required.\n";
                                           if (departureAirportField.getText().isEmpty()) errorMessage += "Departure airport is required.\n";
                                           if (arrivalAirportField.getText().isEmpty()) errorMessage += "Arrival airport is required.\n";
                                           if (departureDatePicker.getValue() == null) errorMessage += "Departure date is required.\n";
                                           if (arrivalDatePicker.getValue() == null) errorMessage += "Arrival date is required.\n";
                                           if (statusComboBox.getValue() == null) errorMessage += "Flight status is required.\n";

                                           try {
                                               Integer.parseInt(durationField.getText());
                                           } catch (NumberFormatException e) {
                                               errorMessage += "Invalid duration.\n";
                                           }

                                           try {
                                               Integer.parseInt(availableSeatsField.getText());
                                           } catch (NumberFormatException e) {
                                               errorMessage += "Invalid number of seats.\n";
                                           }

                                           try {
                                               Double.parseDouble(priceField.getText());
                                           } catch (NumberFormatException e) {
                                               errorMessage += "Invalid price.\n";
                                           }

                                           if (!errorMessage.isEmpty()) {
                                               showAlert(Alert.AlertType.ERROR, "Input Error", errorMessage);
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
                                               FlightStatus status = FlightStatus.valueOf(statusComboBox.getValue());
                                               LocalDate departureDate = departureDatePicker.getValue();
                                               LocalDate arrivalDate = arrivalDatePicker.getValue();
                                               LocalDateTime departureDateTime = LocalDateTime.of(departureDate, LocalTime.of(departureHourSpinner.getValue(), departureMinuteSpinner.getValue()));
                                               LocalDateTime arrivalDateTime = LocalDateTime.of(arrivalDate, LocalTime.of(arrivalHourSpinner.getValue(), arrivalMinuteSpinner.getValue()));

                                               Flight flight = new Flight(
                                                       flightToEdit.getId_flight(),
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

                                               flightService.modifier(flight);
                                               System.out.println("Updating flight: " + flight);
                                               showAlert(Alert.AlertType.INFORMATION, "Success", "Flight updated successfully.");
                                               clearForm();
                                               closeForm();
                                           } catch (Exception e) {
                                               showAlert(Alert.AlertType.ERROR, "Error", "Error updating flight: " + e.getMessage());
                                           }
                                       }

                                       @FXML
                                       private void handleCancel() {
                                           clearForm();
                                           closeForm();
                                       }

                                       private void clearForm() {
                                           flightNumberField.clear();
                                           airlineIdField.setValue(null);
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

                                       private void closeForm() {
                                           if (stage != null) {
                                               stage.close();
                                           }
                                       }
                                   }