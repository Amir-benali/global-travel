package com.globalTravel.controllers.hotel;

import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.services.hotel.HotelService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class HotelCreateForm {

    @FXML private Label formTitleLabel;
    @FXML private TextField hotelNameField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField countryField;
    @FXML private TextField categoryField;
    @FXML private TextField servicesField;
    @FXML private TextField coordinatesField;
    @FXML private TextArea reviewsField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private HotelService hotelService = new HotelService();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        System.out.println("Initializing HotelCreateForm...");
    }

    private void clearForm() {
        hotelNameField.clear();
        addressField.clear();
        cityField.clear();
        countryField.clear();
        categoryField.clear();
        servicesField.clear();
        coordinatesField.clear();
        reviewsField.clear();
    }

    @FXML
    private void handleSaveHotel() {
        try {
            Hotel hotel = new Hotel(
                    hotelNameField.getText(),
                    addressField.getText(),
                    cityField.getText(),
                    countryField.getText(),
                    Integer.parseInt(categoryField.getText()),
                    servicesField.getText(),
                    coordinatesField.getText(),
                    reviewsField.getText()
            );

            addHotel(hotel);
            closeForm();
        } catch (Exception e) {
            System.err.println("Error saving hotel: " + e.getMessage());
        }
    }

    private void addHotel(Hotel hotel) {
        hotelService.ajouter(hotel);
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