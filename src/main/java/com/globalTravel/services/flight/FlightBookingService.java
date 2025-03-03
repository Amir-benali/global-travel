package com.globalTravel.services.flight;

        import com.globalTravel.models.flight.Ticket;
        import com.globalTravel.services.flight.TicketService;

        import java.util.ArrayList;
        import java.util.List;

public class FlightBookingService {

            private final TicketService ticketService = new TicketService();

            public boolean bookFlight(Ticket ticket) {
                try {
                    ticketService.ajouter(ticket);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            public List<String> getBookedSeats(int flightId) {
                List<String> bookedSeats = new ArrayList<>();
                List<Ticket> tickets = ticketService.getTicketsByFlightId(flightId);
                for (Ticket ticket : tickets) {
                    bookedSeats.add(ticket.getSeat_number());
                }

                return bookedSeats;
            }
        }