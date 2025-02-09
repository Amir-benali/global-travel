package com.globalTravel.services;

import com.globalTravel.models.CarDriver;
import com.globalTravel.models.Route;
import com.globalTravel.utils.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RouteService implements IService<Route> {
    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(Route route) {
        String req = "INSERT INTO car_route (date_start, date_destination,location_start,location_destination) VALUES (?,?,?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setTimestamp(1,  Timestamp.valueOf(route.getDate_start()));
            pst.setTimestamp(2, Timestamp.valueOf(route.getDate_destination()));
            pst.setString(3, route.getLocation_start());
            pst.setString(4, route.getLocation_destination());


            pst.executeUpdate();
            System.out.println("added  route");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Route route) {

        String req = "UPDATE car_route SET date_start=? ,date_destination=?,location_start=?,location_destination=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setTimestamp(1, Timestamp.valueOf(route.getDate_start()));
            pst.setTimestamp(2, Timestamp.valueOf(route.getDate_destination()));
            pst.setString(3, route.getLocation_start());
            pst.setString(4, route.getLocation_destination());
            pst.setInt(5, route.getId());

            pst.executeUpdate();
            System.out.println("route has been modified");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void supprimer(Route route) {
        String req = "DELETE from car_route WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, route.getId());
            pst.executeUpdate();
            System.out.println("route has been deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Route> rechercher() {
        List<Route>  routes = new ArrayList<>();

        String req = "SELECT * FROM car_route ";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                routes.add(new Route(rs.getInt("id"),rs.getTimestamp("date_start").toLocalDateTime(),rs.getTimestamp("date_destination").toLocalDateTime(),rs.getString("location_start"),rs.getString("location_destination")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return routes;

    }

    public Route getRouteById(int id) {
        Route route = null;

        String req = "SELECT * FROM car_route WHERE id= ? ";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                route = new Route(rs.getInt("id"),rs.getTimestamp("date_start").toLocalDateTime(),rs.getTimestamp("date_destination").toLocalDateTime(),rs.getString("location_start"),rs.getString("location_destination"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return route;
    }

}
