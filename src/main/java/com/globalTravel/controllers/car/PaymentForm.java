package com.globalTravel.controllers.car;

import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.car.CarReservation;
import com.globalTravel.models.car.Offer;
import com.globalTravel.models.car.Route;
import com.globalTravel.models.car.TypeCarReservation;
import com.globalTravel.models.user.User;
import com.globalTravel.services.car.CarReservationService;
import com.globalTravel.services.car.OfferService;
import com.globalTravel.services.car.RouteService;
import com.globalTravel.utils.StripePayment;
import com.stripe.model.PaymentIntent;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class PaymentForm implements FrontNavigatable {

    @FXML
    private WebView webView;
    private CarReservationService carReservationService = new CarReservationService();
    private RouteService routeService = new RouteService();
    private Route route;
    private ArrayList<User> employees;
    private Offer offer;
    private FrontOffice frontOfficeController;
    private CarReservation carReservation;
    private ArrayList<String> reservedSeats;
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
            PaymentIntent intent = stripePayment.createPaymentIntent((long) (offer.getPrice()*1000), "usd", "Private Car Booking", paymentMethodId);
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
        carReservation.setStatus(TypeCarReservation.CONFIRMED);
        carReservationService.modifier(carReservation);
        carReservationService.assignEmployeeToReservation(offer.getId(), employees);
        ArrayList<String> seats = new ArrayList<>(offer.getReservedSeats());
        seats.addAll(this.reservedSeats);
        seats = seats.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
        System.out.println(seats);
        offer.setReservedSeats(seats);
        OfferService offerService = new OfferService();
        offerService.modifier(offer);
        frontOfficeController.navigateTo("dashboard/car/offer-reservation-grid.fxml");
    }
    public void rejectPayment() {
        System.out.println("Payment rejected: " );
        carReservation.setStatus(TypeCarReservation.FAILED);
        carReservationService.modifier(carReservation);
        frontOfficeController.navigateTo("dashboard/car/offer-reservation-grid.fxml");


    }

    public void initialize(Route route, ArrayList<User> emp, Offer offer, ArrayList<String> seats) {

        System.out.println("Initializing PaymentForm...");
        System.out.println("Route: " + route);
        System.out.println("Offer: " + offer);

        int id =routeService.addRoute(route);
        System.out.println(id);
        route.setId(id);
        CarReservation carReservation = new CarReservation(new Date(System.currentTimeMillis()), TypeCarReservation.PENDING, route, offer);
        int idres = carReservationService.addCarReservation(carReservation);
        carReservation.setId(idres);
        this.route = route;
        this.employees = emp;
        this.offer = offer;
        this.carReservation = carReservation;
        this.reservedSeats = seats;
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
    }
}