package com.globalTravel.services;


import com.globalTravel.models.TypeActivity;
import com.globalTravel.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TypeActivityService implements IService<TypeActivity> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(TypeActivity typeActivity) {
        String req = "INSERT INTO typeactivity (nomEvenement, nomType) VALUES (?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, typeActivity.getNomEvenement());
            pst.setString(2, typeActivity.getNomType());
            pst.executeUpdate();
            System.out.println("Type d'activité ajoute avec succes ");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l ajout du type d'activite : " + e.getMessage());
        }
    }

    @Override
    public void modifier(TypeActivity typeActivity) {
        String req = "UPDATE typeactivity SET nomEvenement=?, nomType=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, typeActivity.getNomEvenement());
            pst.setString(2, typeActivity.getNomType());
            pst.setInt(3, typeActivity.getId());
            pst.executeUpdate();
            System.out.println("Type d'activité modifie avec succes ");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du type d'activite : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(TypeActivity typeActivity) {
        String req = "DELETE FROM typeactivity WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, typeActivity.getId());
            pst.executeUpdate();
            System.out.println("Type d'activité supprimé avec succes ");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du type d'activité : " + e.getMessage());
        }
    }

    @Override
    public List<TypeActivity> rechercher() {
        List<TypeActivity> typeActivities = new ArrayList<>();
        String req = "SELECT * FROM typeactivity";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                TypeActivity typeActivity = new TypeActivity(
                        rs.getInt("id"),
                        rs.getString("nomEvenement"),
                        rs.getString("nomType")
                );
                typeActivities.add(typeActivity);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des types d'activités : " + e.getMessage());
        }

        return typeActivities;
    }
}
