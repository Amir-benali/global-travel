package com.globalTravel.controllers.car;

import com.globalTravel.controllers.DashBoard;
import com.globalTravel.controllers.Navigatable;
import com.globalTravel.models.car.Offer;
import com.globalTravel.services.car.OfferService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class OfferGrid implements Navigatable {
    private DashBoard dashBoardController;
    private OfferService offerService = new OfferService();
    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private FlowPane offersGrid;

    private List<Offer> offers;

    @FXML
    public void initialize() {
        loadOffers();
    }

    private void loadOffers() {
        offersGrid.getChildren().clear();
        offers = getOffers();

        for (Offer offer : offers) {
            VBox offerCard = createOfferCard(offer);
            offersGrid.getChildren().add(offerCard);
        }
        System.out.println("Offers: " + offers);
    }

    private VBox createOfferCard(Offer offer) {
        VBox card = new VBox(15);
        card.getStyleClass().addAll("offer-card", "modern-card");
        card.setPadding(new Insets(15));

        // Offer Image
        String imagePath = "/images/carlogo.png"; // Default image
        Image offerImage = new Image(imagePath, 325, 200, false, true);

        ImageView offerImageView = new ImageView(offerImage);
        offerImageView.setFitWidth(325);
        offerImageView.setFitHeight(200);
        offerImageView.setPreserveRatio(false);
        offerImageView.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.2)));
        offerImageView.getStyleClass().add("offer-image");

        // Offer Info Container
        VBox offerInfo = new VBox(10);
        offerInfo.getStyleClass().add("offer-info");

        // Offer Details Grid (3 Rows x 2 Columns)
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(10);

        detailsGrid.add(createIcon(FontAwesomeIcon.FILE_TEXT, Color.BLUE), 0, 0);
        detailsGrid.add(new Label(offer.getDescription()), 1, 0);

        detailsGrid.add(createIcon(FontAwesomeIcon.CALENDAR, Color.GREEN), 0, 1);
        detailsGrid.add(new Label(offer.getDate().toString()), 1, 1);

        detailsGrid.add(createIcon(FontAwesomeIcon.DOLLAR, Color.ORANGE), 2, 0);
        detailsGrid.add(new Label("$" + offer.getPrice()), 3, 0);



        detailsGrid.add(createIcon(FontAwesomeIcon.CAR, Color.PURPLE), 2, 1);
        detailsGrid.add(new Label(offer.getCar().getBrand() + " " + offer.getCar().getModel()), 3, 1);

        detailsGrid.getStyleClass().add("details-grid");

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button detailsButton = new Button("Show Details");
        detailsButton.getStyleClass().addAll("modern-button", "details-button");
        detailsButton.setOnAction(e -> showOfferDetails(offer));

        Button updateButton = new Button("Update");
        updateButton.getStyleClass().addAll("modern-button", "update-button");
        updateButton.setOnAction(e -> {
            try {
                navigateToUpdateOffer(offer);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("modern-button", "delete-button");
        deleteButton.setOnAction(e -> deleteOffer(offer));

        buttonBox.getChildren().addAll(detailsButton, updateButton, deleteButton);

        // Assemble all components
        offerInfo.getChildren().addAll(detailsGrid);
        card.getChildren().addAll(offerImageView, offerInfo, buttonBox);

        // Add hover effect
        card.setOnMouseEntered(e -> card.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.3))));
        card.setOnMouseExited(e -> card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.1))));

        return card;
    }

    // Utility method to create FontAwesome icons with color
    private FontAwesomeIconView createIcon(FontAwesomeIcon icon, Color color) {
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setGlyphSize(20); // Increased size
        iconView.setFill(color); // Set color
        return iconView;
    }



    private void showOfferDetails(Offer offer) {
        dashBoardController.navigateTo("dashboard/car/offer-details.fxml");
        ((OfferDetails) dashBoardController.getController()).initialize(offer);
    }

    private void deleteOffer(Offer offer) {
        System.out.println("Deleting: " + offer);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Are you sure you want to delete this offer?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            offerService.supprimer(offer);
            loadOffers();
        }
    }

    private void navigateToUpdateOffer(Offer offer) throws IOException {
        dashBoardController.navigateTo("dashboard/car/offer-update-form.fxml");
        OfferUpdateForm updateForm = (OfferUpdateForm) dashBoardController.getController();
        updateForm.initialize(offer);
    }

    private List<Offer> getOffers() {
        return offerService.rechercher();
    }

    public void addOffer(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/car/offer-create-form.fxml");
    }

    public void navigateToRoute(ActionEvent actionEvent) {
//        dashBoardController.navigateTo("dashboard/offer/route-grid.fxml");
    }

    public void navigateToCar(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/car/car-grid.fxml");
    }

    public void navigateToDriver(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/car/driver-grid.fxml");
    }
}