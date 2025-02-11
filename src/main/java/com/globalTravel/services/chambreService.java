package com.globalTravel.services;



import com.globalTravel.models.chambre;
import com.globalTravel.utils.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class chambreService implements IService<chambre> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(chambre c) {
        String req = "INSERT INTO chambre (type_chambre_h, prix_nuit_h, dispo_h, option_h) VALUES ('"
                + c.getType_chambre_h() + "', "
                + c.getPrix_nuit_h() + ", '"
                + c.getDispo_h() + "', '"
                + c.getOption_h() + "')";
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Chambre ajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(chambre c) {
        String req = "UPDATE chambre SET type_chambre_h='" + c.getType_chambre_h() + "', prix_nuit_h=" + c.getPrix_nuit_h()
                + ", dispo_h='" + c.getDispo_h() + "', option_h='" + c.getOption_h() + "' WHERE id_Chambre_h=" + c.getId_Chambre_h();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Chambre modifiée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(chambre c) {
        String req = "DELETE FROM chambre WHERE id_Chambre_h=" + c.getId_Chambre_h();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Chambre supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<chambre> rechercher() {
        List<chambre> chambres = new ArrayList<>();
        String req = "SELECT * FROM chambre";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                chambres.add(new chambre(
                        rs.getInt("id_Chambre_h"),
                        rs.getString("type_chambre_h"),
                        rs.getInt("prix_nuit_h"),
                        rs.getDate("dispo_h").toLocalDate(),
                        rs.getString("option_h")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return chambres;
    }
}
