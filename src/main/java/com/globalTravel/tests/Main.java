package com.globalTravel.tests;

        import com.globalTravel.models.activity.Activity;
        import com.globalTravel.models.activity.Review;
        import com.globalTravel.models.activity.TypeActivity;
        import com.globalTravel.models.car.CarDriver;
        import com.globalTravel.models.car.Offer;
        import com.globalTravel.models.car.PrivateCar;
        import com.globalTravel.models.car.Route;
        import com.globalTravel.models.flight.*;
        import com.globalTravel.models.hotel.chambre;
        import com.globalTravel.models.hotel.hotel;
        import com.globalTravel.models.hotel.reservation_hotel;
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
        import com.globalTravel.services.hotel.chambreService;
        import com.globalTravel.services.hotel.hotelService;
        import com.globalTravel.services.hotel.reservation_hotelService;
        import com.globalTravel.services.user.AdminService;
        import com.globalTravel.utils.DataSource;

        import java.sql.Timestamp;
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
                adminService.ajouter(admin);
                List<Admin> admins = adminService.rechercher();
                System.out.println("Liste des Admins :");
                for (Admin a : admins) {
                    System.out.println(a.getFirstName() + " " + a.getLastName());
                }

                if (!admins.isEmpty()) {
                    Admin adminToModify = admins.get(0);
                    adminToModify.setFirstName("Jonathan2");
                    adminService.modifier(adminToModify);
                    System.out.println("Admin modifié !");
                }

                if (!admins.isEmpty()) {
                    Admin adminToDelete = admins.get(0);
                    adminService.supprimer(adminToDelete);
                    System.out.println("Admin supprimé !");
                }

                // flight module
                AirlineService as = new AirlineService();
                as.ajouter(new Airline("France", "FR", "Airfrance"));

                FlightService fs = new FlightService();
                fs.ajouter(new Flight("F2", 5, "TUN", "ORY",
                        Timestamp.valueOf("2021-12-12 12:00:00"),
                        Timestamp.valueOf("2021-12-12 14:00:00"),
                        2, 100, 200.0, FlightStatus.Scheduled));

                TicketService ts = new TicketService();
                ts.ajouter(new Ticket(6, "A2", TicketClass.Economy, 100.0, TicketStatus.Not_Booked,
                        Timestamp.valueOf("2021-12-12 12:00:00")));

                as.modifier(new Airline(5, "Tunisair", "TUN", "Tunisia"));
                fs.modifier(new Flight(7, "F2222", 5, "TUN", "CDG",
                        Timestamp.valueOf("2021-12-12 12:00:00"),
                        Timestamp.valueOf("2021-12-12 14:00:00"),
                        2, 100, 200.0, FlightStatus.Scheduled));
                ts.modifier(new Ticket(5, 6, "Abcfre251", TicketClass.Business, 350.0,
                        TicketStatus.Not_Booked, Timestamp.valueOf("2021-12-12 12:00:00")));

                System.out.println(as.rechercher());
                System.out.println(fs.rechercher());
                System.out.println(ts.rechercher());

                // car module
                OfferService offerService = new OfferService();
                PrivateCarService carService = new PrivateCarService();
                RouteService routeService = new RouteService();
                CarDriverService driverService = new CarDriverService();

                Route route = new Route(LocalDateTime.now(), LocalDateTime.now(), "11", "11");
                routeService.ajouter(route);
                routeService.modifier(new Route(2, LocalDateTime.now(), LocalDateTime.now(), "12", "12"));
                routeService.supprimer(new Route(2, null, null, null, null));
                System.out.println(routeService.rechercher());

                CarDriver driver = new CarDriver("ahmed2", "amin", "99885544");
                driverService.ajouter(driver);
                driverService.modifier(new CarDriver(4, "ahmed", "ahmed", "99885577"));
                driverService.supprimer(new CarDriver(4, "", "", ""));
                System.out.println(driverService.rechercher());

                PrivateCar car = new PrivateCar("brand 2", "model 2", 5, new CarDriver(3, "ahmed", "ahmed", "99885577"));
                carService.ajouter(car);
                carService.modifier(new PrivateCar(2, "brand 10", "model 10", 5, new CarDriver(2, "ahmed", "ahmed", "99885577")));
                carService.supprimer(new PrivateCar(2, "brand 10", "model 10", 5, new CarDriver(2, "ahmed", "ahmed", "99885577")));
                System.out.println(carService.rechercher());

                Offer offer = new Offer("offer desc", LocalDateTime.now(), 20.5f, new Route(1, LocalDateTime.now(), LocalDateTime.now(), "11", "11"), new PrivateCar(1, "", "", 4, new CarDriver(1, "", "", "")));
                offerService.ajouter(offer);
                offerService.modifier(new Offer(4, "offer desc 4 ", LocalDateTime.now(), 15.5f, new Route(1, LocalDateTime.now(), LocalDateTime.now(), "14", "14"), new PrivateCar(3, "", "", 4, new CarDriver(1, "", "", ""))));
                offerService.supprimer(new Offer(4, "offer desc 2 ", LocalDateTime.now(), 15.5f, new Route(1, LocalDateTime.now(), LocalDateTime.now(), "11", "11"), new PrivateCar(3, "", "", 4, new CarDriver(1, "", "", ""))));
                System.out.println(offerService.rechercher());

                // hotel module
                hotelService service = new hotelService();
                hotel h1 = new hotel("Hôtel Paris", "123 Rue de la Paix", "Paris", "France", 5, "Wi-Fi, Petit-déjeuner, Salle de réunion", "0123456789, contact@hotelparis.com", "Excellent séjour, personnel très accueillant.");
                service.ajouter(h1);
                service.rechercher().forEach(System.out::println);
                service.modifier(new hotel(28, "Hôtel de Paris", "234 Rue de la Paix", "tunis", "tunisie", 4, "picine, Petit-déjeuner, Salle de réunion", "+21623456789, contact@hotelparis.com", "le meilleur, personnel très accueillant."));
                System.out.println("Liste des hôtels après modification :");
                service.rechercher().forEach(System.out::println);
                service.supprimer(new hotel(28, "", "", "", "", 0, "", "", ""));
                System.out.println("Hôtel supprimé avec succès.");
                System.out.println("Liste des hôtels après suppression :");
                service.rechercher().forEach(System.out::println);

                reservation_hotelService serviceReservation = new reservation_hotelService();
                reservation_hotel reservation1 = new reservation_hotel(
                        LocalDate.of(2025, 2, 5),
                        LocalDate.of(2025, 2, 15),
                        2,
                        "confirmée",
                        "Carte de crédit"
                );
                serviceReservation.ajouter(reservation1);
                serviceReservation.rechercher().forEach(System.out::println);
                serviceReservation.modifier(new reservation_hotel(5, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 10), 3, "annuler", "PayPal"));
                serviceReservation.rechercher().forEach(System.out::println);
                serviceReservation.supprimer(new reservation_hotel(5, null, null, 0, "", ""));
                System.out.println("Liste des réservations après suppression :");
                serviceReservation.rechercher().forEach(System.out::println);

                chambreService servicech = new chambreService();
                chambre chambre1 = new chambre("suite", 150, LocalDate.of(2025, 2, 5), "massage, piscine, food");
                servicech.ajouter(chambre1);
                System.out.println("Liste des chambres après ajout :");
                servicech.rechercher().forEach(System.out::println);
                chambre chambreModifiee = new chambre(2, "double", 120, LocalDate.of(2027, 3, 10), "piscine, food");
                servicech.modifier(chambreModifiee);
                System.out.println("Liste des chambres après modification :");
                servicech.rechercher().forEach(System.out::println);
                servicech.supprimer(new chambre(2, "", 0, null, ""));
                System.out.println("Liste des chambres après suppression :");
                servicech.rechercher().forEach(System.out::println);

                // activity module
                TypeActivityService typeActivityService = new TypeActivityService();
                TypeActivity newTypeActivity = new TypeActivity("Aventure", "hotel");
                typeActivityService.ajouter(newTypeActivity);
                TypeActivity updatedTypeActivity = new TypeActivity(1, "Aventure Extrême", "voiture");
                typeActivityService.modifier(updatedTypeActivity);
                TypeActivity typeActivityToDelete = new TypeActivity(1, "", "");
                typeActivityService.supprimer(typeActivityToDelete);

                ReviewService reviewService = new ReviewService();
                Review newReview = new Review(2, "Super expérience !", 3, LocalDateTime.now(), "Refusée");
                reviewService.ajouter(newReview);
                Review updatedReview = new Review(5, "Expérience incroyable ", 4, LocalDateTime.now(), "Acceptée");
                reviewService.modifier(updatedReview);
                Review reviewToDelete = new Review(5, "", 1, null, "");
                reviewService.supprimer(reviewToDelete);

                ActivityService activityService = new ActivityService();
                Calendar cal = Calendar.getInstance();
                Date dateDebut = cal.getTime();
                cal.set(2026, Calendar.FEBRUARY, 2, 22, 2, 2);
                Date dateFin = cal.getTime();
                Activity newActivity = new Activity(dateDebut, dateFin, "Excursion en montagne", "Tunis, Tunisie", true, 250, true, false, true);
                activityService.ajouter(newActivity);
                Calendar calDebut = Calendar.getInstance();
                calDebut.set(2021, Calendar.FEBRUARY, 1, 11, 1, 1);
                dateDebut = calDebut.getTime();
                Calendar calFin = Calendar.getInstance();
                calFin.set(2022, Calendar.FEBRUARY, 2, 22, 20, 20);
                dateFin = calFin.getTime();
                Activity updatedActivity = new Activity(9, dateDebut, dateFin, "Plongée ", "Hammamet, ", false, 600, false, true, true);
                activityService.modifier(updatedActivity);
                Activity activityToDelete = new Activity(9, null, null, null, null, false, 0, false, false, false);
                activityService.supprimer(activityToDelete);
                System.out.println(activityService.rechercher());
            }
        }

