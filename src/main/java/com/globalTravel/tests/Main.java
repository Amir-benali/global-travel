package com.globalTravel.tests;


import com.globalTravel.models.Activity;
import com.globalTravel.services.ActivityService;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {



        //type activity
        // TypeActivityService typeActivityService = new TypeActivityService();


        //ajoute
        //   TypeActivity newTypeActivity = new TypeActivity("Aventure", "hotel");
        // typeActivityService.ajouter(newTypeActivity);

        // Modification
        //TypeActivity updatedTypeActivity = new TypeActivity(1, "Aventure Extrême", "voiture");
        //typeActivityService.modifier(updatedTypeActivity);

        //Suppression
        // TypeActivity typeActivityToDelete = new TypeActivity(1, "", "");
        //typeActivityService.supprimer(typeActivityToDelete);



////review
       // ReviewService reviewService = new ReviewService();

        //ajoute
        //Review newReview = new Review(1,"Super expérience !", 3, LocalDateTime.now(), "Refusée");
        //reviewService.ajouter(newReview);

        //modif
        //Review updatedReview = new Review(1, "Expérience incroyable !", 4, LocalDateTime.now(), "Acceptée");
        //reviewService.modifier(updatedReview);


        // Suppression
        // Review reviewToDelete = new Review(1, "", 0, null, "");
        // reviewService.supprimer(reviewToDelete);















































        //Activity

        ActivityService activityService = new ActivityService();

        // ajoute
         Date dateDebut = new Date(2025 - 1900, Calendar.JANUARY, 1, 10, 1, 1);  // Date de début
         Date dateFin = new Date(2025 - 1900, Calendar.JANUARY, 3, 22, 2, 2);    // Date de fin

        Activity newActivity = new Activity(dateDebut, dateFin,"Excursion en montagne", "Tunis, Tunisie", true, 250, true, false, true);

        // Ajouter l'activité
         activityService.ajouter(newActivity);



        //   Modification d'une activité
        //   Activity updatedActivity = new Activity(1,new Date(2024 - 1900, Calendar.FEBRUARY, 1, 10, 1, 1),
        //          new Date(2024 - 1900, Calendar.FEBRUARY, 1, 10, 1, 1), "Plongée sous-marine",
        //      "Hammamet, Tunisie", false, 500, false, true, true);
        // activityService.modifier(updatedActivity);

        // Suppression d'une activité
        //  Activity activityToDelete = new Activity(1, null, null, null, null, false, 0, false, false, false);
        // activityService.supprimer(activityToDelete);

        // Affichage des activités
        // System.out.println(activityService.rechercher());


    }
}
