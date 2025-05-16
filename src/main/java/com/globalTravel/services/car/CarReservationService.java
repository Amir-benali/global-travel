package com.globalTravel.services.car;

import com.globalTravel.models.car.CarDriver;
import com.globalTravel.models.car.CarReservation;
import com.globalTravel.models.car.Route;
import com.globalTravel.models.car.TypeCarReservation;
import com.globalTravel.models.user.Employee;
import com.globalTravel.models.user.User;
import com.globalTravel.services.IService;
import com.globalTravel.services.user.UserService;
import com.globalTravel.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarReservationService implements IService<CarReservation> {
    private Connection connection = DataSource.getInstance().getConnection();
    private OfferService offerService = new OfferService();
    private RouteService routeService = new RouteService();
    private UserService userService = new UserService();
    @Override
    public void ajouter(CarReservation carReservation) {
        String req = "INSERT INTO car_reservation (date,status,offer_id,route_id ) VALUES (?,?,?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setDate(1, carReservation.getDate());
            pst.setString(2, carReservation.getStatus().toString());
            pst.setInt(3, carReservation.getOffer().getId());
            pst.setInt(4, carReservation.getRoute().getId());
            pst.executeUpdate();
            System.out.println("added  car reservation");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void modifier(CarReservation carReservation) {
        String req = "UPDATE car_reservation SET date=?,status=?,offer_id=?,route_id=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setDate(1, carReservation.getDate());
            pst.setString(2, carReservation.getStatus().toString());
            pst.setInt(3, carReservation.getOffer().getId());
            pst.setInt(4, carReservation.getRoute().getId());
            pst.setInt(5, carReservation.getId());
            pst.executeUpdate();
            System.out.println("car reservation has been modified");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(CarReservation carReservation) {
        String req = "DELETE from car_reservation WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, carReservation.getId());
            pst.executeUpdate();
            System.out.println("car reservation has been deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<CarReservation> rechercher() {
        List<CarReservation> reservations = new ArrayList<>();

        String req = "SELECT * FROM car_reservation";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery(req);
            while (rs.next()) {
                CarReservation reservation = new CarReservation(rs.getInt("id"), rs.getDate("date"), TypeCarReservation.valueOf(rs.getString("status")), routeService.getRouteById( rs.getInt("route_id")),offerService.getOfferById(rs.getInt("offer_id")),  userService.getUserById( rs.getInt("user_id")));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return reservations;
    }
    public void updateStatus(CarReservation carReservation, TypeCarReservation status) {
        String req = "UPDATE car_reservation SET status=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, status.toString());
            pst.setInt(2, carReservation.getId());
            pst.executeUpdate();
            System.out.println("car reservation status has been updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public int addCarReservation(CarReservation carReservation) {
        String req = "INSERT INTO car_reservation (date, status, offer_id, route_id) VALUES (?, ?, ?, ?)";
        int generatedId = -1;

        try (PreparedStatement pst = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pst.setDate(1, carReservation.getDate());
            pst.setString(2, carReservation.getStatus().toString());
            pst.setInt(3, carReservation.getOffer().getId());
            pst.setInt(4, carReservation.getRoute().getId());

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                        System.out.println("Added car reservation with ID: " + generatedId);
                    } else {
                        System.out.println("No ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return generatedId;
    }

    public void assignEmployeeToReservation(int reservationId, ArrayList<User> employees) {
        String req = "insert INTO car_reservation_user (car_reservation_id, user_id) VALUES (?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            for (User employee : employees) {
                pst.setInt(1, reservationId);
                pst.setInt(2, employee.getId());
                pst.executeUpdate();
            }
            System.out.println("added  car reservation user");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public ArrayList<CarReservation> getReservationsByUser(int userId) {
        ArrayList<CarReservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM car_reservation JOIN car_reservation_user ON car_reservation.id = car_reservation_user.car_reservation_id WHERE car_reservation_user.user_id = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                CarReservation reservation = new CarReservation(rs.getInt("id"), rs.getDate("date"), TypeCarReservation.valueOf(rs.getString("status")), routeService.getRouteById( rs.getInt("route_id")),offerService.getOfferById(rs.getInt("offer_id")),  userService.getUserById( rs.getInt("user_id")));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return  reservations ;
    }
}


