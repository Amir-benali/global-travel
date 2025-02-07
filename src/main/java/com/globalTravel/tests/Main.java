package globaltravel.modules.FlightModule.tests;

import globaltravel.modules.FlightModule.models.*;
import globaltravel.modules.FlightModule.services.AirlineService;
import globaltravel.modules.FlightModule.services.FlightService;
import globaltravel.modules.FlightModule.services.TicketService;

public class Main {
    public static void main(String[] args) {

        AirlineService as=new AirlineService();
        //.ajouter(new Airline("Tunisia", "TUN", "Tunisair"));

        FlightService fs=new FlightService();
        //fs.ajouter(new Flight("F1", 1, "TUN", "CDG", "2021-12-12 12:00:00", "2021-12-12 14:00:00", 2, 100, 200.0, FlightStatus.Scheduled));

        TicketService ts=new TicketService();
        //ts.ajouter(new Ticket(2, "A1", TicketClass.Business, 100.0,TicketStatus.Not_Booked, "2021-12-12 12:00:00"));

        //as.modifier(new Airline(1, "Tunisair", "TUN", "Tunisia"));
        //fs.modifier(new Flight(2, "F123", 1, "TUN", "CDG", "2021-12-12 12:00:00", "2021-12-12 14:00:00", 2, 100, 200.0, FlightStatus.Scheduled));
        //ts.modifier(new Ticket(1, 2, "Abc251", TicketClass.Business, 100.0,TicketStatus.Not_Booked, "2021-12-12 12:00:00"));

        //System.out.println(as.rechercher());
        //System.out.println(fs.rechercher());
        //System.out.println(ts.rechercher());

        //fs.supprimer(new Flight(2, "",1, "", "", "", "", 0, 0, 0.0, FlightStatus.Scheduled));
        //as.supprimer(new Airline(1, "", "", ""));
    }
}
