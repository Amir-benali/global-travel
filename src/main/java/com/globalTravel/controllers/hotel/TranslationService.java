package com.globalTravel.controllers.hotel;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TranslationService {

    // URL de l'API Lingva
    private static final String LINGVA_API_URL = "https://lingva.ml/api/v1";

    public static String translate(String text, String sourceLang, String targetLang) {
        if (text == null || text.trim().isEmpty()) {
            return "Texte vide, traduction annulée.";
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Encoder le texte pour l'URL
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());

            // Construire l'URL de la requête
            String url = String.format("%s/%s/%s/%s", LINGVA_API_URL, sourceLang, targetLang, encodedText);
            HttpGet httpGet = new HttpGet(url);

            // Exécution de la requête
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity());

            // Afficher la réponse brute pour le débogage
            System.out.println("Réponse de l'API : " + responseBody);

            // Extraction du texte traduit de la réponse JSON
            JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();

            // Vérifier si la réponse contient une erreur
            if (responseJson.has("error")) {
                String errorMessage = responseJson.get("error").getAsString();
                return "Erreur de traduction : " + errorMessage;
            }

            // Vérifier si le champ "translation" existe
            if (!responseJson.has("translation")) {
                return "Erreur : Réponse de l'API invalide. Champ 'translation' manquant.";
            }

            // Récupérer le texte traduit et remplacer les '+' par des espaces
            String translatedText = responseJson.get("translation").getAsString();
            translatedText = translatedText.replace("+", " "); // Replace '+' with spaces

            return translatedText;
        } catch (Exception e) {
            e.printStackTrace();
            return "Impossible de traduire. Vérifiez votre connexion Internet.";
        }
    }
}