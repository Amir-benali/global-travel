package com.globalTravel.services.car;

import com.globalTravel.models.car.Offer;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;
import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OfferService implements IService<Offer> {

    private Connection connection = DataSource.getInstance().getConnection();
    private PrivateCarService privateCarService = new PrivateCarService();
    private RouteService routeService = new RouteService();
    @Override
    public void ajouter(Offer offer) {
        String req = "INSERT INTO car_offer (description, date,price,route_id,car_id) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1,  offer.getDescription());
            pst.setTimestamp(2, Timestamp.valueOf(offer.getDate()));
            pst.setFloat(3, offer.getPrice());
            if(offer.getRoute() != null) {
                pst.setInt(4, offer.getRoute().getId());
            }
            else {
                pst.setNull(4, Types.INTEGER);
            }
            pst.setInt(5, offer.getCar().getId());


            pst.executeUpdate();
            System.out.println("added  offer");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Offer offer) {

        String req = "UPDATE car_offer SET description=? ,date=?,price=?,route_id=?,car_id=?,reserved_seats=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1,  offer.getDescription());
            pst.setTimestamp(2, Timestamp.valueOf(offer.getDate()));
            pst.setFloat(3, offer.getPrice());
            if(offer.getRoute() != null) {
                pst.setInt(4, offer.getRoute().getId());
            }
            else {
                pst.setNull(4, Types.INTEGER);
            }
            pst.setInt(5, offer.getCar().getId());
            String reservedSeatsJson = new Gson().toJson(offer.getReservedSeats());
            pst.setString(6, reservedSeatsJson);
            pst.setInt(7, offer.getId());

            pst.executeUpdate();
            System.out.println("offer has been modified");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void supprimer(Offer offer) {
        String req = "DELETE from car_offer WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, offer.getId());
            pst.executeUpdate();
            System.out.println("offer has been deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Offer> rechercher() {
        List<Offer>  offers = new ArrayList<>();

        String req = "SELECT * FROM car_offer ";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Offer offer = new Offer(rs.getInt("id"),rs.getString("description"),rs.getTimestamp("date").toLocalDateTime(), rs.getFloat("price"),routeService.getRouteById(rs.getInt("route_id")),privateCarService.getPrivateCarById(rs.getInt("car_id")) );
                 String seats =  rs.getString("reserved_seats");
                seats = seats.replaceAll("[\\[\\]\"]", ""); // Remove brackets and quotes
                offer.setReservedSeats( new ArrayList<>(Arrays.asList(seats.split(","))));
                offers.add(offer);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(offers);

        return offers;

    }

    public Offer getOfferById(int id) {
        Offer offer = null;

        String req = "SELECT * FROM car_offer WHERE id= ? ";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                offer = new Offer(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getFloat("price"),
                        routeService.getRouteById(rs.getInt("route_id")),
                        privateCarService.getPrivateCarById(rs.getInt("car_id"))
                );

                String seats =  rs.getString("reserved_seats");
                seats = seats.replaceAll("[\\[\\]\"]", ""); // Remove brackets and quotes
                offer.setReservedSeats( new ArrayList<>(Arrays.asList(seats.split(","))));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return offer;
    }


}
