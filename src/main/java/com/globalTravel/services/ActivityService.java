package com.globalTravel.services;

import com.globalTravel.models.Activity;
import com.globalTravel.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityService implements IService<Activity> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(Activity activity) {
        String req = "INSERT INTO activity (dateDebut, dateFin, description, localisation, notification, prixTotal, hotelInclus, volInclus, voitureIncluse) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setDate(1, new java.sql.Date(activity.getDateDebut().getTime()));
            pst.setDate(2, new java.sql.Date(activity.getDateFin().getTime()));
            pst.setString(3, activity.getDescription());
            pst.setString(4, activity.getLocalisation());
            pst.setBoolean(5, activity.isNotification());
            pst.setInt(6, activity.getPrixTotal());
            pst.setBoolean(7, activity.isHotelInclus());
            pst.setBoolean(8, activity.isVolInclus());
            pst.setBoolean(9, activity.isVoitureIncluse());
            pst.executeUpdate();
            System.out.println("Activité ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'activité : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Activity activity) {
        String req = "UPDATE activity SET dateDebut=?, dateFin=?, description=?, localisation=?, notification=?, prixTotal=?, hotelInclus=?, volInclus=?, voitureIncluse=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setDate(1, new java.sql.Date(activity.getDateDebut().getTime()));
            pst.setDate(2, new java.sql.Date(activity.getDateFin().getTime()));
            pst.setString(3, activity.getDescription());
            pst.setString(4, activity.getLocalisation());
            pst.setBoolean(5, activity.isNotification());
            pst.setInt(6, activity.getPrixTotal());
            pst.setBoolean(7, activity.isHotelInclus());
            pst.setBoolean(8, activity.isVolInclus());
            pst.setBoolean(9, activity.isVoitureIncluse());
            pst.setInt(10, activity.getId());
            pst.executeUpdate();
            System.out.println("Activité modifiée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'activité : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Activity activity) {
        String req = "DELETE FROM activity WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, activity.getId());
            pst.executeUpdate();
            System.out.println("Activité supprimée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'activité : " + e.getMessage());
        }
    }

    @Override
    public List<Activity> rechercher() {
        List<Activity> activities = new ArrayList<>();
        String req = "SELECT * FROM activity";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Activity activity = new Activity(
                        rs.getInt("id"),
                        rs.getDate("dateDebut"),
                        rs.getDate("dateFin"),
                        rs.getString("description"),
                        rs.getString("localisation"),
                        rs.getBoolean("notification"),
                        rs.getInt("prixTotal"),
                        rs.getBoolean("hotelInclus"),
                        rs.getBoolean("volInclus"),
                        rs.getBoolean("voitureIncluse")
                );
                activities.add(activity);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des activités : " + e.getMessage());
        }

        return activities;
    }
}
