package com.globalTravel.services.hotel;



import com.globalTravel.models.hotel.hotel;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class hotelService implements IService<hotel> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public boolean ajouter(hotel h) {
        String req = "INSERT INTO hotel (nom_h, adresse_h, ville_h, pays_h, categorie_h, services_h, coordonnees_h, avis_h) VALUES ('"+h.getNom_h()+"','"+h.getAdresse_h()+"','"+h.getVille_h()+"','"+h.getPays_h()+"',"+h.getCategorie_h()+",'"+h.getServices_h()+"','"+h.getCoordonnees_h()+"','"+h.getAvis_h()+"')";
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Hôtel ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean modifier(hotel h) {
        String req = "UPDATE hotel SET nom_h='"+h.getNom_h()+"', adresse_h='"+h.getAdresse_h()+"', ville_h='"+h.getVille_h()+"', pays_h='"+h.getPays_h()+"', categorie_h="+h.getCategorie_h()+", services_h='"+h.getServices_h()+"', coordonnees_h='"+h.getCoordonnees_h()+"', avis_h='"+h.getAvis_h()+"' WHERE id_hotel_h="+h.getId_hotel_h();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Hôtel modifié");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public void supprimer(hotel h) {
        String req = "DELETE FROM hotel WHERE id_hotel_h="+h.getId_hotel_h();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Hôtel supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<hotel> rechercher() {
        List<hotel> hotels = new ArrayList<>();
        String req = "SELECT * FROM hotel";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                hotels.add(new hotel(rs.getInt("id_hotel_h"), rs.getString("nom_h"), rs.getString("adresse_h"), rs.getString("ville_h"), rs.getString("pays_h"), rs.getInt("categorie_h"), rs.getString("services_h"), rs.getString("coordonnees_h"), rs.getString("avis_h")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return hotels;
    }


}

