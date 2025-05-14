package com.globalTravel.controllers.activity;

import com.globalTravel.models.activity.Activity;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class AgendaView {
    private List<Activity> activities;

    public AgendaView(List<Activity> activities) {
        this.activities = activities;
    }

    public void start(Stage stage) {
        VBox root = new VBox(10);
        ListView<String> agendaList = new ListView<>();

        // Ajouter les activités à la ListView
        for (Activity activity : activities) {
            agendaList.getItems().add(activity.getNomActivity() + " - " + activity.getDateDebut());
        }

        root.getChildren().add(agendaList);

        // Configurer la scène et afficher la fenêtre
        javafx.scene.Scene scene = new javafx.scene.Scene(root, 400, 300);
        stage.setTitle("Agenda des activités");
        stage.setScene(scene);
        stage.show();
    }
}