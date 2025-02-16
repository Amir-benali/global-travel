package com.globalTravel.services.car;



import com.globalTravel.models.car.CarDriver;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarDriverService implements IService<CarDriver> {
    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public boolean ajouter(CarDriver carDriver) {
        String req = "INSERT INTO car_driver (first_name, last_name ,phone) VALUES (?,?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, carDriver.getFirstName());
            pst.setString(2, carDriver.getLastName());
            pst.setString(3, carDriver.getPhone());

            pst.executeUpdate();
            System.out.println("added  car driver");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean modifier(CarDriver carDriver) {
        String req = "UPDATE car_driver SET first_name=? ,last_name=?,phone=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, carDriver.getFirstName());
            pst.setString(2, carDriver.getLastName());
            pst.setString(3, carDriver.getPhone());
            pst.setInt(4, carDriver.getId());

            pst.executeUpdate();
            System.out.println("car driver has been modified");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public void supprimer(CarDriver carDriver) {
        String req = "DELETE from car_driver WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, carDriver.getId());
            pst.executeUpdate();
            System.out.println("car driver has been deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public CarDriver getCarDriverById(int id) {
        CarDriver driver = null;

        String req = "SELECT * FROM car_driver WHERE id= ? ";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                driver = new CarDriver(rs.getInt("id"), rs.getString("first_name"),rs.getString("last_name"), rs.getString("phone"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return driver;
    }

    @Override
    public List<CarDriver> rechercher() {
        List<CarDriver> drivers = new ArrayList<>();

        String req = "SELECT * FROM car_driver";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery(req);
            while (rs.next()) {
                drivers.add(new CarDriver(rs.getInt("id"), rs.getString("first_name"),rs.getString("last_name"), rs.getString("phone")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return drivers;
    }
}
