package com.globalTravel.controllers.hotel;

import com.globalTravel.models.hotel.Reservation_hotel;
import com.globalTravel.models.hotel.Chambre;
import com.globalTravel.services.hotel.Reservation_hotelService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ListReservationHotel implements Initializable {

    @FXML
    private TableView<Reservation_hotel> reservationTable;

    @FXML
    private TableColumn<Reservation_hotel, Integer> idColumn;

    @FXML
    private TableColumn<Reservation_hotel, String> checkInDateColumn;

    @FXML
    private TableColumn<Reservation_hotel, String> checkOutDateColumn;

    @FXML
    private TableColumn<Reservation_hotel, Integer> nombreChambresColumn;

    @FXML
    private TableColumn<Reservation_hotel, String> statutColumn;

    @FXML
    private TableColumn<Reservation_hotel, String> moyenPaiementColumn;

    @FXML
    private TableColumn<Reservation_hotel, String> chambreColumn;

    private ObservableList<Reservation_hotel> reservationList = FXCollections.observableArrayList();
    private Reservation_hotelService reservationService = new Reservation_hotelService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadReservations();

        // Liaison des colonnes avec les propriétés du modèle
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_reservation_h"));
        checkInDateColumn.setCellValueFactory(new PropertyValueFactory<>("date_checkin_h"));
        checkOutDateColumn.setCellValueFactory(new PropertyValueFactory<>("date_checkout_h"));
        nombreChambresColumn.setCellValueFactory(new PropertyValueFactory<>("nombre_chambres_h"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut_h"));
        moyenPaiementColumn.setCellValueFactory(new PropertyValueFactory<>("moyen_Paiement_h"));

        // Colonne personnalisée pour afficher les informations de la chambre
        chambreColumn.setCellValueFactory(cellData -> {
            Chambre chambre = cellData.getValue().getid_chambre_j();
            if (chambre != null) {
                String hotelInfo = (chambre.getid_hotel_j() != null)
                        ? "Hôtel: " + chambre.getid_hotel_j().getNom_h()
                        : "Aucun hôtel associé";
                return new SimpleStringProperty(
                        "Chambre #" + chambre.getId_Chambre_h() +
                                " (" + chambre.getType_chambre_h() + ") - " +
                                chambre.getPrix_nuit_h() + "€/nuit, " + hotelInfo
                );
            } else {
                return new SimpleStringProperty("Aucune chambre associée");
            }
        });

        // Ajuster la largeur des colonnes en fonction du contenu
        reservationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Appliquer des styles dynamiques aux lignes de la table
        reservationTable.setRowFactory(tv -> {
            TableRow<Reservation_hotel> row = new TableRow<>();
            row.setOnMouseEntered(event -> {
                row.setStyle("-fx-background-color: #f0f0f0;"); // Couleur de fond au survol
            });
            row.setOnMouseExited(event -> {
                row.setStyle(""); // Réinitialiser le style
            });
            return row;
        });

        // Ajouter les données à la table
        reservationTable.setItems(reservationList);
    }

    private void loadReservations() {
        reservationList.clear();
        reservationList.addAll(reservationService.rechercher());
    }
}