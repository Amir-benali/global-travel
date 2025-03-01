package com.globalTravel.controllers.flight;

            import com.globalTravel.controllers.backoffice.DashBoard;
            import com.globalTravel.controllers.backoffice.Navigatable;
            import com.globalTravel.models.flight.Airline;
            import com.globalTravel.services.flight.AirlineService;
            import javafx.application.Platform;
            import javafx.fxml.FXML;
            import javafx.scene.control.*;
            import javafx.stage.FileChooser;
            import javafx.stage.Stage;

            import java.io.File;

            public class AirlineUpdateForm implements Navigatable {

                @FXML private TextField airlineNameField;
                @FXML private TextField airlineCodeField;
                @FXML private TextField countryField;
                @FXML private Label selectedImageLabel;
                @FXML private Button backButton;

                private File selectedLogoFile;
                private AirlineService airlineService = new AirlineService(); // Initialize airlineService
                private Stage stage;
                private DashBoard dashBoardController;
                private Airline airlineToEdit;

                public void setStage(Stage stage) {
                    this.stage = stage;
                }

                public void setDashBoardController(DashBoard dashBoardController) {
                    this.dashBoardController = dashBoardController;
                }

                public void setAirlineToEdit(Airline airlineToEdit) {
                    this.airlineToEdit = airlineToEdit;
                    if (airlineToEdit != null) {
                        populateForm();
                    }
                }

                @FXML
                public void initialize() {
                    System.out.println("AirlineUpdateForm initialized");
                }

                private void populateForm() {
                    Platform.runLater(() -> {
                        airlineNameField.setText(airlineToEdit.getAirline_name());
                        airlineCodeField.setText(airlineToEdit.getAirline_code());
                        countryField.setText(airlineToEdit.getCountry());
                        // Load the logo if available
                        if (selectedLogoFile != null) {
                            //airlineLogoPreview.setImage(new Image(selectedLogoFile.toURI().toString()));
                            selectedImageLabel.setText(selectedLogoFile.getName());
                        }
                    });
                }

                @FXML
                private void handleSaveAirline() {
                    if (!validateInput()) return;

                    try {
                        airlineToEdit.setAirline_name(airlineNameField.getText());
                        airlineToEdit.setAirline_code(airlineCodeField.getText());
                        airlineToEdit.setCountry(countryField.getText());

                        airlineService.modifier(airlineToEdit);

                        showAlert(Alert.AlertType.INFORMATION, "Success", "Airline updated successfully.");
                        dashBoardController.navigateTo("dashboard/flight/airline-grid.fxml");
                        closeForm();
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Error updating airline: " + e.getMessage());
                    }
                }

                private boolean validateInput() {
                    String errorMessage = "";

                    if (airlineNameField.getText().isEmpty()) {
                        errorMessage += "Airline name is required.\n";
                    } else if (airlineNameField.getText().length() > 50) {
                        errorMessage += "Airline name cannot exceed 50 characters.\n";
                    }

                    if (airlineCodeField.getText().isEmpty()) {
                        errorMessage += "Airline code is required.\n";
                    } else if (airlineCodeField.getText().length() != 2) {
                        errorMessage += "Airline code must be exactly 2 characters.\n";
                    }

                    if (countryField.getText().isEmpty()) {
                        errorMessage += "Country is required.\n";
                    } else if (countryField.getText().length() > 50) {
                        errorMessage += "Country name cannot exceed 50 characters.\n";
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
                        //airlineLogoPreview.setImage(new Image(selectedFile.toURI().toString()));
                    }
                }

                @FXML
                private void handleCancel() {
                    clearForm();
                    closeForm();
                }

                private void clearForm() {
                    airlineNameField.clear();
                    airlineCodeField.clear();
                    countryField.clear();
                    selectedImageLabel.setText("No image selected");
                    //airlineLogoPreview.setImage(null);
                }

                private void closeForm() {
                    if (stage != null) {
                        stage.close();
                    }
                }

                @FXML
                private void handleBack() {
                    dashBoardController.navigateTo("dashboard/flight/airline-grid.fxml");
                }
            }