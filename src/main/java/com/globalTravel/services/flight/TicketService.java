package com.globalTravel.services.flight;

import com.globalTravel.models.flight.Ticket;
import com.globalTravel.models.flight.TicketClass;
import com.globalTravel.models.flight.TicketStatus;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketService implements IService<Ticket> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(Ticket ticket) {
        String req="INSERT INTO tickets (id_flight, seat_number, ticket_class, ticket_price, ticket_status, ticket_booking_date) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, ticket.getFlight_id());
            pst.setString(2, ticket.getSeat_number());
            pst.setString(3, ticket.getTicketClass().name());
            pst.setDouble(4, ticket.getTicket_price());
            pst.setString(5, ticket.getStatus().name());
            pst.setTimestamp(6, ticket.getBooking_date());

            pst.executeUpdate();

            System.out.println("Ticket added");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Ticket ticket) {
        String req = "UPDATE tickets SET id_flight=?, seat_number=?, ticket_class=?, ticket_price=?, ticket_status=?, ticket_booking_date=? WHERE ticket_id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, ticket.getFlight_id());
            pst.setString(2, ticket.getSeat_number());
            pst.setString(3, ticket.getTicketClass().name());
            pst.setDouble(4, ticket.getTicket_price());
            pst.setString(5, ticket.getStatus().name());
            pst.setTimestamp(6, ticket.getBooking_date());
            pst.setInt(7, ticket.getTicket_id());

            pst.executeUpdate();

            System.out.println("Ticket updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
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

    @Override
    public List<Ticket> rechercher() {
        List<Ticket> tickets = new ArrayList<>();
        String req="SELECT * FROM tickets";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.executeQuery();
            while (pst.getResultSet().next()) {
                tickets.add(new Ticket(pst.getResultSet().getInt("ticket_id"), pst.getResultSet().getInt("id_flight"), pst.getResultSet().getString("seat_number"), TicketClass.valueOf(pst.getResultSet().getString("ticket_class")), pst.getResultSet().getDouble("ticket_price"), TicketStatus.valueOf(pst.getResultSet().getString("ticket_status")), pst.getResultSet().getTimestamp("ticket_booking_date")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tickets;
    }

}

