package com.globalTravel.services.flight;

                       import com.globalTravel.models.flight.Ticket;
                       import com.globalTravel.models.flight.TicketClass;
                       import com.globalTravel.models.flight.TicketStatus;
                       import com.globalTravel.utils.DataSource;

                       import java.sql.Connection;
                       import java.sql.PreparedStatement;
                       import java.sql.ResultSet;
                       import java.sql.SQLException;
                       import java.util.ArrayList;
                       import java.util.List;

public class TicketService {

                           private Connection connection = DataSource.getInstance().getConnection();

                           public void ajouter(Ticket ticket) {
                               String req = "INSERT INTO tickets (id_flight, passenger_id,passenger_email, seat_number, ticket_class, ticket_price, ticket_status, ticket_booking_date) VALUES (?, ?,?, ?, ?, ?, ?, ?)";
                               try {
                                   PreparedStatement pst = connection.prepareStatement(req);
                                   pst.setInt(1, ticket.getFlight_id());
                                   pst.setInt(2, ticket.getPassenger_id());
                                   pst.setString(3, ticket.getPassenger_email());
                                   pst.setString(4, ticket.getSeat_number());
                                   pst.setString(5, ticket.getTicketClass().name());
                                   pst.setDouble(6, ticket.getTicket_price());
                                   pst.setString(7, ticket.getStatus().name());
                                   pst.setTimestamp(8, ticket.getBooking_date());
                                   pst.executeUpdate();
                                   System.out.println("Ticket added");
                               } catch (SQLException e) {
                                   System.out.println(e.getMessage());
                               }
                           }

                           public void modifier(Ticket ticket) {
                               String req = "UPDATE tickets SET id_flight=?, passenger_id=?,passenger_email=? ,seat_number=?, ticket_class=?, ticket_price=?, ticket_status=?, ticket_booking_date=? WHERE ticket_id=?";
                               try {
                                   PreparedStatement pst = connection.prepareStatement(req);
                                   pst.setInt(1, ticket.getFlight_id());
                                   pst.setInt(2, ticket.getPassenger_id());
                                   pst.setString(3, ticket.getPassenger_email());
                                   pst.setString(4, ticket.getSeat_number());
                                   pst.setString(5, ticket.getTicketClass().name());
                                   pst.setDouble(6, ticket.getTicket_price());
                                   pst.setString(7, ticket.getStatus().name());
                                   pst.setTimestamp(8, ticket.getBooking_date());
                                   pst.setInt(9, ticket.getTicket_id());
                                   pst.executeUpdate();
                                   System.out.println("Ticket updated");
                               } catch (SQLException e) {
                                   System.out.println(e.getMessage());
                               }
                           }

                           public void supprimer(Ticket ticket) {
                               String req = "DELETE from tickets WHERE ticket_id=?";
                               try {
                                   PreparedStatement pst = connection.prepareStatement(req);
                                   pst.setInt(1, ticket.getTicket_id());
                                   pst.executeUpdate();
                                   System.out.println("Ticket deleted");
                               } catch (SQLException e) {
                                   System.out.println(e.getMessage());
                               }
                           }


                           public List<Ticket> rechercher() {
                               String req = "SELECT * FROM tickets";
                               List<Ticket> tickets = null;
                                 try {
                                      PreparedStatement pst = connection.prepareStatement(req);
                                      pst.executeQuery();
                                      System.out.println("Tickets retrieved");
                                 } catch (SQLException e) {
                                      System.out.println(e.getMessage());
                                 }
                                    return tickets;
                           }

    public List<Ticket> getTicketsByFlightId(int flightId) {
        String req = "SELECT * FROM tickets WHERE id_flight=?";
        List<Ticket> tickets = null;
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, flightId);
            pst.executeQuery();
            System.out.println("Tickets retrieved");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return tickets;

    }


    // TicketService.java
    public List<Ticket> getTicketsByUserId(int userId) {
        String req = "SELECT * FROM tickets WHERE passenger_id=?";
        List<Ticket> tickets = new ArrayList<>(); // Initialize the tickets list
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tickets.add(new Ticket(
                    rs.getInt("ticket_id"),
                    rs.getInt("id_flight"),
                    rs.getInt("passenger_id"),
                    rs.getString("passenger_email"),
                    rs.getString("seat_number"),
                    TicketClass.valueOf(rs.getString("ticket_class")),
                    rs.getDouble("ticket_price"),
                    TicketStatus.valueOf(rs.getString("ticket_status")),
                    rs.getTimestamp("ticket_booking_date")
                ));
            }
            System.out.println("Tickets retrieved");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tickets;
    }
}