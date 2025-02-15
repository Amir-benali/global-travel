package com.globalTravel.tests;

import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.Review;
import com.globalTravel.models.activity.TypeActivity;
import com.globalTravel.models.car.CarDriver;
import com.globalTravel.models.car.Offer;
import com.globalTravel.models.car.PrivateCar;
import com.globalTravel.models.car.Route;
import com.globalTravel.models.flight.*;

import com.globalTravel.models.hotel.Chambre;
import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.models.hotel.Reservation_hotel;
import com.globalTravel.models.user.Admin;
import com.globalTravel.services.activity.ActivityService;
import com.globalTravel.services.activity.ReviewService;
import com.globalTravel.services.activity.TypeActivityService;
import com.globalTravel.services.car.CarDriverService;
import com.globalTravel.services.car.OfferService;
import com.globalTravel.services.car.PrivateCarService;
import com.globalTravel.services.car.RouteService;
import com.globalTravel.services.flight.AirlineService;
import com.globalTravel.services.flight.FlightService;
import com.globalTravel.services.flight.TicketService;


import com.globalTravel.services.hotel.ChambreService;
import com.globalTravel.services.hotel.HotelService;
import com.globalTravel.services.hotel.Reservation_hotelService;
import com.globalTravel.services.user.AdminService;
import com.globalTravel.utils.DataSource;


import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        //connection test
        DataSource ds= DataSource.getInstance();

// user module

        //         Créer un objet Admin
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
        }

//Modifier un Admin
        if (!admins.isEmpty()) {
            Admin adminToModify = admins.get(0); // Modifier le premier Admin trouvé
            adminToModify.setFirstName("Jonathan2");
            adminService.modifier(adminToModify);
            System.out.println("Admin modifié !");
        }

//Supprimer un Admin
        if (!admins.isEmpty()) {
            Admin adminToDelete = admins.get(0); // Supprimer le premier Admin trouvé
            adminService.supprimer(adminToDelete);
            System.out.println("Admin supprimé !");
        }


// flight module

        AirlineService as=new AirlineService();
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

     //SUPPRESSION
      /*
        fs.supprimer(new Flight(7, "",1, "", "", "", "", 0, 0, 0.0, FlightStatus.Scheduled));
        as.supprimer(new Airline(5, "", "", ""));
        ts.supprimer(new Ticket(5, 6, "", TicketClass.Business, 0.0,TicketStatus.Not_Booked, ""));
      */

        //car module

        OfferService offerService = new OfferService();
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


// hotel module


     try {
      // Création d'une instance de HotelService
      HotelService hotelService = new HotelService();

      // Création d'un nouvel hôtel (utilise le constructeur sans ID)
      Hotel hotel = new Hotel("Hôtel Paris", "123 Rue de la Paix", "Paris", "France", 5,
              "Wi-Fi, Petit-déjeuner, Salle de réunion", "0123456789, contact@hotelparis.com",
              "Excellent séjour, personnel très accueillant.");

      // Ajout de l'hôtel à la base de données
      hotelService.ajouter(hotel);
      System.out.println("Liste des hôtels après ajout :");
      hotelService.rechercher().forEach(System.out::println);

      // Modification de l'hôtel avec ID 54 (vérifiez que cet ID existe dans votre BD)
      Hotel hotelModifie = new Hotel(59, "Hôtel de Paris", "234 Rue de la Paix", "tunis", "tunisie", 4,
              "picine, Petit-déjeuner, Salle de réunion", "+21623456789, contact@hotelparis.com", "le meilleur, personnel très accueillant.");
      hotelService.modifier(hotelModifie);

      System.out.println("Liste des hôtels après modification :");
      hotelService.rechercher().forEach(System.out::println);

      // Suppression de l'hôtel avec ID 54
      hotelService.supprimer(new Hotel(59, "", "", "", "", 0, "", "", ""));
      System.out.println("Hôtel supprimé avec succès.");

      System.out.println("Liste des hôtels après suppression :");
      hotelService.rechercher().forEach(System.out::println);

      // ====================================================
      // Création d'une instance de ChambreService
      ChambreService chambreService = new ChambreService();

      // Création d'une nouvelle chambre avec un hôtel déjà existant
      Hotel hotelExistant = new Hotel(28, "Hôtel Paris", "123 Rue de la Paix", "Paris", "France", 5,
              "Wi-Fi, Petit-déjeuner, Salle de réunion", "0123456789, contact@hotelparis.com",
              "Excellent séjour, personnel très accueillant.");

      Chambre chambre = new Chambre("Suite", 150, LocalDate.of(2025, 2, 5),
              "Massage, piscine, food", hotelExistant);

      // Ajout de la chambre à la base de données
      chambreService.ajouter(chambre);
      System.out.println("Liste des chambres après ajout :");
      chambreService.rechercher().forEach(System.out::println);

      // Modification de la chambre
      Chambre chambreModifiee = new Chambre(38, "Triple", 1440, LocalDate.of(2027, 3, 10),
              "Piscine, food", hotelExistant);
      chambreService.modifier(chambreModifiee);
      System.out.println("Liste des chambres après modification :");
      chambreService.rechercher().forEach(System.out::println);

      // Suppression de la chambre
      chambreService.supprimer(new Chambre(38, "", 0, null, "", hotelExistant));

      System.out.println("Liste des chambres après suppression :");
      chambreService.rechercher().forEach(System.out::println);

      // ====================================================
      // Création d'une instance de Reservation_hotelService
      Reservation_hotelService reservationService = new Reservation_hotelService();

      // Création d'une nouvelle réservation avec une chambre existante
      Chambre chambreReservee = new Chambre(25, "Suite", 150, LocalDate.of(2025, 2, 5),
              "Massage, piscine, food", hotelExistant);

      Reservation_hotel reservation = new Reservation_hotel(
              LocalDate.of(2025, 2, 5),
              LocalDate.of(2025, 2, 10),
              2, "Confirmée", "Carte de crédit", chambreReservee);

      // Ajout de la réservation à la base de données
      reservationService.ajouter(reservation);
      System.out.println("Liste des réservations après ajout :");
      reservationService.rechercher().forEach(System.out::println);

      // Modification de la réservation
      Reservation_hotel reservationModifiee = new Reservation_hotel(
              7, LocalDate.of(2025, 2, 6),
              LocalDate.of(2025, 2, 12),
              3, "Annulée", "PayPal", chambreReservee);
      reservationService.modifier(reservationModifiee);
      System.out.println("Liste des réservations après modification :");
      reservationService.rechercher().forEach(System.out::println);

      // Suppression de la réservation
      reservationService.supprimer(new Reservation_hotel(7, null, null, 0, null, null, null));

      System.out.println("Liste des réservations après suppression :");
      reservationService.rechercher().forEach(System.out::println);

     } catch (Exception e) {
      System.err.println("Erreur lors de l'exécution du programme : " + e.getMessage());
      e.printStackTrace();
     }
     // activity module

        //type activity
        TypeActivityService typeActivityService = new TypeActivityService();
        //ajoute
        TypeActivity newTypeActivity = new TypeActivity("Aventure", "hotel");
        typeActivityService.ajouter(newTypeActivity);
        // Modification
         TypeActivity updatedTypeActivity = new TypeActivity(1, "Aventure Extrême", "voiture");
         typeActivityService.modifier(updatedTypeActivity);
        //Suppression
          TypeActivity typeActivityToDelete = new TypeActivity(1, "", "");
          typeActivityService.supprimer(typeActivityToDelete);



        ///review
         ReviewService reviewService = new ReviewService();
        //ajoute
         Review newReview = new Review(2,"Super expérience !", 3, LocalDateTime.now(), "Refusée");
        reviewService.ajouter(newReview);
        //modif
        Review updatedReview = new Review(5, "Expérience incroyable ", 4, LocalDateTime.now(), "Acceptée");
        reviewService.modifier(updatedReview);
        // Suppression
         Review reviewToDelete = new Review(5,"", 1,null, "");
           reviewService.supprimer(reviewToDelete);



        // activity
         ActivityService activityService = new ActivityService();
        //ajoute
         Calendar cal = Calendar.getInstance();
         Date dateDebut = cal.getTime();
        cal.set(2026, Calendar.FEBRUARY, 2, 22, 2, 2);
        Date dateFin = cal.getTime();
        Activity newActivity = new Activity(dateDebut, dateFin, "Excursion en montagne", "Tunis, Tunisie",
             true, 250, true, false, true);
         activityService.ajouter(newActivity);
          //Modification
          Calendar calDebut = Calendar.getInstance();
          calDebut.set(2021, Calendar.FEBRUARY, 1, 11, 1, 1);
        dateDebut = calDebut.getTime();
         Calendar calFin = Calendar.getInstance();
         calFin.set(2022, Calendar.FEBRUARY, 2, 22, 20, 20);
        dateFin = calFin.getTime();
         Activity updatedActivity = new Activity(9, dateDebut, dateFin, "Plongée ", "Hammamet, ",
               false, 600, false, true, true);
         activityService.modifier(updatedActivity);
        // Suppression
          Activity activityToDelete = new Activity(9, null, null, null, null, false, 0, false, false, false);
          activityService.supprimer(activityToDelete);
         System.out.println(activityService.rechercher());

    }

}