package com.globalTravel.controllers.hotel;

import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.hotel.Chambre;
import com.globalTravel.models.hotel.Reservation_hotel;
import com.globalTravel.services.hotel.Reservation_hotelService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class HotelReservationForm implements FrontNavigatable {

    @FXML
    private Label typeLabel; // Label pour afficher le type de chambre

    @FXML
    private Label priceLabel; // Label pour afficher le prix par nuit

    @FXML
    private DatePicker checkinDatePicker; // Sélecteur de date de check-in

    @FXML
    private DatePicker checkoutDatePicker; // Sélecteur de date de check-out

    @FXML
    private TextField numberOfRoomsField; // Champ pour le nombre de chambres

    @FXML
    private ComboBox<String> paymentMethodComboBox; // ComboBox pour la méthode de paiement

    private Chambre selectedChambre; // Chambre sélectionnée
    private FrontOffice frontOfficeController;

    /**
     * Définit la chambre sélectionnée et met à jour les labels.
     *
     * @param chambre La chambre sélectionnée.
     */
    public void setSelectedChambre(Chambre chambre) {
        this.selectedChambre = chambre;

        // Afficher le type de chambre et le prix par nuit
        typeLabel.setText("Type de chambre: " + chambre.getType_chambre_h());
        priceLabel.setText("Prix par nuit: " + chambre.getPrix_nuit_h() + " €");
    }

    /**
     * Initialise le formulaire de réservation.
     */
    @FXML
    public void initialize() {
        // Initialiser les options de méthode de paiement
        paymentMethodComboBox.getItems().addAll("Carte de crédit", "PayPal", "Espèces");
    }

    /**
     * Gère l'action du bouton "Réserver".
     */
    @FXML
    private void handleReservation() {
        // Récupérer les valeurs du formulaire
        LocalDate checkinDate = checkinDatePicker.getValue();
        LocalDate checkoutDate = checkoutDatePicker.getValue();
        int numberOfRooms = Integer.parseInt(numberOfRoomsField.getText());
        String paymentMethod = paymentMethodComboBox.getValue();

        // Valider les données
        if (checkinDate == null || checkoutDate == null || numberOfRooms <= 0 || paymentMethod == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs correctement.");
            return;
        }

        // Créer un objet Reservation_hotel
        Reservation_hotel reservation = new Reservation_hotel();
        reservation.setDate_checkin_h(checkinDate);
        reservation.setDate_checkout_h(checkoutDate);
        reservation.setNombre_chambres_h(numberOfRooms);
        reservation.setMoyen_Paiement_h(paymentMethod);
        reservation.setid_chambre_j(selectedChambre);
        reservation.setStatut_h("Confirmed"); // Définir le statut à "Confirmed"

        // Enregistrer la réservation dans la base de données
        Reservation_hotelService reservationService = new Reservation_hotelService();
        reservationService.ajouter(reservation);

        // Afficher un message de succès
        showAlert("Succès", "Votre réservation a été enregistrée avec succès !");

        // Revenir à la page des chambres
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/hotel/chambre-grid.fxml"));
            Parent root = loader.load();

            // Remplacer le contenu de la scène actuelle par la page des chambres
            Stage stage = (Stage) typeLabel.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de naviguer vers la page des chambres.");
        }
    }

    /**
     * Gère l'action du bouton "Annuler".
     */
    @FXML
    private void handleCancel() {
        // Revenir à la page des chambres
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontOffice/front-office.fxml"));
            Parent root = loader.load();

            // Remplacer le contenu de la scène actuelle par la page des chambres
            Stage stage = (Stage) typeLabel.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir à la page des chambres.");
        }
    }

    /**
     * Gère l'action du bouton "Payer".
     */
    @FXML
    private void handlePayment() {
        // Récupérer les valeurs du formulaire
        LocalDate checkinDate = checkinDatePicker.getValue();
        LocalDate checkoutDate = checkoutDatePicker.getValue();
        int numberOfRooms = Integer.parseInt(numberOfRoomsField.getText());
        String paymentMethod = paymentMethodComboBox.getValue();

        // Valider les données
        if (checkinDate == null || checkoutDate == null || numberOfRooms <= 0 || paymentMethod == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs correctement.");
            return;
        }

        // Créer une réservation (mais sans l'ajouter encore à la base de données)
        Reservation_hotel reservation = new Reservation_hotel();
        reservation.setDate_checkin_h(checkinDate);
        reservation.setDate_checkout_h(checkoutDate);
        reservation.setNombre_chambres_h(numberOfRooms);
        reservation.setMoyen_Paiement_h(paymentMethod);
        reservation.setid_chambre_j(selectedChambre);
        reservation.setStatut_h("Pending Payment"); // Mettre un statut temporaire

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/hotel/payment-reservation.fxml"));
            Parent paymentPage = loader.load();

            PaymentReservation paymentController = loader.getController();
            paymentController.setReservationDetails(selectedChambre, checkinDate, checkoutDate, numberOfRooms);

            // Définir un callback pour ajouter la réservation après le paiement
            paymentController.setOnPaymentSuccess(() -> {
                reservation.setStatut_h("Confirmed"); // Marquer comme confirmée
                Reservation_hotelService reservationService = new Reservation_hotelService();
                reservationService.ajouter(reservation); // Ajouter à la base de données
                showAlert("Succès", "Votre réservation a été enregistrée avec succès !");
                frontOfficeController.navigateTo("dashboard/hotel/list-reservation-h.fxml");

            });

            // Afficher la page de paiement
            Stage stage = new Stage();
            stage.setScene(new Scene(paymentPage));
            stage.setTitle("Paiement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page de paiement.");
        }
    }


    /**
     * Affiche une alerte.
     *
     * @param title   Le titre de l'alerte.
     * @param message Le message de l'alerte.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController=frontOfficeController;
    }
}