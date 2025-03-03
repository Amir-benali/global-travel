package com.globalTravel.controllers.hotel;

import com.globalTravel.models.hotel.Chambre;
import com.globalTravel.utils.StripePayment;
import com.stripe.model.PaymentIntent;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import netscape.javascript.JSObject;

import java.time.LocalDate;

public class PaymentReservation {

    @FXML
    private WebView webView;

    private Chambre selectedChambre;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private int numberOfRooms;

    private Runnable paymentSuccessCallback; // Callback pour le succès du paiement

    /**
     * Définit les détails de la réservation.
     *
     * @param chambre       La chambre sélectionnée.
     * @param checkinDate   La date de check-in.
     * @param checkoutDate  La date de check-out.
     * @param numberOfRooms Le nombre de chambres.
     */
    public void setReservationDetails(Chambre chambre, LocalDate checkinDate, LocalDate checkoutDate, int numberOfRooms) {
        this.selectedChambre = chambre;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.numberOfRooms = numberOfRooms;
    }

    /**
     * Définit le callback pour le succès du paiement.
     *
     * @param callback Le callback à exécuter lorsque le paiement est réussi.
     */
    public void setOnPaymentSuccess(Runnable callback) {
        this.paymentSuccessCallback = callback;
    }

    @FXML
    public void initialize() {
        // Charger le formulaire de paiement Stripe
        WebEngine webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/web/payment/stripe-js.html").toExternalForm());

        // Injecter un objet JavaFX dans JavaScript pour la communication
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javafxConnector", this);
            }
        });

        // Activer JavaScript
        webEngine.setJavaScriptEnabled(true);
    }

    /**
     * Méthode appelée par JavaScript lorsque le paiement est traité.
     *
     * @param paymentMethodId     L'ID de la méthode de paiement Stripe.
     * @param billingDetailsJson  Les détails de facturation au format JSON.
     */
    public void receivePaymentMethod(String paymentMethodId, String billingDetailsJson) {
        System.out.println("Received PaymentMethod ID: " + paymentMethodId);
        System.out.println("Billing Details: " + billingDetailsJson);

        // Traiter le paiement avec Stripe
        StripePayment stripePayment = new StripePayment();
        try {
            // Créer un PaymentIntent pour la réservation
            PaymentIntent intent = stripePayment.createPaymentIntent(
                    (int) (selectedChambre.getPrix_nuit_h() * 100), // Montant en cents
                    "eur", // Devise (euros)
                    "Réservation de chambre", // Description
                    paymentMethodId // ID de la méthode de paiement
            );

            // Vérifier si le paiement a réussi
            if (intent.getStatus().equals("succeeded")) {
                System.out.println("Payment successful!");
                WebEngine webEngine = webView.getEngine();
                webEngine.executeScript("window.paymentStatus = 'success';");
                webEngine.load(getClass().getResource("/web/payment/success.html").toExternalForm());

                // Notifier le succès du paiement
                if (paymentSuccessCallback != null) {
                    paymentSuccessCallback.run();
                }
            } else {
                System.out.println("Payment failed!");
                WebEngine webEngine = webView.getEngine();
                webEngine.executeScript("window.paymentStatus = 'failed';");
                webEngine.load(getClass().getResource("/web/payment/rejected.html").toExternalForm());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Payment failed!");
            WebEngine webEngine = webView.getEngine();
            webEngine.executeScript("window.paymentStatus = 'failed';");
            webEngine.load(getClass().getResource("/web/payment/rejected.html").toExternalForm());
        }
    }

    /**
     * Méthode appelée lorsque l'utilisateur confirme le paiement réussi.
     */
    public void confirmPayment() {
        System.out.println("Payment confirmed for the reservation!");

        // Fermer la fenêtre de paiement
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.close();

        // Afficher un message de succès
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Successful");
        alert.setHeaderText(null);
        alert.setContentText("Your payment was successful. Thank you for your reservation!");
        alert.showAndWait();
    }

    /**
     * Méthode appelée lorsque l'utilisateur rejette ou annule le paiement.
     */
    public void rejectPayment() {
        System.out.println("Payment rejected for the reservation!");

        // Fermer la fenêtre de paiement
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.close();

        // Afficher un message d'erreur
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Payment Failed");
        alert.setHeaderText(null);
        alert.setContentText("There was an issue processing your payment. Please try again.");
        alert.showAndWait();
    }
}