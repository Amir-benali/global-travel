package com.globalTravel.services.car;



import com.globalTravel.models.car.PrivateCar;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrivateCarService implements IService<PrivateCar> {

    private Connection connection = DataSource.getInstance().getConnection();
    private CarDriverService carDriverservice = new CarDriverService();

    @Override
    public void ajouter(PrivateCar privateCar) {
        String req = "INSERT INTO private_car (brand, model ,num_place,image,id_driver) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, privateCar.getBrand());
            pst.setString(2, privateCar.getModel());
            pst.setInt(3, privateCar.getNum_place());
            pst.setString(4, privateCar.getImage());
            pst.setInt(5, privateCar.getCarDriver().getId());

            pst.executeUpdate();
            System.out.println("added private car");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(PrivateCar privateCar) {
        String req = "UPDATE private_car SET brand=? ,model=?,num_place=?,id_driver=?,image=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, privateCar.getBrand());
            pst.setString(2, privateCar.getModel());
            pst.setInt(3, privateCar.getNum_place());
            pst.setInt(4, privateCar.getCarDriver().getId());
            pst.setString(5, privateCar.getImage());
            pst.setInt(6, privateCar.getId());

            pst.executeUpdate();
            System.out.println("private car has been modified");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(PrivateCar privateCar) {
        String req = "DELETE from private_car WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, privateCar.getId());
            pst.executeUpdate();
            System.out.println("private car has been deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<PrivateCar> rechercher() {
        List<PrivateCar> cars = new ArrayList<>();

        String req = "SELECT * FROM private_car";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery(req);
            while (rs.next()) {
                cars.add(new PrivateCar(rs.getInt("id"), rs.getString("brand"),rs.getString("model"), rs.getInt("num_place"),carDriverservice.getCarDriverById(rs.getInt("id_driver")),rs.getString("image")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return cars;
    }
    public PrivateCar getPrivateCarById(int id) {
        PrivateCar car = null;

        String req = "SELECT * FROM private_car where id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                car =new PrivateCar(rs.getInt("id"), rs.getString("brand"),rs.getString("model"), rs.getInt("num_place"),carDriverservice.getCarDriverById(rs.getInt("id_driver")),rs.getString("image"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return car;
    }




}







