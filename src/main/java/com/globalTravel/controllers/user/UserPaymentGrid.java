package com.globalTravel.controllers.user;

import com.globalTravel.controllers.frontoffice.FrontNavigatable;
import com.globalTravel.controllers.frontoffice.FrontOffice;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class UserPaymentGrid implements FrontNavigatable {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private static final String API_KEY = "AIzaSyD_WURCUPe_1j7JKTSajZzDeheiH2Yo35k";

    @FXML
    private TextField inputField; // Input field for user messages

    @FXML
    private VBox chatArea; // VBox to hold chat bubbles

    @FXML
    private ScrollPane chatScrollPane; // ScrollPane to make the chat area scrollable

    @FXML
    private Button paymentButton;
    private FrontOffice frontOfficeController;
    private String userImage;

    /**
     * Sends a request to the Gemini API and displays the response in the chat area.
     */
    @FXML
    private void handleSendRequest() {
        String input = inputField.getText();
        if (input.isEmpty()) {
            addBotMessage("Please enter a query.", chatArea);
            return;
        }

        // Add user message to the chat area
        addUserMessage(input, chatArea);

        // Clear the input field
        inputField.clear();

        // Send the request to the API
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

                // Add bot message to the chat area
                addBotMessage(generatedText, chatArea);
            }
        } catch (IOException e) {
            addBotMessage("Error: " + e.getMessage(), chatArea);
        }
    }

    /**
     * Extracts the generated text from the JSON response.
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
     * Clears the input field and chat area.
     */
    @FXML
    private void handleClear() {
        inputField.clear();
        chatArea.getChildren().clear();
    }

    /**
     * Adds a user message to the chat area (aligned to the far right).
     */
    private void addUserMessage(String message, VBox chatArea) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_RIGHT); // Align to the far right
        messageContainer.setPadding(new Insets(5, 10, 5, 10));
        messageContainer.setMaxWidth(Double.MAX_VALUE); // Allow the container to take up the full width
        messageContainer.setSpacing(10);

        // User image
        ImageView userImage = new ImageView((this.userImage != null) ? new Image(this.userImage) : new Image(getClass().getResourceAsStream("/images/user-icon.png")));
        userImage.setFitWidth(40); // Set image width
        userImage.setFitHeight(40); // Set image height
        userImage.setSmooth(true); // Enable smooth resizing
        userImage.setPreserveRatio(false);
        userImage.setClip(new Circle(userImage.getFitWidth() / 2, userImage.getFitHeight() / 2, Math.min(userImage.getFitWidth(), userImage.getFitHeight()) / 2));

        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 10px; -fx-padding: 10px;");
        textFlow.setMaxWidth(Double.MAX_VALUE); // Allow the message bubble to take up the full width

        // Parse the message for formatting
        parseFormattedText(message, textFlow, javafx.scene.paint.Color.WHITE);

        messageContainer.getChildren().addAll(textFlow, userImage); // Add message bubble and user image
        chatArea.getChildren().add(messageContainer);

        // Scroll to the bottom of the chat area
        chatScrollPane.setVvalue(1.0);
    }

    /**
     * Adds a bot message to the chat area (aligned to the far left).
     */
    private void addBotMessage(String message, VBox chatArea) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_LEFT); // Align to the far left
        messageContainer.setPadding(new Insets(5, 10, 5, 10));
        messageContainer.setMaxWidth(Double.MAX_VALUE); // Allow the container to take up the full width

        // Bot image
        ImageView botImage = new ImageView(new Image(getClass().getResourceAsStream("/images/chatbot-icon.png")));
        botImage.setFitWidth(40); // Set image width
        botImage.setFitHeight(40); // Set image height
        botImage.setPreserveRatio(true); // Maintain aspect ratio

        // Message bubble
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-background-color: #f1f1f1; -fx-background-radius: 10px; -fx-padding: 10px;");
        textFlow.setMaxWidth(Double.MAX_VALUE); // Allow the message bubble to take up the full width

        // Parse the message for formatting
        parseFormattedText(message, textFlow, javafx.scene.paint.Color.BLACK);

        messageContainer.getChildren().addAll(botImage, textFlow); // Add bot image and message bubble
        chatArea.getChildren().add(messageContainer);

        // Scroll to the bottom of the chat area
        chatScrollPane.setVvalue(1.0);
    }

    /**
     * Parses the message for basic formatting (bold, italic) and adds styled text to the TextFlow.
     */
    private void parseFormattedText(String message, TextFlow textFlow, javafx.scene.paint.Color textColor) {
        String[] parts = message.split("\\*\\*|\\*"); // Split by ** or *
        boolean isBold = false;
        boolean isItalic = false;

        for (String part : parts) {
            Text text = new Text(part);
            text.setFill(textColor);
            text.setFont(Font.font("Arial", isBold ? FontWeight.BOLD : FontWeight.NORMAL, isItalic ? FontPosture.ITALIC : FontPosture.REGULAR, 14));
            textFlow.getChildren().add(text);

            // Toggle bold/italic state
            if (part.isEmpty()) {
                if (message.contains("**" + part + "**")) {
                    isBold = !isBold;
                } else if (message.contains("*" + part + "*")) {
                    isItalic = !isItalic;
                }
            }
        }
    }

    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;
        userImage = frontOfficeController.getCurrentUser().getImage();
    }
}