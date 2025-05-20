package com.globalTravel.services.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalTravel.models.flight.Airline;
import com.globalTravel.models.flight.Flight;
import com.globalTravel.models.flight.FlightStatus;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightService implements IService<Flight> {

    private final Gson gson = new Gson();
    private final Connection connection = DataSource.getInstance().getConnection();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void ajouter(Flight flight) {
        String req = "INSERT INTO flights (flight_number, airline_id, departure_country, arrival_country, departure_airport_name, arrival_airport_name, departure_time, arrival_time, duration_per_hours, available_seats, unavailable_seats, seats_number, flight_base_price, flight_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, flight.getFlight_number());
            pst.setInt(2, flight.getAirlineId());
            pst.setString(3, flight.getDeparture_country());
            pst.setString(4, flight.getArrival_country());
            pst.setString(5, flight.getDeparture_airport());
            pst.setString(6, flight.getArrival_airport());
            pst.setTimestamp(7, flight.getDeparture_time());
            pst.setTimestamp(8, flight.getArrival_time());
            pst.setInt(9, flight.getDuration());
            pst.setString(10, gson.toJson(flight.getAvailableSeats()));
            pst.setString(11, gson.toJson(flight.getUnavailableSeats()));
            pst.setInt(12, flight.getSeatsNumber());
            pst.setDouble(13, flight.getBase_price());
            pst.setString(14, flight.getStatus().name());

            pst.executeUpdate();
            System.out.println("Flight added");
        } catch (SQLException e) {
            System.out.println("Error adding flight: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Flight flight) {
        String req = "UPDATE flights SET flight_number=?, airline_id=?, departure_country=?, arrival_country=?, departure_airport_name=?, arrival_airport_name=?, departure_time=?, arrival_time=?, duration_per_hours=?, available_seats=?, unavailable_seats=?, seats_number=?, flight_base_price=?, flight_status=? WHERE id_flight=?";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, flight.getFlight_number());
            pst.setInt(2, flight.getAirlineId());
            pst.setString(3, flight.getDeparture_country());
            pst.setString(4, flight.getArrival_country());
            pst.setString(5, flight.getDeparture_airport());
            pst.setString(6, flight.getArrival_airport());
            pst.setTimestamp(7, flight.getDeparture_time());
            pst.setTimestamp(8, flight.getArrival_time());
            pst.setInt(9, flight.getDuration());
            pst.setString(10, gson.toJson(flight.getAvailableSeats()));
            pst.setString(11, gson.toJson(flight.getUnavailableSeats()));
            pst.setInt(12, flight.getSeatsNumber());
            pst.setDouble(13, flight.getBase_price());
            pst.setString(14, flight.getStatus().name());
            pst.setInt(15, flight.getId_flight());

            pst.executeUpdate();
            System.out.println("Flight updated");
        } catch (SQLException e) {
            System.out.println("Error updating flight: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Flight flight) {
        String req = "DELETE FROM flights WHERE id_flight=?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, flight.getId_flight());
            pst.executeUpdate();
            System.out.println("Flight deleted");
        } catch (SQLException e) {
            System.out.println("Error deleting flight: " + e.getMessage());
        }
    }

    @Override
    public List<Flight> rechercher() {
        List<Flight> flights = new ArrayList<>();
        String req = "SELECT * FROM flights";

        try (PreparedStatement pst = connection.prepareStatement(req);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                List<String> availableSeats = objectMapper.readValue(rs.getString("available_seats"), List.class);
                List<String> unavailableSeats = objectMapper.readValue(rs.getString("unavailable_seats"), List.class);

                flights.add(new Flight(
                        rs.getInt("id_flight"),
                        rs.getString("flight_number"),
                        rs.getInt("airline_id"),
                        rs.getString("departure_country"),
                        rs.getString("arrival_country"),
                        rs.getString("departure_airport_name"),
                        rs.getString("arrival_airport_name"),
                        rs.getTimestamp("departure_time"),
                        rs.getTimestamp("arrival_time"),
                        rs.getInt("duration_per_hours"),
                        availableSeats,
                        unavailableSeats,
                        rs.getInt("seats_number"),
                        rs.getDouble("flight_base_price"),
                        FlightStatus.valueOf(rs.getString("flight_status"))
                ));
            }

        } catch (SQLException | JsonProcessingException e) {
            System.out.println("Error fetching flights: " + e.getMessage());
        }
        return flights;
    }

    public boolean isFlightNumberExists(String flightNumber) {
        String query = "SELECT COUNT(*) FROM flights WHERE flight_number = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, flightNumber);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking flight number: " + e.getMessage());
        }
        return false;
    }

    public void decrementAvailableSeats(int flightId) {
        String checkSeatsQuery = "SELECT seats_number FROM flights WHERE id_flight = ?";
        String decrementSeatsQuery = "UPDATE flights SET seats_number = seats_number - 1 WHERE id_flight = ? AND seats_number > 0";
        try (PreparedStatement checkPst = connection.prepareStatement(checkSeatsQuery)) {
            checkPst.setInt(1, flightId);
            ResultSet rs = checkPst.executeQuery();
            if (rs.next() && rs.getInt("seats_number") > 0) {
                try (PreparedStatement decrementPst = connection.prepareStatement(decrementSeatsQuery)) {
                    decrementPst.setInt(1, flightId);
                    decrementPst.executeUpdate();
                    System.out.println("Available seats decremented");
                }
            } else {
                System.out.println("No available seats to decrement");
            }
        } catch (SQLException e) {
            System.out.println("Error decrementing seats: " + e.getMessage());
        }
    }

    public Flight getFlightById(int flightId) {
        String query = "SELECT * FROM flights WHERE id_flight = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, flightId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    List<String> availableSeats = objectMapper.readValue(rs.getString("available_seats"), List.class);
                    List<String> unavailableSeats = objectMapper.readValue(rs.getString("unavailable_seats"), List.class);
                    return new Flight(
                            rs.getInt("id_flight"),
                            rs.getString("flight_number"),
                            rs.getInt("airline_id"),
                            rs.getString("departure_country"),
                            rs.getString("arrival_country"),
                            rs.getString("departure_airport_name"),
                            rs.getString("arrival_airport_name"),
                            rs.getTimestamp("departure_time"),
                            rs.getTimestamp("arrival_time"),
                            rs.getInt("duration_per_hours"),
                            availableSeats,
                            unavailableSeats,
                            rs.getInt("seats_number"),
                            rs.getDouble("flight_base_price"),
                            FlightStatus.valueOf(rs.getString("flight_status"))
                    );
                }
            }
        } catch (SQLException | JsonProcessingException e) {
            System.err.println("Error getting flight by ID: " + e.getMessage());
        }
        return null;
    }
}
