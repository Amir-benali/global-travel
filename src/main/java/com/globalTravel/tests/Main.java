package com.globalTravel.tests;

import com.globalTravel.models.chambre;
import com.globalTravel.models.hotel;
import com.globalTravel.models.reservation_hotel;
import com.globalTravel.services.chambreService;
import com.globalTravel.services.hotelService;
import com.globalTravel.services.reservation_hotelService;
import com.globalTravel.utils.DataSource;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        // Création d'une instance de hotelService
        hotelService service = new hotelService();

        // Création d'un nouvel hôtel
        hotel h1 = new hotel("Hôtel Paris", "123 Rue de la Paix", "Paris", "France", 5, "Wi-Fi, Petit-déjeuner, Salle de réunion", "0123456789, contact@hotelparis.com", "Excellent séjour, personnel très accueillant.");

        // Ajout de l'hôtel à la base de données
        service.ajouter(h1);
        service.rechercher().forEach(System.out::println);
        // Modification de l'hôtel
        service.modifier(new hotel(26, "Hôtel de Paris", "234 Rue de la Paix", "tunis", "tunisie", 4, "picine, Petit-déjeuner, Salle de réunion", "+21623456789, contact@hotelparis.com", "le meilleur, personnel très accueillant."));
        // Recherche de tous les hôtels après modification
        System.out.println("Liste des hôtels après modification :");
        service.rechercher().forEach(System.out::println);

        // Suppression de l'hôtel
        service.supprimer(new hotel(26, "", "", "", "",0,"","",""));
        System.out.println("Hôtel supprimé avec succès.");

        // Recherche de tous les hôtels après suppression
        System.out.println("Liste des hôtels après suppression :");
        service.rechercher().forEach(System.out::println);

        /*
        // ====================================================

        // Création d'une instance de reservation_hotelService
        reservation_hotelService serviceReservation = new reservation_hotelService();

        // Création d'une nouvelle réservation
        reservation_hotel reservation1 = new reservation_hotel(
                LocalDate.of(2025, 2, 5), // Date de check-in : 5 février 2025
                LocalDate.of(2025, 2, 15), // Date de check-out : 15 février 2025
                2, // Nombre de chambres réservées
                "confirmée", // Statut de la réservation
                "Carte de crédit" // Méthode de paiement
        );

        // Ajout de la réservation à la base de données
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

        // Ajout de la chambre à la base de données
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
        servicech.rechercher().forEach(System.out::println);
        */

    }


}
