package com.globalTravel.controllers.hotel;

import com.globalTravel.controllers.backoffice.DashBoard;
import com.globalTravel.controllers.backoffice.Navigatable;
import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.services.hotel.HotelService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HotelUpdateForm implements Navigatable {

    @FXML private TextField hotelNameField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private ComboBox<String> cityComboBox;
    @FXML private ComboBox<Integer> categoryField;
    @FXML private TextField servicesField;
    @FXML private TextField coordinatesField;
    @FXML private TextArea reviewsField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @FXML private ComboBox<String> languageComboBox;
    @FXML private Button translateButton;
    @FXML private TextArea translatedReviewTextArea;

    @FXML private ComboBox<String> emojiComboBox;
    @FXML private Button emojiButton;

    private DashBoard dashBoardController;
    private HotelService hotelService = new HotelService();
    private Stage stage;
    private Hotel hotelToEdit;

    // Map pour stocker les villes par pays
    private Map<String, ObservableList<String>> citiesByCountry = new HashMap<>();

    @FXML
    public void initialize() {
        System.out.println("Initializing HotelUpdateForm...");
        loadCountries();
        loadCitiesFromCSV(); // Charger les villes depuis le fichier CSV
        countryComboBox.setOnAction(event -> onCountrySelected());

        // Initialiser la ComboBox pour la catégorie (1-7)
        ObservableList<Integer> categories = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7);
        categoryField.setItems(categories);

        // Initialiser la ComboBox pour les langues de traduction
        languageComboBox.setItems(FXCollections.observableArrayList("en", "fr", "es", "de"));

        // Si un hôtel à modifier est déjà défini, remplir le formulaire
        if (hotelToEdit != null) {
            populateForm();
        }

        // Ajout d'emojis courants dans la ComboBox
        ObservableList<String> emojis = FXCollections.observableArrayList("😀", "😍", "😎", "😢", "😡", "👍", "👎", "⭐", "🔥", "💯");
        emojiComboBox.setItems(emojis);

        // Sélectionner le premier emoji par défaut
        emojiComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleInsertEmoji() {
        String selectedEmoji = emojiComboBox.getValue();
        if (selectedEmoji != null) {
            reviewsField.appendText(selectedEmoji + " ");
        }
    }

    public void setHotelToEdit(Hotel hotelToEdit) {
        this.hotelToEdit = hotelToEdit;
        if (hotelToEdit != null) {
            populateForm();
        }
    }

    private void populateForm() {
        hotelNameField.setText(hotelToEdit.getNom_h());
        addressField.setText(hotelToEdit.getAdresse_h());
        cityComboBox.setValue(hotelToEdit.getVille_h());
        countryComboBox.setValue(hotelToEdit.getPays_h());
        categoryField.setValue(hotelToEdit.getCategorie_h());
        servicesField.setText(hotelToEdit.getServices_h());
        coordinatesField.setText(hotelToEdit.getCoordonnees_h());
        reviewsField.setText(hotelToEdit.getAvis_h());
    }

    // Méthode pour charger les pays (REST Countries API)
    private void loadCountries() {
        String apiUrl = "https://restcountries.com/v3.1/all";
        ObservableList<String> countriesList = FXCollections.observableArrayList();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            CloseableHttpResponse response = httpClient.execute(request);

            String jsonResponse = EntityUtils.toString(response.getEntity());
            JsonArray jsonArray = JsonParser.parseString(jsonResponse).getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject country = element.getAsJsonObject();
                String countryName = country.get("name").getAsJsonObject().get("common").getAsString();
                countriesList.add(countryName);
            }

            countryComboBox.setItems(countriesList);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des pays : " + e.getMessage());
            showError("Erreur lors du chargement des pays. Vérifiez votre connexion Internet.");
        }
    }

    // Méthode pour charger les villes depuis le fichier CSV
    private void loadCitiesFromCSV() {
        try (InputStream inputStream = getClass().getResourceAsStream("/worldcities.csv");
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length >= 5) {
                    String cityName = nextLine[0]; // Nom de la ville
                    String countryName = nextLine[4]; // Nom du pays

                    // Ajouter la ville à la liste des villes du pays
                    citiesByCountry.computeIfAbsent(countryName, k -> FXCollections.observableArrayList()).add(cityName);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des villes depuis le fichier CSV : " + e.getMessage());
            showError("Erreur lors du chargement des villes. Vérifiez le fichier CSV.");
        } catch (CsvValidationException e) {
            System.err.println("Erreur de validation CSV : " + e.getMessage());
            showError("Erreur de validation CSV. Vérifiez le format du fichier CSV.");
        }
    }

    // Méthode pour charger les villes en fonction du pays sélectionné
    private void loadCitiesForCountry(String countryName) {
        ObservableList<String> cityList = citiesByCountry.getOrDefault(countryName, FXCollections.observableArrayList());
        cityComboBox.setItems(cityList);
    }

    @FXML
    private void onCountrySelected() {
        String selectedCountry = countryComboBox.getValue();
        if (selectedCountry != null) {
            loadCitiesForCountry(selectedCountry);
        }
    }

    @FXML
    private void handleSaveHotel() {
        if (!validateForm()) return;

        String selectedCountry = countryComboBox.getValue();
        String selectedCity = cityComboBox.getValue();
        Integer selectedCategory = categoryField.getValue();

        Hotel updatedHotel = new Hotel(
                hotelToEdit.getId_hotel_h(),
                hotelNameField.getText().trim(),
                addressField.getText().trim(),
                selectedCity,
                selectedCountry,
                selectedCategory,
                servicesField.getText().trim(),
                coordinatesField.getText().trim(),
                reviewsField.getText().trim()
        );

        hotelService.modifier(updatedHotel);
        showConfirmation("Hôtel mis à jour avec succès !");
        dashBoardController.navigateTo("dashboard/hotel/hotel-grid.fxml");
        closeForm();
    }

    // Validation du formulaire
    private boolean validateForm() {
        boolean isValid = true;

        // Validation du nom de l'hôtel
        if (hotelNameField.getText().trim().isEmpty() || hotelNameField.getText().trim().length() <= 2) {
            setFieldError(hotelNameField, "Le nom de l'hôtel est requis et doit contenir plus de 2 caractères.");
            isValid = false;
        } else {
            clearFieldError(hotelNameField);
        }

        // Validation de l'adresse
        if (addressField.getText().trim().isEmpty() || addressField.getText().trim().length() <= 2) {
            setFieldError(addressField, "L'adresse est requise et doit contenir plus de 2 caractères.");
            isValid = false;
        } else {
            clearFieldError(addressField);
        }

        // Validation des services
        if (servicesField.getText().trim().isEmpty() || servicesField.getText().trim().length() <= 2) {
            setFieldError(servicesField, "Les services sont requis et doivent contenir plus de 2 caractères.");
            isValid = false;
        } else {
            clearFieldError(servicesField);
        }

        // Validation des coordonnées
        if (coordinatesField.getText().trim().isEmpty() || coordinatesField.getText().trim().length() <= 2) {
            setFieldError(coordinatesField, "Les coordonnées sont requises et doivent contenir plus de 2 caractères.");
            isValid = false;
        } else {
            clearFieldError(coordinatesField);
        }

        // Validation des avis des clients
        if (reviewsField.getText().trim().isEmpty() || reviewsField.getText().trim().length() <= 2) {
            setFieldError(reviewsField, "Les avis des clients sont requis et doivent contenir plus de 2 caractères.");
            isValid = false;
        } else {
            clearFieldError(reviewsField);
        }

        // Validation du pays
        if (countryComboBox.getValue() == null) {
            setFieldError(countryComboBox, "Choisissez un pays.");
            isValid = false;
        } else {
            clearFieldError(countryComboBox);
        }

        // Validation de la ville
        if (cityComboBox.getValue() == null) {
            setFieldError(cityComboBox, "Choisissez une ville.");
            isValid = false;
        } else {
            clearFieldError(cityComboBox);
        }

        // Validation de la catégorie (1-7)
        if (categoryField.getValue() == null) {
            setFieldError(categoryField, "Choisissez une catégorie entre 1 et 7.");
            isValid = false;
        } else {
            clearFieldError(categoryField);
        }

        return isValid;
    }

    // Méthode pour afficher les erreurs de validation
    private void setFieldError(Control field, String message) {
        field.setStyle("-fx-border-color: red;");
        Tooltip tooltip = new Tooltip(message);
        Tooltip.install(field, tooltip);
    }

    // Méthode pour effacer les erreurs de validation
    private void clearFieldError(Control field) {
        field.setStyle("");
        Tooltip.uninstall(field, null);
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private void closeForm() {
        if (stage != null) {
            stage.close();
        }
    }

    private void showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void setDashBoardController(DashBoard dashBoardController) {
        this.dashBoardController = dashBoardController;
    }

    @FXML
    private void handleTranslate() {
        String textToTranslate = reviewsField.getText();
        String targetLanguage = languageComboBox.getValue();

        if (textToTranslate.isEmpty() || targetLanguage == null) {
            translatedReviewTextArea.setText("Veuillez entrer un avis et choisir une langue.");
            return;
        }

        String translatedText = TranslationService.translate(textToTranslate, "auto", targetLanguage);
        translatedReviewTextArea.setText(translatedText);
    }
}
