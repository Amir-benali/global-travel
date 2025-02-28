package com.globalTravel.controllers.hotel;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.services.hotel.HotelService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

public class HotelGrid implements Navigatable {

    private DashBoard dashBoardController;

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private FlowPane hotelsGrid;

    @FXML
    private TextField searchField;

    private List<Hotel> hotels;

    @FXML
    public void initialize() {
        loadHotels();
    }

    private void loadHotels() {
        hotelsGrid.getChildren().clear();
        hotels = getHotels();

        // Trier les hôtels par nom
        hotels.sort((h1, h2) -> h1.getNom_h().compareToIgnoreCase(h2.getNom_h()));

        for (Hotel hotel : hotels) {
            VBox hotelCard = createHotelCard(hotel);
            hotelsGrid.getChildren().add(hotelCard);
        }
    }

    private VBox createHotelCard(Hotel hotel) {
        VBox card = new VBox(10);
        card.getStyleClass().add("hotel-card");

        VBox hotelInfo = new VBox(5);
        hotelInfo.getStyleClass().add("hotel-info");

        // Hotel image
        ImageView hotelLogoView = new ImageView(new Image("/images/hotelLogo.jpg", 200, 150, true, true));
        hotelLogoView.getStyleClass().add("hotel-logo");

        // Hotel details
        Label nameLabel = new Label("Hotel: " + hotel.getNom_h());
        nameLabel.getStyleClass().add("hotel-title");

        Label addressLabel = new Label("Address: " + hotel.getAdresse_h());
        addressLabel.getStyleClass().add("hotel-address");

        Label cityLabel = new Label("City: " + hotel.getVille_h());
        cityLabel.getStyleClass().add("hotel-city");

        Label categoryLabel = new Label("Category: " + hotel.getCategorie_h() + " Stars");
        categoryLabel.getStyleClass().add("hotel-category");

        // Buttons
        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> {
            try {
                navigateToUpdateHotel(hotel);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        updateButton.getStyleClass().add("view-details-button");

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> deleteHotel(hotel));

        HBox buttonHbox = new HBox(10);
        buttonHbox.getStyleClass().add("button-hbox");
        buttonHbox.getChildren().addAll(updateButton, deleteButton);

        hotelInfo.getChildren().addAll(hotelLogoView, nameLabel, addressLabel, cityLabel, categoryLabel, buttonHbox);
        card.getChildren().add(hotelInfo);

        return card;
    }

    private void deleteHotel(Hotel hotel) {
        if (hotel == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Veuillez sélectionner un hôtel à supprimer.");
            errorAlert.showAndWait();
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
                    loadHotels();

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Succès");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("L'hôtel a été supprimé avec succès !");
                    successAlert.showAndWait();
                } catch (Exception e) {
                    System.err.println("Erreur lors de la suppression de l'hôtel : " + e.getMessage());

                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Erreur lors de la suppression de l'hôtel !");
                    errorAlert.showAndWait();
                }
            }
        });
    }

    private void navigateToUpdateHotel(Hotel hotel) throws IOException {
        dashBoardController.navigateTo("dashboard/hotel/hotel-update-form.fxml");
        HotelUpdateForm updateForm = (HotelUpdateForm) dashBoardController.getController();
        updateForm.setHotelToEdit(hotel);
    }

    private List<Hotel> getHotels() {
        HotelService hotelService = new HotelService();
        return hotelService.rechercher();
    }

    @FXML
    private void searchHotelByName(ActionEvent event) {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadHotels();
            return;
        }

        HotelService hotelService = new HotelService();
        List<Hotel> filteredHotels = hotelService.rechercherParNom(searchText);

        // Trier les hôtels filtrés par nom
        filteredHotels.sort((h1, h2) -> h1.getNom_h().compareToIgnoreCase(h2.getNom_h()));

        hotelsGrid.getChildren().clear();
        for (Hotel hotel : filteredHotels) {
            VBox hotelCard = createHotelCard(hotel);
            hotelsGrid.getChildren().add(hotelCard);
        }
    }

    public void addHotel(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/hotel/hotel-create-form.fxml");
    }

    public void navigateToChambre(ActionEvent actionEvent) {
        dashBoardController.navigateTo("dashboard/hotel/chambre-grid.fxml");
    }

    // Méthode pour exporter les hôtels en PDF
    @FXML
    private void exportToPDF(ActionEvent event) {
        // Créer un FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("HotelsList.pdf"); // Nom par défaut du fichier

        // Afficher la boîte de dialogue pour choisir l'emplacement
        File file = fileChooser.showSaveDialog(hotelsGrid.getScene().getWindow());

        // Vérifier si l'utilisateur a choisi un fichier
        if (file != null) {
            try {
                // Créer le document PDF
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Ajouter un titre au PDF
                document.add(new Paragraph("Liste des Hôtels\n\n"));

                // Ajouter les détails de chaque hôtel
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

                // Afficher une alerte de succès
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Le fichier PDF a été généré avec succès !");
                successAlert.showAndWait();
            } catch (DocumentException | IOException e) {
                e.printStackTrace();

                // Afficher une alerte d'erreur
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Erreur lors de la génération du PDF !");
                errorAlert.showAndWait();
            }
        }
    }

    // Méthode pour calculer la répartition des hôtels par catégorie
    private Map<Integer, Long> getHotelsByCategory() {
        return hotels.stream()
                .collect(Collectors.groupingBy(Hotel::getCategorie_h, Collectors.counting()));
    }

    // Méthode pour afficher la répartition des hôtels par catégorie
    @FXML
    private void showHotelsByCategory() {
        Map<Integer, Long> hotelsByCategory = getHotelsByCategory();

        // Construire le message à afficher
        StringBuilder message = new StringBuilder("Répartition des hôtels par catégorie :\n\n");
        for (Map.Entry<Integer, Long> entry : hotelsByCategory.entrySet()) {
            message.append(entry.getKey()).append(" étoiles : ").append(entry.getValue()).append(" hôtels\n");
        }

        // Afficher une boîte de dialogue avec les résultats
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Statistiques des hôtels");
        alert.setHeaderText(null);
        alert.setContentText(message.toString());
        alert.showAndWait();
    }

    // Méthode pour exporter les statistiques au format PDF
    @FXML
    private void exportStatisticsToPDF(ActionEvent event) {
        Map<Integer, Long> hotelsByCategory = getHotelsByCategory();

        // Créer un FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer les statistiques en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("HotelsStatistics.pdf");

        // Afficher la boîte de dialogue pour choisir l'emplacement
        File file = fileChooser.showSaveDialog(hotelsGrid.getScene().getWindow());

        if (file != null) {
            try {
                // Créer le document PDF
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Ajouter un titre au PDF
                document.add(new Paragraph("Statistiques des hôtels par catégorie\n\n"));

                // Ajouter les détails de chaque catégorie
                for (Map.Entry<Integer, Long> entry : hotelsByCategory.entrySet()) {
                    document.add(new Paragraph(entry.getKey() + " étoiles : " + entry.getValue() + " hôtels"));
                }

                document.close();

                // Afficher une alerte de succès
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Le fichier PDF a été généré avec succès !");
                successAlert.showAndWait();
            } catch (DocumentException | IOException e) {
                e.printStackTrace();

                // Afficher une alerte d'erreur
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Erreur lors de la génération du PDF !");
                errorAlert.showAndWait();
            }
        }
    }
}