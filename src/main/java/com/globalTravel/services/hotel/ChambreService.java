package com.globalTravel.services.hotel;


import com.globalTravel.models.hotel.Chambre;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChambreService implements IService<Chambre> {

    private Connection connection = DataSource.getInstance().getConnection();
    private HotelService hotelService = new HotelService();

    @Override
    public void ajouter(Chambre c) {
        String req = "INSERT INTO chambre (type_chambre_h, prix_nuit_h, dispo_h, option_h, id_hotel_j) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, c.getType_chambre_h());
            pst.setInt(2, c.getPrix_nuit_h());
            pst.setDate(3, Date.valueOf(c.getDispo_h())); // Conversion LocalDate -> java.sql.Date
            pst.setString(4, c.getOption_h());
            pst.setInt(5, c.getid_hotel_j().getId_hotel_h());
            pst.executeUpdate();
            System.out.println("Chambre ajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Chambre c) {
        String req = "UPDATE chambre SET type_chambre_h = ?, prix_nuit_h = ?, dispo_h = ?, option_h = ?, id_hotel_j = ? WHERE id_Chambre_h = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, c.getType_chambre_h());
            pst.setInt(2, c.getPrix_nuit_h());
            pst.setDate(3, Date.valueOf(c.getDispo_h()));
            pst.setString(4, c.getOption_h());
            pst.setInt(5, c.getid_hotel_j().getId_hotel_h());
            pst.setInt(6, c.getId_Chambre_h());
            pst.executeUpdate();
            System.out.println("Chambre modifiée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Chambre c) {
        String req = "DELETE FROM chambre WHERE id_Chambre_h = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, c.getId_Chambre_h());
            pst.executeUpdate();
            System.out.println("Chambre supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Chambre> rechercher() {
        List<Chambre> chambres = new ArrayList<>();
        String req = "SELECT * FROM Chambre";
        try (PreparedStatement pst = connection.prepareStatement(req);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                chambres.add(new Chambre(
                        rs.getInt("id_Chambre_h"),
                        rs.getString("type_chambre_h"),
                        rs.getInt("prix_nuit_h"),
                        rs.getDate("dispo_h").toLocalDate(),
                        rs.getString("option_h"),
                        hotelService.getHOTELById(rs.getInt("id_hotel_j"))
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return chambres;
    }

    public Chambre getChambreById(int id) {
        Chambre chambre = null;
        String req = "SELECT * FROM chambre WHERE id_Chambre_h = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                chambre = new Chambre(
                        rs.getInt("id_Chambre_h"),
                        rs.getString("type_chambre_h"),
                        rs.getInt("prix_nuit_h"),
                        rs.getDate("dispo_h").toLocalDate(),
                        rs.getString("option_h"),
                        hotelService.getHOTELById(rs.getInt("id_hotel_j"))
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur getChambreById : " + e.getMessage());
        }
        return chambre;
    }
}