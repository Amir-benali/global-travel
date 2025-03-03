package com.globalTravel.controllers.activity;

import com.globalTravel.models.activity.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityDetailsController {

    @FXML
    private Label nomActivityLabel;

    @FXML
    private Label dateDebutLabel;

    @FXML
    private Label dateFinLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label localisationLabel;

    @FXML
    private Label prixTotalLabel;

    @FXML
    private Label typeActivityLabel;

    @FXML
    private Label hotelLabel;

    @FXML
    private Label voitureLabel;

    @FXML
    private Label volLabel;

    private Connection connection; // Ajoutez une connexion à la base de données

    public ActivityDetailsController() {
        // Initialisez la connexion à la base de données
        connection = com.globalTravel.utils.DataSource.getInstance().getConnection();
    }

    // Méthode pour initialiser les détails de l'activité
    public void initData(Activity activity) {
        nomActivityLabel.setText(activity.getNomActivity());
        dateDebutLabel.setText(activity.getDateDebut().toString());
        dateFinLabel.setText(activity.getDateFin().toString());
        descriptionLabel.setText(activity.getDescription());
        localisationLabel.setText(activity.getLocalisation());
        prixTotalLabel.setText(String.valueOf(activity.getPrixTotal()));
        typeActivityLabel.setText(activity.getTypeActivity().toString());
        hotelLabel.setText(getHotelNameById(activity.getJoinHotelId()));
        voitureLabel.setText(getCarBrandById(activity.getJoinVoitureId()));
        volLabel.setText(getFlightNumberById(activity.getJoinVolsId()));
    }

    // Méthode pour récupérer le nom de l'hôtel par son ID
    private String getHotelNameById(int hotelId) {
        String query = "SELECT nom_h FROM hotel WHERE id_hotel_h = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, hotelId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("nom_h");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du nom de l'hôtel : " + e.getMessage());
        }
        return "N/A";
    }

    // Méthode pour récupérer la marque de la voiture par son ID
    private String getCarBrandById(int carId) {
        String query = "SELECT brand FROM private_car WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, carId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("brand");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la marque de la voiture : " + e.getMessage());
        }
        return "N/A";
    }

    // Méthode pour récupérer le numéro de vol par son ID
    private String getFlightNumberById(int flightId) {
        String query = "SELECT flight_number FROM flights WHERE id_flight = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, flightId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("flight_number");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du numéro de vol : " + e.getMessage());
        }
        return "N/A";
    }
}