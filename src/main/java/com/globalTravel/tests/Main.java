package com.globalTravel.tests;


import com.globalTravel.models.Activity;
import com.globalTravel.models.Review;
import com.globalTravel.models.TypeActivity;
import com.globalTravel.services.ActivityService;
import com.globalTravel.services.ReviewService;
import com.globalTravel.services.TypeActivityService;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {



        //type activity
         //TypeActivityService typeActivityService = new TypeActivityService();
        //ajoute
           //TypeActivity newTypeActivity = new TypeActivity("Aventure", "hotel");
          //typeActivityService.ajouter(newTypeActivity);
        // Modification
        // TypeActivity updatedTypeActivity = new TypeActivity(1, "Aventure Extrême", "voiture");
       // typeActivityService.modifier(updatedTypeActivity);
        //Suppression
       //  TypeActivity typeActivityToDelete = new TypeActivity(1, "", "");
        //typeActivityService.supprimer(typeActivityToDelete);



   ///review
       // ReviewService reviewService = new ReviewService();
        //ajoute
       // Review newReview = new Review(2,"Super expérience !", 3, LocalDateTime.now(), "Refusée");
       //reviewService.ajouter(newReview);
        //modif
        //Review updatedReview = new Review(5, "Expérience incroyable ", 4, LocalDateTime.now(), "Acceptée");
        //reviewService.modifier(updatedReview);
        // Suppression
    // Review reviewToDelete = new Review(5,"", 1,null, "");
    //   reviewService.supprimer(reviewToDelete);










        // activity
       // ActivityService activityService = new ActivityService();
        //ajoute
        // Calendar cal = Calendar.getInstance();
        // Date dateDebut = cal.getTime();
         //cal.set(2026, Calendar.FEBRUARY, 2, 22, 2, 2);
         //Date dateFin = cal.getTime();
         //Activity newActivity = new Activity(dateDebut, dateFin, "Excursion en montagne", "Tunis, Tunisie",
           //     true, 250, true, false, true);
       // activityService.ajouter(newActivity);
        //  Modification
      //  Calendar calDebut = Calendar.getInstance();
      //  calDebut.set(2021, Calendar.FEBRUARY, 1, 11, 1, 1);
      //  Date dateDebut = calDebut.getTime();
        // Calendar calFin = Calendar.getInstance();
        // calFin.set(2022, Calendar.FEBRUARY, 2, 22, 20, 20);
      //  Date dateFin = calFin.getTime();
       // Activity updatedActivity = new Activity(9, dateDebut, dateFin, "Plongée ", "Hammamet, ",
         //       false, 600, false, true, true);
       // activityService.modifier(updatedActivity);
        // Suppression
        //  Activity activityToDelete = new Activity(9, null, null, null, null, false, 0, false, false, false);
        // activityService.supprimer(activityToDelete);
        // System.out.println(activityService.rechercher());


    }
}
