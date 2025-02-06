package globaltravel.modules.FlightModule.services;

import globaltravel.global.utils.DataSource;
import globaltravel.modules.FlightModule.models.Flight;
import globaltravel.modules.FlightModule.models.FlightStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightService implements IService<Flight> {

    private Connection connection = DataSource.getInstance().getConnection();


    @Override
    public void ajouter(Flight flight) {
        String req = "INSERT INTO flights (flight_number, airline_id, departure_airport_name, arrival_airport_name, departure_time, arrival_time, duration_per_hours, available_seats, flight_base_price, flight_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, flight.getFlight_number());
            pst.setInt(2, flight.getAirline_id());
            pst.setString(3, flight.getDeparture_airport());
            pst.setString(4, flight.getArrival_airport());
            pst.setString(5, flight.getDeparture_time());
            pst.setString(6, flight.getArrival_time());
            pst.setInt(7, flight.getDuration());
            pst.setInt(8, flight.getAvailable_seats());
            pst.setDouble(9, flight.getBase_price());
            pst.setString(10, flight.getStatus().name());

            pst.executeUpdate();

            System.out.println("Flight added");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Flight flight) {
        String req = "UPDATE flights SET flight_number=?, airline_id=?, departure_airport_name=?, arrival_airport_name=?, departure_time=?, arrival_time=?, duration_per_hours=?, available_seats=?, flight_base_price=?, flight_status=? WHERE id_flight=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, flight.getFlight_number());
            pst.setInt(2, flight.getAirline_id());
            pst.setString(3, flight.getDeparture_airport());
            pst.setString(4, flight.getArrival_airport());
            pst.setString(5, flight.getDeparture_time());
            pst.setString(6, flight.getArrival_time());
            pst.setInt(7, flight.getDuration());
            pst.setInt(8, flight.getAvailable_seats());
            pst.setDouble(9, flight.getBase_price());
            pst.setString(10, flight.getStatus().toString());
            pst.setInt(11, flight.getId_flight());

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
                        res.getInt("airline_id"),
                        res.getString("departure_airport_name"),
                        res.getString("arrival_airport_name"),
                        res.getString("departure_time"),
                        res.getString("arrival_time"),
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
}
