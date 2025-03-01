package com.globalTravel.controllers.car;

import com.globalTravel.utils.StripePayment;
import com.stripe.model.PaymentIntent;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class PaymentForm {

    @FXML
    private WebView webView;

    @FXML
    public void initialize() {
        WebEngine webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/web/payment/stripe-js.html").toExternalForm());

        // Inject JavaFX object into JavaScript
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javafxConnector", this);
            }
        });

        // Add a listener to handle navigation
        webEngine.locationProperty().addListener((obs, oldLocation, newLocation) -> {
            System.out.println("Navigating from " + oldLocation + " to " + newLocation);
        });

        // Enable JavaScript
        webEngine.setJavaScriptEnabled(true);
    }

    public void receivePaymentMethod(String paymentMethodId, String billingDetailsJson) {
        System.out.println("Received PaymentMethod ID: " + paymentMethodId);
        System.out.println("Billing Details: " + billingDetailsJson);

        StripePayment stripePayment = new StripePayment();
        try {
            PaymentIntent intent = stripePayment.createPaymentIntent(5000, "usd", "Private Car Booking", paymentMethodId);
            if (intent.getStatus().equals("succeeded")) {
                System.out.println("Payment successful!");
                WebEngine webEngine = webView.getEngine();
                webEngine.executeScript("window.paymentStatus = 'success';");
                webEngine.load(getClass().getResource("/web/payment/success.html").toExternalForm());
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

    public void confirmPayment( ) {
        System.out.println("Payment confirmed: ");

    }
    public void rejectPayment() {
        System.out.println("Payment rejected: " );

    }
}