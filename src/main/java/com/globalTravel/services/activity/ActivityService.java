package com.globalTravel.services.activity;

import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.TypeActivity;
import com.globalTravel.models.user.User;
import com.globalTravel.services.IActivityService;
import com.globalTravel.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityService implements IActivityService<Activity> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public boolean ajouter(Activity activity) {
        String req = "INSERT INTO activity (dateDebut, dateFin, description, localisation, prixTotal, nomActivity, typeActivity, joinHotelId, joinVoitureId, joinVolsId, user_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
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
            pst.setInt(11, activity.getUser_id());
            pst.executeUpdate();
            System.out.println("Activité ajoutée avec succès ");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'activité : " + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean modifier(Activity activity) {
        String req = "UPDATE activity SET dateDebut=?, dateFin=?, description=?, localisation=?, prixTotal=?, nomActivity=?, typeActivity=?, joinHotelId=?, joinVoitureId=?, joinVolsId=? ,user_id=? WHERE id=?";
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
            pst.setInt(11, activity.getUser_id());

            pst.executeUpdate();
            System.out.println("Activité modifiée avec succès ");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'activité : " + e.getMessage());
        }
        return true;
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
                        rs.getInt("joinVolsId"),
                        rs.getInt("user_id")
                );
                activities.add(activity);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des activités : " + e.getMessage());
        }
        return activities;
    }

    /**
     * Recherche des activités par nom.
     *
     * @param nomActivity Le nom de l'activité à rechercher.
     * @return Une liste d'activités correspondant au nom recherché.
     */
    public List<Activity> rechercherParNom(String nomActivity) {
        List<Activity> activities = new ArrayList<>();
        String req = "SELECT * FROM activity WHERE nomActivity LIKE ?"; // Requête SQL pour filtrer par nom
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, "%" + nomActivity + "%"); // Ajoute le wildcard % pour une recherche partielle
            ResultSet rs = pst.executeQuery();

            // Parcourt les résultats et les mappe à des objets Activity
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
                        rs.getInt("joinVolsId"),
                        rs.getInt("user_id")
                );
                activities.add(activity);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche des activités par nom : " + e.getMessage());
        }
        return activities;
    }

    // Convertit une chaîne de caractères en enum TypeActivity
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





    public void associateUserToActivity(int userId, int activityId) {
        String sql = "INSERT INTO user_activity (user_id, activity_id) VALUES (?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setInt(2, activityId);
            pst.executeUpdate();
            System.out.println("Utilisateur associé à l'activité avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'association de l'utilisateur à l'activité : " + e.getMessage());
        }
    }


    public List<Activity> getActivitiesForUser(int userId) {
        List<Activity> activities = new ArrayList<>();
        String query = "SELECT a.* FROM activity a " +
                "JOIN user_activity ua ON a.id = ua.activity_id " +
                "WHERE ua.user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Activity activity = new Activity(
                        resultSet.getInt("id"),
                        resultSet.getTimestamp("dateDebut"),
                        resultSet.getTimestamp("dateFin"),
                        resultSet.getString("description"),
                        resultSet.getString("localisation"),
                        resultSet.getInt("prixTotal"),
                        resultSet.getString("nomActivity"),
                        TypeActivity.valueOf(resultSet.getString("typeActivity")),
                        resultSet.getInt("joinHotelId"),
                        resultSet.getInt("joinVoitureId"),
                        resultSet.getInt("joinVolsId"),
                        resultSet.getInt("user_id")
                );
                activities.add(activity);
            }
        } catch (SQLException e) {

            System.out.println("Erreur lors de la récupération des activités : " + e.getMessage());
        }
        return activities;
    }

    public void rejectActivity(int activityId) {
        // Implémentez la logique pour marquer l'activité comme refusée ou la supprimer de la base de données.
        // Par exemple, exécuter une requête SQL pour mettre à jour ou supprimer l'activité.
        System.out.println("Activité refusée (ID: " + activityId + ") dans la base de données.");
    }

}