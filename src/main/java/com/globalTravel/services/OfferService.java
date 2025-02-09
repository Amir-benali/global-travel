package com.globalTravel.services;

import com.globalTravel.models.Offer;
import com.globalTravel.models.PrivateCar;
import com.globalTravel.models.Route;
import com.globalTravel.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
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
            pst.setInt(4, offer.getRoute().getId());
            pst.setInt(5, offer.getCar().getId());


            pst.executeUpdate();
            System.out.println("added  offer");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Offer offer) {

        String req = "UPDATE car_offer SET description=? ,date=?,price=?,route_id=?,car_id=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1,  offer.getDescription());
            pst.setTimestamp(2, Timestamp.valueOf(offer.getDate()));
            pst.setFloat(3, offer.getPrice());
            pst.setInt(4, offer.getRoute().getId());
            pst.setInt(5, offer.getCar().getId());
            pst.setInt(6, offer.getId());

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
                offers.add(new Offer(rs.getInt("id"),rs.getString("description"),rs.getTimestamp("date").toLocalDateTime(), rs.getFloat("price"),routeService.getRouteById(rs.getInt("route_id")),privateCarService.getPrivateCarById(rs.getInt("car_id")) ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

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
                offer = new Offer(rs.getInt("id"),rs.getString("description"),rs.getTimestamp("date").toLocalDateTime(), rs.getFloat("price"),routeService.getRouteById(rs.getInt("route_id")),privateCarService.getPrivateCarById(rs.getInt("car_id")));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return offer;
    }


}
