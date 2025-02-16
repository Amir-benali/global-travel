package com.globalTravel.services.activity;

import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.TypeActivity;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityService implements IService<Activity> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public boolean ajouter(Activity activity) {
        String req = "INSERT INTO activity (dateDebut, dateFin, description, localisation, prixTotal, nomActivity, typeActivity, joinHotelId, joinVoitureId, joinVolsId) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setTimestamp(1, new java.sql.Timestamp(activity.getDateDebut().getTime()));
            pst.setTimestamp(2, new java.sql.Timestamp(activity.getDateFin().getTime()));
            pst.setString(3, activity.getDescription());
            pst.setString(4, activity.getLocalisation());
            pst.setInt(5, activity.getPrixTotal());
            pst.setString(6, activity.getNomActivity());
            pst.setString(7, activity.getTypeActivity().name());
            pst.setInt(8, activity.getJoinHotelId());
            pst.setInt(9, activity.getJoinVoitureId());
            pst.setInt(10, activity.getJoinVolsId());
            pst.executeUpdate();
            System.out.println("Activité ajoutée avec succès ");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'activité : " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean modifier(Activity activity) {
        String req = "UPDATE activity SET dateDebut=?, dateFin=?, description=?, localisation=?, prixTotal=?, nomActivity=?, typeActivity=?, joinHotelId=?, joinVoitureId=?, joinVolsId=? WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setTimestamp(1, new java.sql.Timestamp(activity.getDateDebut().getTime()));
            pst.setTimestamp(2, new java.sql.Timestamp(activity.getDateFin().getTime()));
            pst.setString(3, activity.getDescription());
            pst.setString(4, activity.getLocalisation());
            pst.setInt(5, activity.getPrixTotal());
            pst.setString(6, activity.getNomActivity());
            pst.setString(7, activity.getTypeActivity().name());
            pst.setInt(8, activity.getJoinHotelId());
            pst.setInt(9, activity.getJoinVoitureId());
            pst.setInt(10, activity.getJoinVolsId());
            pst.setInt(11, activity.getId());
            pst.executeUpdate();
            System.out.println("Activité modifiée avec succès ");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'activité : " + e.getMessage());
        }
        return false;
    }

    @Override
    public void supprimer(Activity activity) {
        String req = "DELETE FROM activity WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, activity.getId());
            pst.executeUpdate();
            System.out.println("Activité supprimée avec succès ");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'activité : " + e.getMessage());
        }
    }

    @Override
    public List<Activity> rechercher() {
        List<Activity> activities = new ArrayList<>();
        String req = "SELECT * FROM activity";
        try (PreparedStatement pst = connection.prepareStatement(req);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Activity activity = new Activity(
                        rs.getInt("id"),
                        rs.getTimestamp("dateDebut"),
                        rs.getTimestamp("dateFin"),
                        rs.getString("description"),
                        rs.getString("localisation"),
                        rs.getInt("prixTotal"),
                        rs.getString("nomActivity"),
                        getTypeActivityFromString(rs.getString("typeActivity")),
                        rs.getInt("joinHotelId"),
                        rs.getInt("joinVoitureId"),
                        rs.getInt("joinVolsId")
                );
                activities.add(activity);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des activités : " + e.getMessage());
        }
        return activities;
    }
    //converture  string par enum
    private TypeActivity getTypeActivityFromString(String typeActivityString) {
        try {
            return TypeActivity.valueOf(typeActivityString);
        } catch (IllegalArgumentException e) {
            System.out.println("Type d'activité invalide: " + typeActivityString);
            return null;
        }

    }

    public boolean existsById(int id) {
        String query = "SELECT COUNT(*) FROM activity WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Retourne vrai si l'ID existe
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }











}
