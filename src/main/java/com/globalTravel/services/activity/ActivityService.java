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
        Activity existingActivity = findById(activity.getId());
        if (existingActivity == null) {
            System.out.println("Activité non trouvée avec l'ID: " + activity.getId());
            return false;
        }

        // Vérification des clés étrangères seulement si elles sont modifiées et non nulles
        if (activity.getJoinVolsId() != existingActivity.getJoinVolsId() && activity.getJoinVolsId() != 0) {
            if (!foreignKeyExists("flights", "id_flight", activity.getJoinVolsId())) {
                System.out.println("Erreur: Le nouveau vol référencé n'existe pas");
                return false;
            }
        }

        if (activity.getJoinHotelId() != existingActivity.getJoinHotelId() && activity.getJoinHotelId() != 0) {
            if (!foreignKeyExists("hotels", "id_hotel", activity.getJoinHotelId())) {
                System.out.println("Erreur: Le nouvel hôtel référencé n'existe pas");
                return false;
            }
        }

        if (activity.getJoinVoitureId() != existingActivity.getJoinVoitureId() && activity.getJoinVoitureId() != 0) {
            if (!foreignKeyExists("voitures", "id_voiture", activity.getJoinVoitureId())) {
                System.out.println("Erreur: La nouvelle voiture référencée n'existe pas");
                return false;
            }
        }

        String req = "UPDATE activity SET dateDebut=?, dateFin=?, description=?, localisation=?, prixTotal=?, nomActivity=?, typeActivity=?, joinHotelId=?, joinVoitureId=?, joinVolsId=? WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setTimestamp(1, new java.sql.Timestamp(activity.getDateDebut().getTime()));
            pst.setTimestamp(2, new java.sql.Timestamp(activity.getDateFin().getTime()));
            pst.setString(3, activity.getDescription());
            pst.setString(4, activity.getLocalisation());
            pst.setInt(5, activity.getPrixTotal());
            pst.setString(6, activity.getNomActivity());
            pst.setString(7, activity.getTypeActivity().name());

            // Utilisation de setObject pour permettre les valeurs NULL
            pst.setObject(8, activity.getJoinHotelId() != 0 ? activity.getJoinHotelId() : null);
            pst.setObject(9, activity.getJoinVoitureId() != 0 ? activity.getJoinVoitureId() : null);
            pst.setObject(10, activity.getJoinVolsId() != 0 ? activity.getJoinVolsId() : null);

            pst.setInt(11, activity.getId());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Activité modifiée avec succès");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'activité : " + e.getMessage());
            if (e.getMessage().contains("foreign key constraint")) {
                System.out.println("Détail de l'erreur FK: " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public void supprimer(Activity activity) {
        try {
            connection.setAutoCommit(false);

            String deleteUserActivity = "DELETE FROM user_activity WHERE activity_id = ?";
            try (PreparedStatement pst = connection.prepareStatement(deleteUserActivity)) {
                pst.setInt(1, activity.getId());
                pst.executeUpdate();
            }

            String deleteActivity = "DELETE FROM activity WHERE id = ?";
            try (PreparedStatement pst = connection.prepareStatement(deleteActivity)) {
                pst.setInt(1, activity.getId());
                pst.executeUpdate();
            }

            connection.commit();
            System.out.println("Activité et ses associations supprimées avec succès");

        } catch (SQLException e) {
            try {
                connection.rollback();
                System.out.println("Transaction annulée en raison d'une erreur");
            } catch (SQLException ex) {
                System.out.println("Erreur lors de l'annulation de la transaction : " + ex.getMessage());
            }
            System.out.println("Erreur lors de la suppression de l'activité : " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Erreur lors de la réactivation de l'auto-commit : " + e.getMessage());
            }
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

    public List<Activity> rechercherParNom(String nomActivity) {
        List<Activity> activities = new ArrayList<>();
        String req = "SELECT *, " +
                "CASE " +
                "  WHEN nomActivity LIKE ? THEN 3 " +
                "  WHEN nomActivity LIKE ? THEN 2 " +
                "  WHEN nomActivity LIKE ? THEN 1 " +
                "  ELSE 0 " +
                "END as pertinence " +
                "FROM activity " +
                "WHERE nomActivity LIKE ? OR nomActivity LIKE ? OR nomActivity LIKE ? " +
                "ORDER BY pertinence DESC, nomActivity ASC";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            String exactMatch = nomActivity;
            String startsWith = nomActivity + "%";
            String contains = "%" + nomActivity + "%";

            pst.setString(1, exactMatch);
            pst.setString(2, startsWith);
            pst.setString(3, contains);
            pst.setString(4, exactMatch);
            pst.setString(5, startsWith);
            pst.setString(6, contains);

            ResultSet rs = pst.executeQuery();

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

    public Activity findById(int id) {
        String query = "SELECT * FROM activity WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new Activity(
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
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'activité: " + e.getMessage());
        }
        return null;
    }

    private boolean foreignKeyExists(String tableName, String idColumn, int idValue) {
        if (idValue == 0) {
            return true; // Permet les valeurs nulles
        }

        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + idColumn + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idValue);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de la clé étrangère: " + e.getMessage());
        }
        return false;
    }

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
                return resultSet.getInt(1) > 0;
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
        String sql = "DELETE FROM activity WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, activityId);
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Activité refusée et supprimée avec succès (ID: " + activityId + ")");
            } else {
                System.out.println("Aucune activité trouvée avec l'ID: " + activityId);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du rejet de l'activité : " + e.getMessage());
        }
    }
}