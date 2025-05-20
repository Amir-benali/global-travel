package com.globalTravel.services.hotel;

import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HotelService implements IService<Hotel> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(Hotel h) {
        String req = "INSERT INTO hotel (nom_h, adresse_h, ville_h, pays_h, categorie_h, services_h, coordonnees_h, avis_h) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, h.getNom_h());
            pst.setString(2, h.getAdresse_h());
            pst.setString(3, h.getVille_h());
            pst.setString(4, h.getPays_h());
            pst.setInt(5, h.getCategorie_h());
            pst.setString(6, h.getServices_h());
            pst.setString(7, h.getCoordonnees_h());
            pst.setString(8, h.getAvis_h());
            pst.executeUpdate();
            System.out.println("Hôtel ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Hotel h) {
        String req = "UPDATE hotel SET nom_h = ?, adresse_h = ?, ville_h = ?, pays_h = ?, categorie_h = ?, services_h = ?, coordonnees_h = ?, avis_h = ? WHERE id_hotel_h = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, h.getNom_h());
            pst.setString(2, h.getAdresse_h());
            pst.setString(3, h.getVille_h());
            pst.setString(4, h.getPays_h());
            pst.setInt(5, h.getCategorie_h());
            pst.setString(6, h.getServices_h());
            pst.setString(7, h.getCoordonnees_h());
            pst.setString(8, h.getAvis_h());
            pst.setInt(9, h.getId_hotel_h());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Hôtel modifié avec succès !");
            } else {
                System.out.println("Aucune modification effectuée. Vérifie si l'ID existe.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Hotel h) {
        String req = "DELETE FROM hotel WHERE id_hotel_h = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, h.getId_hotel_h());
            pst.executeUpdate();
            System.out.println("Hôtel supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Hotel getHOTELById(int id) {
        Hotel hotel = null;
        String req = "SELECT * FROM hotel WHERE id_hotel_h = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                hotel = new Hotel(
                        rs.getInt("id_hotel_h"),
                        rs.getString("nom_h"),
                        rs.getString("adresse_h"),
                        rs.getString("ville_h"),
                        rs.getString("pays_h"),
                        rs.getInt("categorie_h"),
                        rs.getString("services_h"),
                        rs.getString("coordonnees_h"),
                        rs.getString("avis_h")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur getHOTELById : " + e.getMessage());
        }
        return hotel;
    }

    @Override
    public List<Hotel> rechercher() {
        List<Hotel> hotels = new ArrayList<>();
        String req = "SELECT * FROM hotel";
        try (PreparedStatement pst = connection.prepareStatement(req);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                hotels.add(new Hotel(
                        rs.getInt("id_hotel_h"),
                        rs.getString("nom_h"),
                        rs.getString("adresse_h"),
                        rs.getString("ville_h"),
                        rs.getString("pays_h"),
                        rs.getInt("categorie_h"),
                        rs.getString("services_h"),
                        rs.getString("coordonnees_h"),
                        rs.getString("avis_h")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return hotels;
    }

    public List<Hotel> rechercherParNom(String nom) {
        List<Hotel> hotels = new ArrayList<>();
        String req = "SELECT * FROM hotel WHERE nom_h LIKE ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, "%" + nom + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                hotels.add(new Hotel(
                        rs.getInt("id_hotel_h"),
                        rs.getString("nom_h"),
                        rs.getString("adresse_h"),
                        rs.getString("ville_h"),
                        rs.getString("pays_h"),
                        rs.getInt("categorie_h"),
                        rs.getString("services_h"),
                        rs.getString("coordonnees_h"),
                        rs.getString("avis_h")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche par nom : " + e.getMessage());
        }
        return hotels;
    }
}