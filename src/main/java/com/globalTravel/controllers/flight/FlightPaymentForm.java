package com.globalTravel.controllers.flight;

    import com.globalTravel.controllers.frontoffice.FrontNavigatable;
    import com.globalTravel.controllers.frontoffice.FrontOffice;
    import com.globalTravel.models.flight.Flight;
    import com.globalTravel.models.flight.FlightReservation;
    import com.globalTravel.models.flight.Ticket;
    import com.globalTravel.models.flight.TicketStatus;
    import com.globalTravel.models.user.User;
    import com.globalTravel.services.flight.FlightReservationService;
    import com.globalTravel.services.flight.FlightBookingService;
    import com.globalTravel.utils.StripePayment;
    import com.mailjet.client.errors.MailjetException;
    import com.stripe.model.PaymentIntent;
    import javafx.fxml.FXML;
    import javafx.scene.control.Alert;
    import javafx.scene.web.WebEngine;
    import javafx.scene.web.WebView;
    import netscape.javascript.JSObject;


    import java.sql.Date;

    public class FlightPaymentForm implements FrontNavigatable {

        @FXML
        private WebView webView;
        private FlightReservationService flightReservationService = new FlightReservationService();
        private FlightBookingService flightBookingService = new FlightBookingService();
        private Flight flight;
        private User currentUser;
        private Ticket ticket;
        private FrontOffice frontOfficeController;
        private FlightReservation flightReservation;

        @FXML
        public void initialize(Ticket ticket, Flight flight, User currentUser) {
            System.out.println("Initializing FlightPaymentForm...");
            System.out.println("Ticket: " + ticket);
            System.out.println("Flight: " + flight);
            System.out.println("User: " + currentUser);

            FlightReservation flightReservation = new FlightReservation(new Date(System.currentTimeMillis()), "PENDING", flight, currentUser);
            int id = flightReservationService.ajouter(flightReservation);
            flightReservation.setId(id);
            this.flight = flight;
            this.currentUser = currentUser;
            this.ticket = ticket;
            this.flightReservation = flightReservation;

            WebEngine webEngine = webView.getEngine();
            webEngine.load(getClass().getResource("/web/payment/stripe-js.html").toExternalForm());

            // Inject JavaFX object into JavaScript
            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("javafxConnector", this);
                }
            });

            // Enable JavaScript
            webEngine.setJavaScriptEnabled(true);
        }

        public void receivePaymentMethod(String paymentMethodId, String billingDetailsJson) {
            System.out.println("Received PaymentMethod ID: " + paymentMethodId);
            System.out.println("Billing Details: " + billingDetailsJson);

            StripePayment stripePayment = new StripePayment();
            try {
                PaymentIntent intent = stripePayment.createPaymentIntent((long) (ticket.getTicket_price() * 1000), "usd", "Flight Booking", paymentMethodId);
                if (intent.getStatus().equals("succeeded")) {
                    confirmPayment();
                } else {
                    rejectPayment();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Payment failed!");
                WebEngine webEngine = webView.getEngine();
                webEngine.executeScript("window.paymentStatus = 'failed';");
                webEngine.load(getClass().getResource("/web/payment/rejected.html").toExternalForm());
                rejectPayment();
            }
        }

        public void confirmPayment() {
            System.out.println("Payment confirmed: ");
            ticket.setStatus(TicketStatus.Booked);
            boolean success = flightBookingService.bookFlight(ticket);
            if (success) {
                showAlert("Success", "Booking confirmed!");
                flightReservation.setStatus("CONFIRMED");
                flightReservationService.modifier(flightReservation);

                frontOfficeController.navigateTo("dashboard/flight/flight-grid.fxml");
            } else {
                showAlert("Error", "Booking failed. Please try again.");
            }
        }

        private void showAlert(String success, String s) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(success);
            alert.setHeaderText(null);
            alert.setContentText(s);
            alert.showAndWait();
        }

        public void rejectPayment() {
            System.out.println("Payment rejected: ");
            flightReservation.setStatus("FAILED");
            flightReservationService.modifier(flightReservation);
            frontOfficeController.navigateTo("dashboard/flight/flight-grid.fxml");
        }

        public void initialize(Flight flight, User currentUser, Ticket ticket) {
            System.out.println("Initializing FlightPaymentForm...");
            System.out.println("Flight: " + flight);
            System.out.println("User: " + currentUser);
            System.out.println("Ticket: " + ticket);

            FlightReservation flightReservation = new FlightReservation(new Date(System.currentTimeMillis()), "PENDING", flight, currentUser);
            int id = flightReservationService.ajouter(flightReservation);
            flightReservation.setId(id);
            this.flight = flight;
            this.currentUser = currentUser;
            this.ticket = ticket;
            this.flightReservation = flightReservation;
        }

        @Override
        public void setFrontOfficeController(FrontOffice frontOfficeController) {
            this.frontOfficeController = frontOfficeController;
        }
    }