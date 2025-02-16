package com.globalTravel.tests;

import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.Review;
import com.globalTravel.models.activity.TypeActivity;
import com.globalTravel.services.activity.ActivityService;
import com.globalTravel.services.activity.ReviewService;
import com.globalTravel.utils.DataSource;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        //connection test
        DataSource ds= DataSource.getInstance();

// user module

      /*  //         Créer un objet Admin
        Admin admin = new Admin(
                0, // L'id sera auto-généré par la base de données
                "Homme",
                new Date(1990, 5, 10),
                "Tunis, Tunisie",
                "admin@example.com",
                "Admin2",
                "password123",
                "John",
                "Doe",
                "123456789",
                "image.jpg",
                "Actif",
                "Super Admin"
        );

        AdminService adminService = new AdminService();

//Ajouter un Admin
        adminService.ajouter(admin);

//Récupérer tous les Admins et les afficher
      List<Admin> admins = adminService.rechercher();
       System.out.println("Liste des Admins :");
        for (Admin a : admins) {
            System.out.println(a.getFirstName() + " " + a.getLastName());
        }*/

//Modifier un Admin
     /*   if (!admins.isEmpty()) {
            Admin adminToModify = admins.get(0); // Modifier le premier Admin trouvé
            adminToModify.setFirstName("Jonathan2");
            adminService.modifier(adminToModify);
            System.out.println("Admin modifié !");
        } */

//Supprimer un Admin
    /*    if (!admins.isEmpty()) {
            Admin adminToDelete = admins.get(0); // Supprimer le premier Admin trouvé
            adminService.supprimer(adminToDelete);
            System.out.println("Admin supprimé !");
        }  */


// flight module

     /*   AirlineService as=new AirlineService();
        //as.ajouter(new Airline("Tunisia", "TUN", "Tunisair"));
        as.ajouter(new Airline("France", "FR", "Airfrance"));

        FlightService fs=new FlightService();
        //fs.ajouter(new Flight("F1", 3, "TUN", "CDG", "2021-12-12 12:00:00", "2021-12-12 14:00:00", 2, 100, 200.0, FlightStatus.Scheduled));
        fs.ajouter(new Flight("F2", 5, "TUN", "ORY", "2021-12-12 12:00:00", "2021-12-12 14:00:00", 2, 100, 200.0, FlightStatus.Scheduled));

        TicketService ts=new TicketService();
        //ts.ajouter(new Ticket(6, "A1", TicketClass.Business, 100.0, TicketStatus.Not_Booked, "2021-12-12 12:00:00"));
        ts.ajouter(new Ticket(6, "A2", TicketClass.Economy, 100.0, TicketStatus.Not_Booked, "2021-12-12 12:00:00"));

        as.modifier(new Airline(5, "Tunisair", "TUN", "Tunisia"));
        fs.modifier(new Flight(7, "F2222", 5, "TUN", "CDG", "2021-12-12 12:00:00", "2021-12-12 14:00:00", 2, 100, 200.0, FlightStatus.Scheduled));
        ts.modifier(new Ticket(5, 6, "Abcfre251", TicketClass.Business, 350.0,TicketStatus.Not_Booked, "2021-12-12 12:00:00"));

        System.out.println(as.rechercher());
        System.out.println(fs.rechercher());
        System.out.println(ts.rechercher());

        //fs.supprimer(new Flight(3, "",1, "", "", "", "", 0, 0, 0.0, FlightStatus.Scheduled));
        //as.supprimer(new Airline(1, "", "", ""));
        //ts.supprimer(new Ticket(1, 3, "Abc251", TicketClass.Business, 100.0,TicketStatus.Not_Booked, "2021-12-12 12:00:00"));
   /*

      */
     //SUPPRESSION
      /*
        fs.supprimer(new Flight(7, "",1, "", "", "", "", 0, 0, 0.0, FlightStatus.Scheduled));
        as.supprimer(new Airline(5, "", "", ""));
        ts.supprimer(new Ticket(5, 6, "", TicketClass.Business, 0.0,TicketStatus.Not_Booked, ""));
      */

        //car module

       /*  OfferService offerService = new OfferService();
        PrivateCarService carService = new PrivateCarService();
        RouteService routeService = new RouteService();
        CarDriverService driverService = new CarDriverService();

        Route route = new Route(LocalDateTime.now(),LocalDateTime.now(),"11","11");
        routeService.ajouter(route);
        routeService.modifier(new Route(2,LocalDateTime.now(),LocalDateTime.now(),"12","12"));
        routeService.supprimer(new Route(2,null,null,null,null));
        System.out.println(routeService.rechercher());



        CarDriver driver =new CarDriver("ahmed2","amin","99885544");
        driverService.ajouter(driver);
        driverService.modifier(new CarDriver(4,"ahmed","ahmed","99885577"));
        driverService.supprimer(new CarDriver(4,"","",""));
        System.out.println(driverService.rechercher());


        PrivateCar car = new PrivateCar("brand 2","model 2",5,new CarDriver(3,"ahmed","ahmed","99885577"));
        carService.ajouter(car);
        carService.modifier(new PrivateCar(2,"brand 10","model 10",5,new CarDriver(2,"ahmed","ahmed","99885577")));
        carService.supprimer(new PrivateCar(2,"brand 10","model 10",5,new CarDriver(2,"ahmed","ahmed","99885577")));
        System.out.println(carService.rechercher());


        Offer offer = new Offer("offer desc",LocalDateTime.now(),20.5f,new Route(1,LocalDateTime.now(),LocalDateTime.now(),"11","11"),new PrivateCar(1,"","",4,new CarDriver(1,"","","")));

        offerService.ajouter(offer);
        offerService.modifier(new Offer(4,"offer desc 4 ", LocalDateTime.now(),15.5f,new Route(1,LocalDateTime.now(),LocalDateTime.now(),"14","14"),new PrivateCar(3,"","",4,new CarDriver(1,"","",""))));
        offerService.supprimer(new Offer(4,"offer desc 2 ", LocalDateTime.now(),15.5f,new Route(1,LocalDateTime.now(),LocalDateTime.now(),"11","11"),new PrivateCar(3,"","",4,new CarDriver(1,"","",""))));

        System.out.println(offerService.rechercher());

       */



// hotel module

//Création d'une instance de hotelService
       /* hotelService service = new hotelService();

//Création d'un nouvel hôtel
        hotel h1 = new hotel("Hôtel Paris", "123 Rue de la Paix", "Paris", "France", 5, "Wi-Fi, Petit-déjeuner, Salle de réunion", "0123456789, contact@hotelparis.com", "Excellent séjour, personnel très accueillant.");

//Ajout de l'hôtel
        service.ajouter(h1);
        service.rechercher().forEach(System.out::println);
//Modification de l'hôtel
        service.modifier(new hotel(28, "Hôtel de Paris", "234 Rue de la Paix", "tunis", "tunisie", 4, "picine, Petit-déjeuner, Salle de réunion", "+21623456789, contact@hotelparis.com", "le meilleur, personnel très accueillant."));
//Recherche de tous les hôtels après modification
        System.out.println("Liste des hôtels après modification :");
        service.rechercher().forEach(System.out::println);

// Suppression de l'hôtel
        service.supprimer(new hotel(28, "", "", "", "",0,"","",""));
        System.out.println("Hôtel supprimé avec succès.");

//Recherche de tous les hôtels après suppression
        System.out.println("Liste des hôtels après suppression :");
        service.rechercher().forEach(System.out::println);


        // ====================================================

        reservation_hotelService serviceReservation = new reservation_hotelService();

        // Création d'une nouvelle réservation
        reservation_hotel reservation1 = new reservation_hotel(
                LocalDate.of(2025, 2, 5), // Date de check-in : 5 février 2025
                LocalDate.of(2025, 2, 15), // Date de check-out : 15 février 2025
                2, // Nombre de chambres réservées
                "confirmée", // Statut de la réservation
                "Carte de crédit" // Méthode de paiement
        );   */

      /*  // Ajout de la réservation
        serviceReservation.ajouter(reservation1);
        serviceReservation.rechercher().forEach(System.out::println);

        // Modification de la réservation
        serviceReservation.modifier(new reservation_hotel(5, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 10), 3, "annuler", "PayPal"));

        // Recherche de toutes les réservations après modification
        serviceReservation.rechercher().forEach(System.out::println);

        // Suppression de la réservation
        serviceReservation.supprimer(new reservation_hotel(5, null, null, 0, "", ""));

        // Recherche de toutes les réservations après suppression
        System.out.println("Liste des réservations après suppression :");
        serviceReservation.rechercher().forEach(System.out::println);


        // Création d'une instance de chambreService
        chambreService servicech = new chambreService();

        // Création d'une nouvelle chambre
        chambre chambre1 = new chambre("suite", 150, LocalDate.of(2025, 2, 5), "massage, piscine, food");

        // Ajout de la chambre
        servicech.ajouter(chambre1);
        System.out.println("Liste des chambres après ajout :");
        servicech.rechercher().forEach(System.out::println);

        // Modification de la chambre
        chambre chambreModifiee = new chambre(2, "double", 120, LocalDate.of(2027, 3, 10), "piscine, food");
        servicech.modifier(chambreModifiee);
        System.out.println("Liste des chambres après modification :");
        servicech.rechercher().forEach(System.out::println);

        // Suppression de la chambre
        servicech.supprimer(new chambre(2,"",0, null, ""));

        // Recherche de toutes les chambres après suppression
        System.out.println("Liste des chambres après suppression :");
        servicech.rechercher().forEach(System.out::println);   */



   ////////////////////////////////////////////review////////////////////////////////////////////////////////////////////

        ////////////////////ajoute////////////////////////////

      ReviewService reviewService = new ReviewService();
      Review newReview = new Review("Super expérience !", 3, 79);
        reviewService.ajouter(newReview);


        ///////////////////////modif/////////////////////////


       int reviewIdToUpdate =17 ;
        int activityId = 79;
        if (reviewService.reviewExists(reviewIdToUpdate)) {
            if (reviewService.activityExists(activityId)) {

                Review updatedReview = new Review(reviewIdToUpdate, "Expérience incroyable", 0, LocalDateTime.now(), activityId);
                reviewService.modifier(updatedReview);
            } else {
                System.out.println("Erreur  L'activité avec  ID " + activityId + " n'existe pas.");
            }
        } else {
            System.out.println("Erreur  Aucun avis trouvé avec ID " + reviewIdToUpdate);
        }

        /////////////////////////////// Suppression///////////////////////////////////


       int reviewIdToDelete = 17;
        if (reviewService.reviewExists(reviewIdToDelete)) {
            Review reviewToDelete = new Review(reviewIdToDelete, ".", 0, null, 79);
            reviewService.supprimer(reviewToDelete);
        } else {
            System.out.println("Erreur : Aucun avis trouvé avec l ID " + reviewIdToDelete);
        }


        ///////////////////////////////////////affichage/////////////////////////////////////////

       reviewService.rechercher().forEach(System.out::println);




        /////////////////////////////////////////////////////////////Activity///////////////////////////////////////////////

        ////////////////////////Ajoute///////////////////////////


        ActivityService activityService = new ActivityService();
        Calendar calendar = Calendar.getInstance();

        // 🔹 Ajout d'une nouvelle activité
        calendar.set(2025, Calendar.JANUARY, 1, 10, 1, 1);
        Timestamp dateDebut1 = new Timestamp(calendar.getTimeInMillis());

        calendar.set(2025, Calendar.FEBRUARY, 3, 22, 2, 2);
        Timestamp dateFin1 = new Timestamp(calendar.getTimeInMillis());

        Activity newActivity = new Activity(
                dateDebut1,
                dateFin1,
                "Excursion en montagne",
                "Tunis, Tunisie",
                250,  // Prix total en TND
                "Randonnée Tunisienne",
                TypeActivity.WORKSHOPS,
                28,  // ID de l'hôtel (joinHotelId)
                1,   // ID de la voiture (joinVoitureId)
                3    // ID du vol (joinVolsId)
        );

        activityService.ajouter(newActivity);
        System.out.println("✅ Activité ajoutée avec succès !");

        // 🔹 Modification d'une activité existante
        calendar.set(2024, Calendar.FEBRUARY, 1, 10, 1, 10);
        Timestamp dateDebut2 = new Timestamp(calendar.getTimeInMillis());

        calendar.set(2024, Calendar.FEBRUARY, 1, 18, 22, 10);
        Timestamp dateFin2 = new Timestamp(calendar.getTimeInMillis());

        Activity updatedActivity = new Activity(
                79, // ID existant
                dateDebut2,
                dateFin2,
                "Plongée sous-marine",
                "Hammamet, Tunisie",
                500,  // Prix total en TND
                "Plongée Hammamet",
                TypeActivity.TEAM_BUILDING_ACTIVITIES,
                28,  // ID de l'hôtel (joinHotelId)
                1,   // ID de la voiture (joinVoitureId)
                2    // ID du vol (joinVolsId)
        );

        activityService.modifier(updatedActivity);
        System.out.println("✅ Activité mise à jour avec succès !");

        ////////////////////////////supprimee//////////////////////


        Activity activityToDelete = new Activity(33,null, null, null, null, 0, null, null, 28,1,2);
        activityService.supprimer(activityToDelete);


        /////////////////////recherche//////////////////////////

        activityService.rechercher().forEach(activity -> System.out.println(activity));














    }

}