package com.globalTravel.controllers.hotel;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.services.hotel.HotelService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;

import javafx.scene.layout.GridPane;

import javafx.scene.paint.Color;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

public class HotelGrid implements Navigatable, FrontNavigatable {

    @FXML private Button btnAddHotel; // Bouton pour ajouter un hôtel
    @FXML private FlowPane hotelsGrid; // Conteneur pour les cartes d'hôtel
    @FXML private TextField searchField; // Champ de recherche

    private DashBoard dashBoardController; // Contrôleur du tableau de bord
    private FrontOffice frontOfficeController; // Contrôleur du front office
    private List<Hotel> hotels; // Liste des hôtels

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    public void initialize() {
        loadHotels(); // Charger les hôtels au démarrage
    }

    /**
     * Charge les hôtels dans le FlowPane hotelsGrid.
     */
    private void loadHotels() {
        hotelsGrid.getChildren().clear(); // Vider le contenu actuel
        hotels = getHotels(); // Récupérer les hôtels

        // Trier les hôtels par nom
        hotels.sort((h1, h2) -> h1.getNom_h().compareToIgnoreCase(h2.getNom_h()));

        // Ajouter les cartes d'hôtel au FlowPane
        for (Hotel hotel : hotels) {
            Node hotelCard = createHotelCard(hotel);
            hotelsGrid.getChildren().add(hotelCard);
        }
    }

    /**
     * Crée une carte d'hôtel avec les informations et les boutons.
     *
     * @param hotel L'hôtel à afficher.
     * @return Une VBox représentant la carte d'hôtel.
     */
    private Node createHotelCard(Hotel hotel) {
        VBox card = new VBox(15);
        card.getStyleClass().add("hotel-card");
        card.setPadding(new Insets(15));

        // Image de l'hôtel
        String imagePath = "/images/hotelLogo.jpg"; // Image par défaut
        Image hotelImage = new Image(getClass().getResource(imagePath).toExternalForm(), 300, 200, false, true);
        ImageView hotelImageView = new ImageView(hotelImage);
        hotelImageView.setFitWidth(300);
        hotelImageView.setFitHeight(200);
        hotelImageView.setPreserveRatio(false);
        hotelImageView.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.2)));
        hotelImageView.getStyleClass().add("hotel-image");

        // Informations de l'hôtel
        VBox hotelInfo = new VBox(10);
        hotelInfo.getStyleClass().add("hotel-info");

        // Nom de l'hôtel avec icône
        FontAwesomeIconView buildingIcon = new FontAwesomeIconView(FontAwesomeIcon.BUILDING);
        buildingIcon.setFill(Color.DARKORANGE);
        buildingIcon.setGlyphSize(20);
        Label nameLabel = new Label();
        nameLabel.setGraphic(buildingIcon);
        nameLabel.setText(" " + hotel.getNom_h());
        nameLabel.getStyleClass().add("hotel-name");

        // Grille pour les détails de l'hôtel
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(10);
        detailsGrid.getStyleClass().add("details-grid");

        // Adresse avec icône
        detailsGrid.add(createIcon(FontAwesomeIcon.MAP_MARKER, Color.BLUE), 0, 0);
        detailsGrid.add(new Label(hotel.getAdresse_h()), 1, 0);

        // Ville avec icône
        detailsGrid.add(createIcon(FontAwesomeIcon.GLOBE, Color.GREEN), 0, 1);
        detailsGrid.add(new Label(hotel.getVille_h()), 1, 1);

        // Catégorie avec icône
        detailsGrid.add(createIcon(FontAwesomeIcon.STAR, Color.ORANGE), 0, 2);
        detailsGrid.add(new Label(hotel.getCategorie_h() + " Stars"), 1, 2);

        // Conteneur pour les boutons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getStyleClass().add("button-box");

        // Bouton "Update" avec icône
        Button updateButton = new Button();
        FontAwesomeIconView updateIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        updateIcon.setSize("1.5em");
        updateButton.setGraphic(updateIcon);
        updateButton.getStyleClass().addAll("hotel-button", "update-button");
        updateButton.setOnAction(e -> {
            try {
                navigateToUpdateHotel(hotel);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Bouton "Delete" avec icône
        Button deleteButton = new Button();
        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
        deleteIcon.setSize("1.5em");
        deleteButton.setGraphic(deleteIcon);
        deleteButton.getStyleClass().addAll("hotel-button", "delete-button");
        deleteButton.setOnAction(e -> deleteHotel(hotel));

        // Bouton "Details" avec icône
        Button detailsButton = new Button();
        FontAwesomeIconView detailsIcon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH);
        detailsIcon.setSize("1.5em");
        detailsButton.setGraphic(detailsIcon);
        detailsButton.getStyleClass().addAll("hotel-button", "view-details-button");

        detailsButton.setOnAction(e -> navigateToChambreManagement(hotel));

        // Ajouter les boutons au conteneur
        buttonBox.getChildren().addAll(updateButton, deleteButton, detailsButton);

        // Assembler tous les composants
        hotelInfo.getChildren().addAll(nameLabel, detailsGrid);
        card.getChildren().addAll(hotelImageView, hotelInfo, buttonBox);

        // Effet de survol
        card.setOnMouseEntered(e -> card.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.3))));
        card.setOnMouseExited(e -> card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.1))));

        return card;
    }

    /**
     * Crée une icône avec une couleur spécifique.
     *
     * @param icon  L'icône à créer.
     * @param color La couleur de l'icône.
     * @return Une FontAwesomeIconView.
     */
    private FontAwesomeIconView createIcon(FontAwesomeIcon icon, Color color) {
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setGlyphSize(20);
        iconView.setFill(color);
        return iconView;
    }

    /**
     * Navigue vers la gestion des chambres pour un hôtel spécifique.
     *
     * @param hotel L'hôtel sélectionné.
     */
    private void navigateToChambreManagement(Hotel hotel) {
        if (frontOfficeController != null) {
            frontOfficeController.navigateTo("dashboard/hotel/chambre-grid.fxml");
            ChambreGrid chambreGrid = (ChambreGrid) frontOfficeController.getController();
            chambreGrid.setHotelId(hotel.getId_hotel_h()); // Passer l'ID de l'hôtel à ChambreGrid
        } else {
            dashBoardController.navigateTo("dashboard/hotel/chambre-grid.fxml");
            ChambreGrid chambreGrid = (ChambreGrid) dashBoardController.getController();
            chambreGrid.setHotelId(hotel.getId_hotel_h()); // Passer l'ID de l'hôtel à ChambreGrid
        }
    }

    /**
     * Supprime un hôtel.
     *
     * @param hotel L'hôtel à supprimer.
     */
    private void deleteHotel(Hotel hotel) {
        if (hotel == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un hôtel à supprimer.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Supprimer l'hôtel ?");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'hôtel : " + hotel.getNom_h() + " ?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    HotelService hotelService = new HotelService();
                    hotelService.supprimer(hotel);
                    System.out.println("Hôtel supprimé avec succès : " + hotel);
                    loadHotels(); // Recharger les hôtels
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "L'hôtel a été supprimé avec succès !");
                } catch (Exception e) {
                    System.err.println("Erreur lors de la suppression de l'hôtel : " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de l'hôtel !");
                }
            }
        });
    }

    /**
     * Affiche une alerte.
     *
     * @param alertType Le type d'alerte.
     * @param title     Le titre de l'alerte.
     * @param message   Le message de l'alerte.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Navigue vers le formulaire de mise à jour d'un hôtel.
     *
     * @param hotel L'hôtel à mettre à jour.
     * @throws IOException Si une erreur d'E/S se produit.
     */
    private void navigateToUpdateHotel(Hotel hotel) throws IOException {
        dashBoardController.navigateTo("dashboard/hotel/hotel-update-form.fxml");
        HotelUpdateForm updateForm = (HotelUpdateForm) dashBoardController.getController();
        updateForm.setHotelToEdit(hotel);
    }

    /**
     * Récupère tous les hôtels.
     *
     * @return Une liste d'hôtels.
     */
    private List<Hotel> getHotels() {
        HotelService hotelService = new HotelService();
        return hotelService.rechercher();
    }

    /**
     * Recherche les hôtels par nom.
     *
     * @param event L'événement de clic.
     */
    @FXML
    private void searchHotelByName(ActionEvent event) {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadHotels(); // Recharger tous les hôtels si le champ de recherche est vide
            return;
        }

        HotelService hotelService = new HotelService();
        List<Hotel> filteredHotels = hotelService.rechercherParNom(searchText);

        // Trier les hôtels filtrés par nom
        filteredHotels.sort((h1, h2) -> h1.getNom_h().compareToIgnoreCase(h2.getNom_h()));

        hotelsGrid.getChildren().clear();
        for (Hotel hotel : filteredHotels) {
            Node hotelCard = createHotelCard(hotel);
            hotelsGrid.getChildren().add(hotelCard);
        }
    }

    /**
     * Navigue vers le formulaire de création d'un hôtel.
     *
     * @param actionEvent L'événement de clic.
     */
    public void addHotel(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/hotel/hotel-create-form.fxml");
    }

    /**
     * Exporte la liste des hôtels en PDF.
     *
     * @param event L'événement de clic.
     */
    @FXML
    private void exportToPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("HotelsList.pdf");

        File file = fileChooser.showSaveDialog(hotelsGrid.getScene().getWindow());

        if (file != null) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                document.add(new Paragraph("Liste des Hôtels\n\n"));

                for (Hotel hotel : hotels) {
                    document.add(new Paragraph("Nom: " + hotel.getNom_h()));
                    document.add(new Paragraph("Adresse: " + hotel.getAdresse_h()));
                    document.add(new Paragraph("Ville: " + hotel.getVille_h()));
                    document.add(new Paragraph("Catégorie: " + hotel.getCategorie_h() + " étoiles"));
                    document.add(new Paragraph("Services: " + hotel.getServices_h()));
                    document.add(new Paragraph("Coordonnées: " + hotel.getCoordonnees_h()));
                    document.add(new Paragraph("Avis: " + hotel.getAvis_h()));
                    document.add(new Paragraph("\n"));
                }

                document.close();

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le fichier PDF a été généré avec succès !");
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la génération du PDF !");
            }
        }
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
        System.out.println("Setting FrontOfficeController: " + frontOfficeController);
        updateButtonVisibility();
        btnAddHotel.setVisible(false);
    }

    /**
     * Masque les boutons "Update" et "Delete" si le contrôleur FrontOffice est actif.
     */
    private void updateButtonVisibility() {
        for (Node node : hotelsGrid.getChildren()) {
            if (node instanceof VBox) {
                VBox card = (VBox) node;
                for (Node child : card.getChildren()) {
                    if (child instanceof HBox) {
                        HBox buttonBox = (HBox) child;
                        // Masquer les boutons "Update" et "Delete"
                        buttonBox.getChildren().removeIf(btn ->
                                btn instanceof Button &&
                                        (btn.getStyleClass().contains("update-button") || btn.getStyleClass().contains("delete-button"))
                        );
                    }
                }
            }
        }
    }
}