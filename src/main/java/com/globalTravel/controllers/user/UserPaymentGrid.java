package com.globalTravel.controllers.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class UserPaymentGrid {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private static final String API_KEY = "AIzaSyD_WURCUPe_1j7JKTSajZzDeheiH2Yo35k";

    @FXML
    private TextField inputField; // Champ de texte pour l'entrée utilisateur

    @FXML
    private TextArea outputArea; // Zone de texte pour afficher la réponse

    @FXML
    private Button paymentButton;

    /**
     * Envoie une requête à l'API Gemini.
     */
    @FXML
    private void handleSendRequest() {
        String input = inputField.getText();
        if (input.isEmpty()) {
            outputArea.setText("Please enter a query.");
            return;
        }

        try {
            String jsonInput = String.format("{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}", input);
            HttpPost httpPost = new HttpPost(API_URL + "?key=" + API_KEY);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(jsonInput));

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpResponse response = httpClient.execute(httpPost);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new IOException("API Error: " + response.getStatusLine().getStatusCode());
                }
                String jsonResponse = EntityUtils.toString(response.getEntity());
                String generatedText = extractGeneratedText(jsonResponse);
                outputArea.setText(generatedText);
            }
        } catch (IOException e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Extrait le texte généré à partir de la réponse JSON.
     */
    private String extractGeneratedText(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        return jsonObject.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
    }

    /**
     * Efface les champs de texte.
     */
    @FXML
    private void handleClear() {
        inputField.clear();
        outputArea.clear();
    }


}
