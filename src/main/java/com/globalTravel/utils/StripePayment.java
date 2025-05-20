package com.globalTravel.utils;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
import javafx.scene.control.Alert;

public class StripePayment {

    // Set your Stripe secret key
    static {
        Stripe.apiKey = "sk_test_51Mx6ArCw3zkipEiTPvMWs6ITmrkG3y7znJeCMDbtuviQrjH3YM8VmMdN07GeT0lxL3zPzsXyOBFrLZLKviUaDzXv00D1YALRmd"; // Replace with your actual key
    }

    /**
     * Create a PaymentMethod from card details.
     */
    public PaymentMethod createPaymentMethod(String cardNumber, String expiryDate, String cvc) {
        try {
            PaymentMethodCreateParams paymentMethodParams = PaymentMethodCreateParams.builder()
                    .setType(PaymentMethodCreateParams.Type.CARD)
                    .setCard(
                            PaymentMethodCreateParams.CardDetails.builder()
                                    .setNumber(cardNumber)
                                    .setExpMonth(Long.parseLong(expiryDate.substring(0, 2)))
                                    .setExpYear(Long.parseLong("20" + expiryDate.substring(3, 5)))
                                    .setCvc(cvc)
                                    .build()
                    )
                    .build();

            return PaymentMethod.create(paymentMethodParams);
        } catch (StripeException e) {
            showErrorAlert("Payment Error", "Failed to create payment method: " + e.getMessage());
            return null;
        }
    }

    /**
     * Create a PaymentIntent using a PaymentMethod ID.
     */
    public PaymentIntent createPaymentIntent(long amount, String currency, String description, String paymentMethodId) throws StripeException {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency(currency)
                        .setPaymentMethod(paymentMethodId)
                        .setConfirm(true)
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true) // Enable automatic payment methods
                                        .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                        .build()
                        )
                        .build();


        return PaymentIntent.create(params);
    }

    /**
     * Process payment: Create a PaymentMethod and use it to confirm a PaymentIntent.
     */
    public void processPayment(long amount, String currency, String description, String cardNumber, String expiryDate, String cvc) {
        PaymentMethod paymentMethod = createPaymentMethod(cardNumber, expiryDate, cvc);
        if (paymentMethod != null) {
            try {
                PaymentIntent paymentIntent = createPaymentIntent(amount, currency, description, paymentMethod.getId());
                showSuccessAlert(paymentIntent);
            } catch (StripeException e) {
                showErrorAlert("Payment Error", "Failed to create payment intent: " + e.getMessage());
            }
        }
    }

    /**
     * Display a success alert with the payment details.
     */
    public void showSuccessAlert(PaymentIntent paymentIntent) {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Payment Successful");
        successAlert.setHeaderText(null);
        successAlert.setContentText("Payment processed successfully! Payment ID: " + paymentIntent.getId());
        successAlert.showAndWait();
    }

    /**
     * Display an error alert with a custom message.
     */
    public void showErrorAlert(String title, String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }
}
