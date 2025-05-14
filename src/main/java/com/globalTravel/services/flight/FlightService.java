package com.globalTravel.services.flight;

import com.globalTravel.models.flight.Flight;
import com.globalTravel.models.flight.FlightStatus;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FlightService implements IService<Flight> {

    private Connection connection = DataSource.getInstance().getConnection();


    @Override
    public void ajouter(Flight flight) {
        String req = "INSERT INTO flights (flight_number, airline_name,departure_country, arrival_country , departure_airport_name, arrival_airport_name, departure_time, arrival_time, duration_per_hours, available_seats, flight_base_price, flight_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, flight.getFlight_number());
            pst.setString(2, flight.getAirline_name());
            pst.setString(3, flight.getDeparture_country());
            pst.setString(4, flight.getArrival_country());
            pst.setString(5, flight.getDeparture_airport());
            pst.setString(6, flight.getArrival_airport());
            pst.setTimestamp(7, flight.getDeparture_time());
            pst.setTimestamp(8, flight.getArrival_time());
            pst.setInt(9, flight.getDuration());
            pst.setInt(10, flight.getAvailable_seats());
            pst.setDouble(11, flight.getBase_price());
            pst.setString(12, flight.getStatus().name());

            pst.executeUpdate();

            System.out.println("Flight added");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Flight flight) {
        String req = "UPDATE flights SET flight_number=?, airline_name=?, departure_country=?, arrival_country=?, departure_airport_name=?, arrival_airport_name=?, departure_time=?, arrival_time=?, duration_per_hours=?, available_seats=?, flight_base_price=?, flight_status=? WHERE id_flight=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, flight.getFlight_number());
            pst.setString(2, flight.getAirline_name());
            pst.setString(3, flight.getDeparture_country());
            pst.setString(4, flight.getArrival_country());
            pst.setString(5, flight.getDeparture_airport());
            pst.setString(6, flight.getArrival_airport());
            pst.setTimestamp(7, flight.getDeparture_time());
            pst.setTimestamp(8, flight.getArrival_time());
            pst.setInt(9, flight.getDuration());
            pst.setInt(10, flight.getAvailable_seats());
            pst.setDouble(11, flight.getBase_price());
            pst.setString(12, flight.getStatus().toString());
            pst.setInt(13, flight.getId_flight());

            pst.executeUpdate();

            System.out.println("Flight updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Flight flight) {
        String req = "DELETE from flights WHERE id_flight=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, flight.getId_flight());

            pst.executeUpdate();
            System.out.println("Flight deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Flight> rechercher() {
        List<Flight> flights=new ArrayList<>();
        String req = "SELECT * FROM flights";

        try{
            PreparedStatement pst=connection.prepareStatement(req);
            ResultSet res=pst.executeQuery(req);
            while (res.next()){
                flights.add(new Flight(res.getInt("id_flight"),
                        res.getString("flight_number"),
                        res.getString("airline_name"),
                        res.getString("departure_country"),
                        res.getString("arrival_country"),
                        res.getString("departure_airport_name"),
                        res.getString("arrival_airport_name"),
                        res.getTimestamp("departure_time"),
                        res.getTimestamp("arrival_time"),
                        res.getInt("duration_per_hours"),
                        res.getInt("available_seats"),
                        res.getDouble("flight_base_price"),
                        FlightStatus.valueOf(res.getString("flight_status"))));

            }
        }catch (SQLException e){
            System.out.printf(e.getMessage());
        }
        return flights;
    }


    public boolean isFlightNumberExists(String flightNumber) {
        String query = "SELECT COUNT(*) FROM flights WHERE flight_number = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, flightNumber);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking flight number: " + e.getMessage());
        }
        return false;
    }

    // FlightService.java
    public void decrementAvailableSeats(int flightId) {
        String checkSeatsQuery = "SELECT available_seats FROM flights WHERE id_flight = ?";
        String decrementSeatsQuery = "UPDATE flights SET available_seats = available_seats - 1 WHERE id_flight = ? AND available_seats > 0";
        try {
            PreparedStatement checkPst = connection.prepareStatement(checkSeatsQuery);
            checkPst.setInt(1, flightId);
            ResultSet rs = checkPst.executeQuery();
            if (rs.next() && rs.getInt("available_seats") > 0) {
                PreparedStatement decrementPst = connection.prepareStatement(decrementSeatsQuery);
                decrementPst.setInt(1, flightId);
                decrementPst.executeUpdate();
                System.out.println("Available seats decremented");
            } else {
                System.out.println("No available seats to decrement");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Flight getFlightById(int flightId) {
        String query = "SELECT * FROM flights WHERE id_flight = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, flightId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Flight(rs.getInt("id_flight"),
                            rs.getString("flight_number"),
                            rs.getString("airline_name"),
                            rs.getString("departure_country"),
                            rs.getString("arrival_country"),
                            rs.getString("departure_airport_name"),
                            rs.getString("arrival_airport_name"),
                            rs.getTimestamp("departure_time"),
                            rs.getTimestamp("arrival_time"),
                            rs.getInt("duration_per_hours"),
                            rs.getInt("available_seats"),
                            rs.getDouble("flight_base_price"),
                            FlightStatus.valueOf(rs.getString("flight_status")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting flight by ID: " + e.getMessage());
        }
        return null;
    }
}

