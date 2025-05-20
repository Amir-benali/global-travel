package com.globalTravel.controllers.car;

import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.car.Offer;
import com.globalTravel.models.user.User;
import com.globalTravel.services.user.UserService;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import java.util.*;

public class CarSeatSelection implements FrontNavigatable {
    @FXML private Rectangle carBody;
    @FXML private VBox driverSeat;
    @FXML private VBox passengerSeat;
    @FXML private HBox middleRow;
    @FXML private HBox thirdRow;
    @FXML private HBox fourthRow;
    @FXML private Label totalSeatsLabel;
    @FXML private Label availableSeatsLabel;
    @FXML private VBox selectedSeatsContainer;
    @FXML private VBox employeeSelectionContainer;
    @FXML private Label noSeatsMessage;
    @FXML private Button continueButton;
    @FXML private Group vehicleBlueprint;

    private Map<String, Double> seatPrices = new HashMap<>();
    private Map<String, Boolean> selectedSeats = new HashMap<>();
    private Map<String, Boolean> reservedSeats = new HashMap<>();
    private Map<String, User> seatAssignments = new HashMap<>();
    private int totalSeats;
    private int availableSeats;
    private double basePrice;
    private UserService userService = new UserService();
    private List<User> employees;
    private Offer offer;
    private FrontOffice frontOfficeController;
    private ArrayList<String> reservedSeatsList = new ArrayList<>();
    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
    }

    @FXML
    public void initialize(Offer offer) {
        this.offer = offer;
        this.reservedSeatsList = offer.getReservedSeats();
        System.out.println(offer.getReservedSeats());
        employees = userService.rechercher().stream()
                .filter(user -> user.getRoles().toLowerCase().contains("employee"))
                .toList();

        initializeData(offer.getCar().getNum_place() - offer.getReservedSeats().size(),
                offer.getPrice(),
                offer.getReservedSeats());
    }

    public void initializeData(int seatCount, double basePrice, List<String> reservedSeatsList) {
        this.totalSeats = seatCount;
        this.basePrice = basePrice;
        this.availableSeats = seatCount - 1 - reservedSeatsList.size() < 0 ? 0 : seatCount - 1 - reservedSeatsList.size();

        initializeSeatPrices();

        for (String seatId : reservedSeatsList) {
            reservedSeats.put(seatId, true);
        }
        System.out.println("Reserved seats: " + reservedSeats);

        updateVehicleBlueprint();
        updateSeatLayout();
        updateSeatCounts();
    }

    private void initializeSeatPrices() {
        seatPrices.put("A2", 0.15 * basePrice);
        seatPrices.put("B1", 0.15 * basePrice);
        seatPrices.put("B2", 0.15 * basePrice);
        seatPrices.put("B3", 0.12 * basePrice);
        seatPrices.put("C1", 0.12 * basePrice);
        seatPrices.put("C2", 0.10 * basePrice);
        seatPrices.put("C3", 0.10 * basePrice);
        seatPrices.put("D1", 0.08 * basePrice);
        seatPrices.put("D2", 0.08 * basePrice);
        seatPrices.put("D3", 0.08 * basePrice);
    }

    private void updateVehicleBlueprint() {
        vehicleBlueprint.getChildren().clear();

        double scale = 1.3;
        vehicleBlueprint.setScaleX(scale);
        vehicleBlueprint.setScaleY(scale);

        if (totalSeats <= 2) {
            // Sports Car Blueprint
            Rectangle body = new Rectangle(20, 30, 140, 140);
            body.setArcWidth(15);
            body.setArcHeight(15);
            body.setFill(Color.WHITE);
            body.setStroke(Color.web("#444"));
            body.setStrokeWidth(1.2);

            Rectangle innerBody = new Rectangle(40, 40, 100, 120);
            innerBody.setArcWidth(10);
            innerBody.setArcHeight(10);
            innerBody.setFill(Color.web("#f0f9ff"));
            innerBody.setStroke(Color.web("#444"));
            innerBody.setStrokeWidth(0.7);

            Line centerLine = new Line(90, 30, 90, 170);
            centerLine.setStroke(Color.web("#555"));
            centerLine.setStrokeWidth(0.5);
            centerLine.getStrokeDashArray().addAll(3d, 1d);

            Rectangle topEdge = new Rectangle(30, 30, 120, 10);
            topEdge.setArcWidth(5);
            topEdge.setArcHeight(5);
            topEdge.setFill(Color.web("#e5e7eb"));
            topEdge.setStroke(Color.web("#444"));
            topEdge.setStrokeWidth(0.5);

            Rectangle bottomEdge = new Rectangle(30, 160, 120, 10);
            bottomEdge.setArcWidth(5);
            bottomEdge.setArcHeight(5);
            bottomEdge.setFill(Color.web("#e5e7eb"));
            bottomEdge.setStroke(Color.web("#444"));
            bottomEdge.setStrokeWidth(0.5);

            Rectangle leftFrontWheel = new Rectangle(25, 50, 12, 20);
            leftFrontWheel.setArcWidth(2);
            leftFrontWheel.setArcHeight(2);
            leftFrontWheel.setFill(Color.web("#d1d5db"));
            leftFrontWheel.setStroke(Color.web("#444"));
            leftFrontWheel.setStrokeWidth(1);

            Rectangle leftRearWheel = new Rectangle(25, 110, 12, 20);
            leftRearWheel.setArcWidth(2);
            leftRearWheel.setArcHeight(2);
            leftRearWheel.setFill(Color.web("#d1d5db"));
            leftRearWheel.setStroke(Color.web("#444"));
            leftRearWheel.setStrokeWidth(1);

            Rectangle rightFrontWheel = new Rectangle(143, 50, 12, 20);
            rightFrontWheel.setArcWidth(2);
            rightFrontWheel.setArcHeight(2);
            rightFrontWheel.setFill(Color.web("#d1d5db"));
            rightFrontWheel.setStroke(Color.web("#444"));
            rightFrontWheel.setStrokeWidth(1);

            Rectangle rightRearWheel = new Rectangle(143, 110, 12, 20);
            rightRearWheel.setArcWidth(2);
            rightRearWheel.setArcHeight(2);
            rightRearWheel.setFill(Color.web("#d1d5db"));
            rightRearWheel.setStroke(Color.web("#444"));
            rightRearWheel.setStrokeWidth(1);

            Text label = new Text(90, 190, "SPORTS CAR");
            label.setFont(Font.font("Arial", FontWeight.BOLD, 8));
            label.setFill(Color.web("#333"));
            label.setTextAlignment(TextAlignment.CENTER);
            label.setX(label.getX() - label.getBoundsInLocal().getWidth() / 2);

            vehicleBlueprint.getChildren().addAll(body, innerBody, centerLine, topEdge, bottomEdge,
                    leftFrontWheel, leftRearWheel, rightFrontWheel, rightRearWheel, label);

        } else if (totalSeats <= 4) {
            // Sedan Blueprint
            Rectangle body = new Rectangle(35, 20, 120, 180);
            body.setArcWidth(10);
            body.setArcHeight(10);
            body.setFill(Color.WHITE);
            body.setStroke(Color.web("#444"));
            body.setStrokeWidth(1.2);

            Rectangle innerBody = new Rectangle(45, 35, 100, 150);
            innerBody.setArcWidth(5);
            innerBody.setArcHeight(5);
            innerBody.setFill(Color.web("#f0f9ff"));
            innerBody.setStroke(Color.web("#444"));
            innerBody.setStrokeWidth(0.7);

            Line centerLine = new Line(95, 20, 95, 200);
            centerLine.setStroke(Color.web("#555"));
            centerLine.setStrokeWidth(0.5);
            centerLine.getStrokeDashArray().addAll(3d, 1d);

            Rectangle leftFrontWheel = new Rectangle(25, 40, 12, 22);
            leftFrontWheel.setArcWidth(2);
            leftFrontWheel.setArcHeight(2);
            leftFrontWheel.setFill(Color.web("#d1d5db"));
            leftFrontWheel.setStroke(Color.web("#444"));
            leftFrontWheel.setStrokeWidth(1);

            Rectangle leftRearWheel = new Rectangle(25, 160, 12, 22);
            leftRearWheel.setArcWidth(2);
            leftRearWheel.setArcHeight(2);
            leftRearWheel.setFill(Color.web("#d1d5db"));
            leftRearWheel.setStroke(Color.web("#444"));
            leftRearWheel.setStrokeWidth(1);

            Rectangle rightFrontWheel = new Rectangle(153, 40, 12, 22);
            rightFrontWheel.setArcWidth(2);
            rightFrontWheel.setArcHeight(2);
            rightFrontWheel.setFill(Color.web("#d1d5db"));
            rightFrontWheel.setStroke(Color.web("#444"));
            rightFrontWheel.setStrokeWidth(1);

            Rectangle rightRearWheel = new Rectangle(153, 160, 12, 22);
            rightRearWheel.setArcWidth(2);
            rightRearWheel.setArcHeight(2);
            rightRearWheel.setFill(Color.web("#d1d5db"));
            rightRearWheel.setStroke(Color.web("#444"));
            rightRearWheel.setStrokeWidth(1);

            vehicleBlueprint.getChildren().addAll(body, innerBody, centerLine,
                    leftFrontWheel, leftRearWheel, rightFrontWheel, rightRearWheel);

        } else if (totalSeats <= 7) {
            // SUV/Minivan Blueprint
            Rectangle body = new Rectangle(30, 25, 160, 250);
            body.setArcWidth(15);
            body.setArcHeight(15);
            body.setFill(Color.WHITE);
            body.setStroke(Color.web("#444"));
            body.setStrokeWidth(1.2);

            Rectangle innerBody = new Rectangle(40, 40, 140, 230);
            innerBody.setArcWidth(8);
            innerBody.setArcHeight(8);
            innerBody.setFill(Color.web("#f0f9ff"));
            innerBody.setStroke(Color.web("#444"));
            innerBody.setStrokeWidth(0.7);

            Line centerLine = new Line(110, 25, 110, 275);
            centerLine.setStroke(Color.web("#555"));
            centerLine.setStrokeWidth(0.5);
            centerLine.getStrokeDashArray().addAll(3d, 1d);

            Rectangle topEdge = new Rectangle(40, 30, 140, 10);
            topEdge.setArcWidth(5);
            topEdge.setArcHeight(5);
            topEdge.setFill(Color.web("#e5e7eb"));
            topEdge.setStroke(Color.web("#444"));
            topEdge.setStrokeWidth(0.5);

            Rectangle bottomEdge = new Rectangle(40, 240, 140, 10);
            bottomEdge.setArcWidth(5);
            bottomEdge.setArcHeight(5);
            bottomEdge.setFill(Color.web("#e5e7eb"));
            bottomEdge.setStroke(Color.web("#444"));
            bottomEdge.setStrokeWidth(0.5);

            Rectangle leftFrontWheel = new Rectangle(20, 50, 15, 25);
            leftFrontWheel.setArcWidth(3);
            leftFrontWheel.setArcHeight(3);
            leftFrontWheel.setFill(Color.web("#d1d5db"));
            leftFrontWheel.setStroke(Color.web("#444"));
            leftFrontWheel.setStrokeWidth(1);

            Rectangle leftRearWheel = new Rectangle(20, 200, 15, 25);
            leftRearWheel.setArcWidth(3);
            leftRearWheel.setArcHeight(3);
            leftRearWheel.setFill(Color.web("#d1d5db"));
            leftRearWheel.setStroke(Color.web("#444"));
            leftRearWheel.setStrokeWidth(1);

            Rectangle rightFrontWheel = new Rectangle(185, 50, 15, 25);
            rightFrontWheel.setArcWidth(3);
            rightFrontWheel.setArcHeight(3);
            rightFrontWheel.setFill(Color.web("#d1d5db"));
            rightFrontWheel.setStroke(Color.web("#444"));
            rightFrontWheel.setStrokeWidth(1);

            Rectangle rightRearWheel = new Rectangle(185, 200, 15, 25);
            rightRearWheel.setArcWidth(3);
            rightRearWheel.setArcHeight(3);
            rightRearWheel.setFill(Color.web("#d1d5db"));
            rightRearWheel.setStroke(Color.web("#444"));
            rightRearWheel.setStrokeWidth(1);

            vehicleBlueprint.getChildren().addAll(body, innerBody, centerLine, topEdge, bottomEdge,
                    leftFrontWheel, leftRearWheel, rightFrontWheel, rightRearWheel);

        } else {
            // Luxury/Stretch Vehicle Blueprint
            Rectangle body = new Rectangle(25, 20, 180, 360);
            body.setArcWidth(15);
            body.setArcHeight(15);
            body.setFill(Color.WHITE);
            body.setStroke(Color.web("#444"));
            body.setStrokeWidth(1.2);

            Rectangle innerBody = new Rectangle(35, 30, 160, 340);
            innerBody.setArcWidth(10);
            innerBody.setArcHeight(10);
            innerBody.setFill(Color.web("#f0f9ff"));
            innerBody.setStroke(Color.web("#444"));
            innerBody.setStrokeWidth(0.7);

            Line centerLine = new Line(115, 20, 115, 380);
            centerLine.setStroke(Color.web("#555"));
            centerLine.setStrokeWidth(0.5);
            centerLine.getStrokeDashArray().addAll(3d, 1d);

            Line row1 = new Line(35, 50, 195, 50);
            row1.setStroke(Color.web("#444"));
            row1.setStrokeWidth(0.7);

            Line row2 = new Line(35, 175, 195, 175);
            row2.setStroke(Color.web("#444"));
            row2.setStrokeWidth(0.7);

            Line row3 = new Line(35, 290, 195, 290);
            row3.setStroke(Color.web("#444"));
            row3.setStrokeWidth(0.7);

            Rectangle topEdge = new Rectangle(45, 25, 140, 10);
            topEdge.setArcWidth(5);
            topEdge.setArcHeight(5);
            topEdge.setFill(Color.web("#e5e7eb"));
            topEdge.setStroke(Color.web("#444"));
            topEdge.setStrokeWidth(0.5);

            Rectangle bottomEdge = new Rectangle(45, 345, 140, 10);
            bottomEdge.setArcWidth(5);
            bottomEdge.setArcHeight(5);
            bottomEdge.setFill(Color.web("#e5e7eb"));
            bottomEdge.setStroke(Color.web("#444"));
            bottomEdge.setStrokeWidth(0.5);

            Rectangle leftFrontWheel = new Rectangle(15, 40, 15, 25);
            leftFrontWheel.setArcWidth(3);
            leftFrontWheel.setArcHeight(3);
            leftFrontWheel.setFill(Color.web("#d1d5db"));
            leftFrontWheel.setStroke(Color.web("#444"));
            leftFrontWheel.setStrokeWidth(1);

            Rectangle leftMiddleWheel = new Rectangle(15, 160, 15, 25);
            leftMiddleWheel.setArcWidth(3);
            leftMiddleWheel.setArcHeight(3);
            leftMiddleWheel.setFill(Color.web("#d1d5db"));
            leftMiddleWheel.setStroke(Color.web("#444"));
            leftMiddleWheel.setStrokeWidth(1);

            Rectangle leftRearWheel = new Rectangle(15, 280, 15, 25);
            leftRearWheel.setArcWidth(3);
            leftRearWheel.setArcHeight(3);
            leftRearWheel.setFill(Color.web("#d1d5db"));
            leftRearWheel.setStroke(Color.web("#444"));
            leftRearWheel.setStrokeWidth(1);

            Rectangle rightFrontWheel = new Rectangle(200, 40, 15, 25);
            rightFrontWheel.setArcWidth(3);
            rightFrontWheel.setArcHeight(3);
            rightFrontWheel.setFill(Color.web("#d1d5db"));
            rightFrontWheel.setStroke(Color.web("#444"));
            rightFrontWheel.setStrokeWidth(1);

            Rectangle rightMiddleWheel = new Rectangle(200, 160, 15, 25);
            rightMiddleWheel.setArcWidth(3);
            rightMiddleWheel.setArcHeight(3);
            rightMiddleWheel.setFill(Color.web("#d1d5db"));
            rightMiddleWheel.setStroke(Color.web("#444"));
            rightMiddleWheel.setStrokeWidth(1);

            Rectangle rightRearWheel = new Rectangle(200, 280, 15, 25);
            rightRearWheel.setArcWidth(3);
            rightRearWheel.setArcHeight(3);
            rightRearWheel.setFill(Color.web("#d1d5db"));
            rightRearWheel.setStroke(Color.web("#444"));
            rightRearWheel.setStrokeWidth(1);

            vehicleBlueprint.getChildren().addAll(body, innerBody, centerLine, row1, row2, row3,
                    topEdge, bottomEdge, leftFrontWheel, leftMiddleWheel, leftRearWheel,
                    rightFrontWheel, rightMiddleWheel, rightRearWheel);
        }
    }

    private void updateSeatLayout() {
        System.out.println(reservedSeatsList.get(0));

        middleRow.getChildren().clear();
        if (thirdRow != null) thirdRow.getChildren().clear();
        if (fourthRow != null) fourthRow.getChildren().clear();

        setupDriverSeat();
        setupPassengerSeat();

        // Middle row seats (B1-B3)
        if (totalSeats >= 3) {
            VBox b1Seat = addSeatToRow(middleRow, "B1", seatPrices.get("B1"));
            if (reservedSeatsList.stream().anyMatch(s -> s.trim().equalsIgnoreCase("B1"))) {
                System.out.println("Seat B1 is reserved");
                markSeatAsReserved((StackPane) b1Seat.getChildren().get(0), (Label) b1Seat.getChildren().get(1), "B1", 0);
            }
        }
        if (totalSeats >= 4) {
            VBox b2Seat = addSeatToRow(middleRow, "B2", seatPrices.get("B2"));
            if (reservedSeatsList.stream().anyMatch(s -> s.trim().equalsIgnoreCase("B2"))) {
                System.out.println("Seat B2 is reserved");
                markSeatAsReserved((StackPane) b2Seat.getChildren().get(0), (Label) b2Seat.getChildren().get(1), "B2", 0);
            }
        }
        if (totalSeats >= 5) {
            VBox b3Seat = addSeatToRow(middleRow, "B3", seatPrices.get("B3"));
            if (reservedSeatsList.stream().anyMatch(s -> s.trim().equalsIgnoreCase("B3"))) {
                System.out.println("Seat B3 is reserved");
                markSeatAsReserved((StackPane) b3Seat.getChildren().get(0), (Label) b3Seat.getChildren().get(1), "B3", 0);
            }
        }

        // Third row seats (C1-C3)
        if (totalSeats >= 6) {
            if (thirdRow == null) {
                thirdRow = new HBox();
                thirdRow.setAlignment(javafx.geometry.Pos.CENTER);
                thirdRow.setSpacing(20);
                ((VBox) middleRow.getParent()).getChildren().add(2, thirdRow);
            }

            VBox c1Seat = addSeatToRow(thirdRow, "C1", seatPrices.get("C1"));
            if (reservedSeatsList.stream().anyMatch(s -> s.trim().equalsIgnoreCase("C1"))) {
                System.out.println("Seat C1 is reserved");
                markSeatAsReserved((StackPane) c1Seat.getChildren().get(0), (Label) c1Seat.getChildren().get(1), "C1", 0);
            }
            if (totalSeats >= 7) {
                VBox c2Seat = addSeatToRow(thirdRow, "C2", seatPrices.get("C2"));
                if (reservedSeatsList.stream().anyMatch(s -> s.trim().equalsIgnoreCase("C2"))) {
                    System.out.println("Seat C2 is reserved");
                    markSeatAsReserved((StackPane) c2Seat.getChildren().get(0), (Label) c2Seat.getChildren().get(1), "C2", 0);
                }
            }
            if (totalSeats >= 8) {
                VBox c3Seat = addSeatToRow(thirdRow, "C3", seatPrices.get("C3"));
                if (reservedSeatsList.stream().anyMatch(s -> s.trim().equalsIgnoreCase("C3"))) {
                    System.out.println("Seat C3 is reserved");
                    markSeatAsReserved((StackPane) c3Seat.getChildren().get(0), (Label) c3Seat.getChildren().get(1), "C3", 0);
                }
            }
        }

        // Fourth row seats (D1-D3)
        if (totalSeats >= 9) {
            if (fourthRow == null) {
                fourthRow = new HBox();
                fourthRow.setAlignment(javafx.geometry.Pos.CENTER);
                fourthRow.setSpacing(20);
                ((VBox) middleRow.getParent()).getChildren().add(3, fourthRow);
            }

            VBox d1Seat = addSeatToRow(fourthRow, "D1", seatPrices.get("D1"));
            if (reservedSeatsList.stream().anyMatch(s -> s.trim().equalsIgnoreCase("D1"))) {
                markSeatAsReserved((StackPane) d1Seat.getChildren().get(0), (Label) d1Seat.getChildren().get(1), "D1", 0);
            }
            if (totalSeats >= 10) {
                VBox d2Seat = addSeatToRow(fourthRow, "D2", seatPrices.get("D2"));
                if (reservedSeatsList.stream().anyMatch(s -> s.trim().equalsIgnoreCase("D2"))) {
                    markSeatAsReserved((StackPane) d2Seat.getChildren().get(0), (Label) d2Seat.getChildren().get(1), "D2", 0);
                }
            }
            if (totalSeats >= 11) {
                VBox d3Seat = addSeatToRow(fourthRow, "D3", seatPrices.get("D3"));
                if (reservedSeatsList.stream().anyMatch(s -> s.trim().equalsIgnoreCase("D3"))) {
                    markSeatAsReserved((StackPane) d3Seat.getChildren().get(0), (Label) d3Seat.getChildren().get(1), "D3", 0);
                }
            }
        }
    }
    private void setupDriverSeat() {
        StackPane driverPane = (StackPane) driverSeat.getChildren().get(0);
        driverPane.setStyle("-fx-background-color: #9ca3af; -fx-border-color: #6b7280; -fx-border-width: 2; " +
                "-fx-border-radius: 5; -fx-background-radius: 5; -fx-min-width: 32; -fx-min-height: 36; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-cursor: not-allowed;");
    }

    private void setupPassengerSeat() {
        StackPane passengerPane = (StackPane) passengerSeat.getChildren().get(0);
        Label priceLabel = (Label) passengerSeat.getChildren().get(2);
        if (reservedSeatsList.stream().anyMatch(s -> s.trim().equalsIgnoreCase("A2"))) {
            markSeatAsReserved(passengerPane, priceLabel, "A2", 0);
        }
    }

    private VBox addSeatToRow(HBox row, String seatId, double price) {
        VBox seatContainer = new VBox();
        seatContainer.setAlignment(javafx.geometry.Pos.CENTER);
        seatContainer.setSpacing(5);
        seatContainer.setId(seatId.toLowerCase() + "Seat");

        StackPane seatPane = new StackPane();
        seatPane.setStyle("-fx-background-color: white; -fx-border-color: #3b82f6; -fx-border-width: 2; " +
                "-fx-border-radius: 5; -fx-background-radius: 5; -fx-min-width: 32; -fx-min-height: 36; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-cursor: hand;");
        seatPane.setOnMouseClicked(this::handleSeatSelection);

        Label seatLabel = new Label(seatId);
        seatLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1e3a8a;");
        seatPane.getChildren().add(seatLabel);

        Label priceLabel = new Label(String.format("$%.2f", price));
        priceLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #1d4ed8;");

        seatContainer.getChildren().addAll(seatPane, priceLabel);
        row.getChildren().add(seatContainer);

        return seatContainer;
    }

    private void markSeatAsReserved(StackPane seatPane, Label priceLabel, String seatId, int index) {
        seatPane.setStyle("-fx-background-color: #d1d5db; -fx-border-color: #9ca3af; -fx-border-width: 2; " +
                "-fx-border-radius: 5; -fx-background-radius: 5; -fx-min-width: 32; -fx-min-height: 36; " +
                "-fx-max-width: 32; -fx-max-height: 36; -fx-pref-width: 32; -fx-pref-height: 36; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-cursor: not-allowed;");
        Label seatLabel = (Label) seatPane.getChildren().get(index);
        seatLabel.setText(seatId);
        seatLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #6b7280;");

        priceLabel.setText("Reserved");
        priceLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #6b7280;");
    }

    private void updateSeatCounts() {
        totalSeatsLabel.setText(String.valueOf(totalSeats));
        availableSeatsLabel.setText(String.valueOf(availableSeats));
    }

    @FXML
    private void handleSeatSelection(javafx.scene.input.MouseEvent event) {
        VBox seatContainer = (VBox) ((StackPane) event.getSource()).getParent();
        StackPane seatPane = (StackPane) seatContainer.getChildren().get(0);
        Label seatLabel = (Label) seatPane.getChildren().get(0);
        String seatId = seatLabel.getText();

        if (selectedSeats.containsKey(seatId)) {
            seatPane.setStyle("-fx-background-color: white; -fx-border-color: #3b82f6; -fx-border-width: 2; " +
                    "-fx-border-radius: 5; -fx-background-radius: 5; -fx-min-width: 32; -fx-min-height: 36; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-cursor: hand;");
            selectedSeats.remove(seatId);
            seatAssignments.remove(seatId);
        } else {
            seatPane.setStyle("-fx-background-color: #dbeafe; -fx-border-color: #3b82f6; -fx-border-width: 2; " +
                    "-fx-border-radius: 5; -fx-background-radius: 5; -fx-min-width: 32; -fx-min-height: 36; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-cursor: hand;");
            selectedSeats.put(seatId, true);
        }

        updateSelectedSeatsDisplay();
        updateEmployeeSelection();
        updateContinueButton();
    }

    private void updateSelectedSeatsDisplay() {
        selectedSeatsContainer.getChildren().clear();

        if (selectedSeats.isEmpty()) {
            Label noSeatsLabel = new Label("No seats selected yet");
            noSeatsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280; -fx-alignment: CENTER; -fx-padding: 15 0;");
            selectedSeatsContainer.getChildren().add(noSeatsLabel);
            return;
        }

        selectedSeatsContainer.setPrefWidth(300);
        selectedSeatsContainer.setMinHeight(100);
        selectedSeatsContainer.setMaxWidth(Double.MAX_VALUE);

        double totalPrice = 0;
        for (String seatId : selectedSeats.keySet()) {
            double price = seatPrices.getOrDefault(seatId, 0.0);
            totalPrice += price;

            HBox seatInfo = new HBox();
            seatInfo.setStyle("-fx-background-color: #dbeafe; -fx-background-radius: 8; -fx-padding: 10; -fx-spacing: 10;");
            seatInfo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            seatInfo.setPrefWidth(280);
            seatInfo.setMaxWidth(Double.MAX_VALUE);

            VBox seatDetails = new VBox();
            Label seatLabel = new Label(seatId);
            seatLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e40af;");

            Label positionLabel = new Label(getSeatPosition(seatId));
            positionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3b82f6;");

            seatDetails.getChildren().addAll(seatLabel, positionLabel);

            Label priceLabel = new Label(String.format("$%.2f", price));
            priceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e40af;");

            HBox.setHgrow(seatDetails, javafx.scene.layout.Priority.ALWAYS);
            seatInfo.getChildren().addAll(seatDetails, priceLabel);
            selectedSeatsContainer.getChildren().add(seatInfo);
        }

        HBox totalBox = new HBox();
        totalBox.setStyle("-fx-background-color: #bfdbfe; -fx-background-radius: 8; -fx-padding: 10; -fx-spacing: 10;");
        totalBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        totalBox.setPrefWidth(280);
        totalBox.setMaxWidth(Double.MAX_VALUE);

        Label totalLabel = new Label("Total:");
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e40af;");

        Label totalPriceLabel = new Label(String.format("$%.2f", totalPrice));
        totalPriceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e40af;");

        totalBox.getChildren().addAll(totalLabel, totalPriceLabel);
        selectedSeatsContainer.getChildren().add(totalBox);
    }

    private String getSeatPosition(String seatId) {
        switch (seatId) {
            case "A2": return "Front passenger seat";
            case "B1": return "Back left seat";
            case "B2": return "Back right seat";
            case "B3": return "Back middle seat";
            case "C1": return "Third row left";
            case "C2": return "Third row right";
            case "C3": return "Third row middle";
            case "D1": return "Fourth row left";
            case "D2": return "Fourth row middle";
            case "D3": return "Fourth row right";
            default: return "Passenger seat";
        }
    }

    private void updateEmployeeSelection() {
        employeeSelectionContainer.getChildren().clear();

        if (selectedSeats.isEmpty()) {
            noSeatsMessage.setVisible(true);
            employeeSelectionContainer.getChildren().add(noSeatsMessage);
            return;
        }

        noSeatsMessage.setVisible(false);
        Map<String, User> tempAssignments = new HashMap<>(seatAssignments);

        employeeSelectionContainer.setPrefWidth(300);
        employeeSelectionContainer.setMinHeight(100);
        employeeSelectionContainer.setMaxWidth(Double.MAX_VALUE);

        for (String seatId : selectedSeats.keySet()) {
            VBox seatAssignment = new VBox();
            seatAssignment.setStyle("-fx-background-color: #dbeafe; -fx-background-radius: 8; -fx-padding: 12; -fx-spacing: 8; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");
            seatAssignment.setPrefWidth(280);
            seatAssignment.setMaxWidth(Double.MAX_VALUE);

            HBox seatHeader = new HBox();
            seatHeader.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            seatHeader.setSpacing(10);

            StackPane seatBadge = new StackPane();
            seatBadge.setStyle("-fx-background-color: #bfdbfe; -fx-background-radius: 6; -fx-min-width: 32; -fx-min-height: 32;");

            Label seatNumber = new Label(seatId);
            seatNumber.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e40af;");
            seatBadge.getChildren().add(seatNumber);

            Label seatTitle = new Label("Seat " + seatId);
            seatTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: medium; -fx-text-fill: #1e40af;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Label seatPosition = new Label(getSeatPosition(seatId));
            seatPosition.setStyle("-fx-font-size: 11px; -fx-background-color: #bfdbfe; -fx-background-radius: 12; -fx-padding: 2 6; -fx-text-fill: #1e40af;");

            seatHeader.getChildren().addAll(seatBadge, seatTitle, spacer, seatPosition);

            HBox employeeSelectionBox = new HBox();
            employeeSelectionBox.setSpacing(10);
            employeeSelectionBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            ComboBox<User> employeeCombo = new ComboBox<>();
            employeeCombo.setPromptText("Select an employee*");
            employeeCombo.setStyle("-fx-background-color: white; -fx-border-color: #93c5fd; -fx-border-radius: 8; -fx-background-radius: 8;");
            employeeCombo.setPrefWidth(200);

            ImageView employeePhoto = new ImageView();
            employeePhoto.setFitHeight(50);
            employeePhoto.setFitWidth(50);
            employeePhoto.setVisible(false);
            employeePhoto.setPreserveRatio(true);

            List<User> availableEmployees = new ArrayList<>(employees);
            for (Map.Entry<String, User> entry : seatAssignments.entrySet()) {
                if (!entry.getKey().equals(seatId)) {
                    availableEmployees.removeIf(user -> user.getId() == entry.getValue().getId());
                }
            }

            employeeCombo.setItems(FXCollections.observableArrayList(availableEmployees));

            employeeCombo.setCellFactory(lv -> new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                        setText(user.getFirstName() + " " + user.getLastName());
                    }
                }
            });

            employeeCombo.setUserData(seatId);

            if (seatAssignments.containsKey(seatId)) {
                employeeCombo.setValue(seatAssignments.get(seatId));

                String photoPath = seatAssignments.get(seatId).getImage();
                if (photoPath != null && !photoPath.isEmpty()) {
                    try {
                        Image image = new Image(photoPath);
                        employeePhoto.setImage(image);
                        employeePhoto.setVisible(true);
                    } catch (Exception e) {
                        System.err.println("Error loading employee photo: " + e.getMessage());
                    }
                }
            }

            employeeCombo.setOnAction(event -> {
                User selectedUser = employeeCombo.getValue();
                String currentSeatId = (String) employeeCombo.getUserData();

                if (selectedUser != null) {
                    seatAssignments.put(currentSeatId, selectedUser);

                    String photoPath = selectedUser.getImage();
                    if (photoPath != null && !photoPath.isEmpty()) {
                        try {
                            Image image = new Image(photoPath);
                            employeePhoto.setImage(image);
                            employeePhoto.setVisible(true);
                        } catch (Exception e) {
                            System.err.println("Error loading employee photo: " + e.getMessage());
                            employeePhoto.setVisible(false);
                        }
                    } else {
                        employeePhoto.setVisible(false);
                    }

                    updateContinueButton();
                } else {
                    seatAssignments.remove(currentSeatId);
                    employeePhoto.setVisible(false);
                }

                for (Node node : employeeSelectionContainer.getChildren()) {
                    if (node instanceof VBox) {
                        for (Node child : ((VBox) node).getChildren()) {
                            if (child instanceof HBox) {
                                for (Node hboxChild : ((HBox) child).getChildren()) {
                                    if (hboxChild instanceof ComboBox && hboxChild != employeeCombo) {
                                        ComboBox<User> otherCombo = (ComboBox<User>) hboxChild;
                                        String otherSeatId = (String) otherCombo.getUserData();
                                        User currentValue = otherCombo.getValue();

                                        List<User> otherAvailableEmployees = new ArrayList<>(employees);
                                        for (User assignedUser : seatAssignments.values()) {
                                            if (assignedUser != currentValue) {
                                                otherAvailableEmployees.removeIf(user -> user.getId() == assignedUser.getId());
                                            }
                                        }
                                        otherCombo.setItems(FXCollections.observableArrayList(otherAvailableEmployees));
                                        otherCombo.setValue(currentValue);
                                    }
                                }
                            }
                        }
                    }
                }

                updateContinueButton();
            });

            employeeSelectionBox.getChildren().addAll(employeeCombo, employeePhoto);
            seatAssignment.getChildren().addAll(seatHeader, employeeSelectionBox);
            employeeSelectionContainer.getChildren().add(seatAssignment);
        }
    }

    private void updateContinueButton() {
        boolean canContinue = !selectedSeats.isEmpty() && allSeatsHaveEmployees();
        continueButton.setDisable(!canContinue);

        if (canContinue) {
            continueButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: medium; " +
                    "-fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;");
        } else {
            continueButton.setStyle("-fx-background-color: #9ca3af; -fx-text-fill: #4b5563; -fx-font-weight: medium; " +
                    "-fx-background-radius: 8; -fx-padding: 12; -fx-cursor: default;");
        }
    }

    private boolean allSeatsHaveEmployees() {
        return !selectedSeats.isEmpty();
    }

    @FXML
    private void handleContinueAction(ActionEvent event) {
        System.out.println("Continue button clicked with seats: " + selectedSeats.keySet());
        System.out.println("Selected employees: " + seatAssignments.values());
        frontOfficeController.navigateTo("dashboard/car/offer-book-form.fxml");
        ((OfferBookForm) frontOfficeController.getController()).initialize(offer, new ArrayList<>(selectedSeats.keySet()), new ArrayList<>(seatAssignments.values()));
    }

    @FXML
    private void handleBackAction(ActionEvent event) {
        System.out.println("Back button clicked");
    }
}