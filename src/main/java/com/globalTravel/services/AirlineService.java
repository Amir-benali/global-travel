package com.globalTravel.services;

import com.globalTravel.models.Airline;
import com.globalTravel.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirlineService implements IService<Airline> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(Airline airline) {
        String req = "INSERT INTO airlines (airline_name,airline_iata_code,airline_country) VALUES (?,?,?)";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, airline.getAirline_name());
            pst.setString(2, airline.getAirline_code());
            pst.setString(3, airline.getCountry());
            pst.executeUpdate();
            System.out.println("Airline added");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Airline airline) {
        String req = "UPDATE airlines SET airline_name=?, airline_iata_code=?, airline_country=? WHERE airline_id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, airline.getAirline_name());
            pst.setString(2, airline.getAirline_code());
            pst.setString(3, airline.getCountry());
            pst.setInt(4, airline.getAirline_id());
            pst.executeUpdate();
            System.out.println("Airline updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void supprimer(Airline airline) {
        String req = "DELETE from airlines WHERE airline_id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, airline.getAirline_id());
            pst.executeUpdate();
            System.out.println("Airline deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<Airline> rechercher() {
        List<Airline> airlines = new ArrayList<>();
        String req = "SELECT * FROM airlines";
        try {
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery(req);
            while (res.next()) {
                airlines.add(new Airline(res.getInt("airline_id"), res.getString("airline_name"), res.getString("airline_iata_code"), res.getString("airline_country")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return airlines;
    }
}

