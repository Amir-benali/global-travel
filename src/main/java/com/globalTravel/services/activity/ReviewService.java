package com.globalTravel.services.activity;

import com.globalTravel.models.activity.Review;
import com.globalTravel.services.IActivityService;
import com.globalTravel.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewService {

    private Connection connection = DataSource.getInstance().getConnection();



    public void ajouter(Review review) {
        if (!activityExists(review.getActivityId())) {
            System.out.println("Erreur : L'activité avec l'ID " + review.getActivityId() + " n'existe pas.");
            return;
        }

        String req = "INSERT INTO review (commentaire, note, dateReview, activityId) VALUES (?, ?, NOW(), ?)";
        try (PreparedStatement pst = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, review.getCommentaire());
            pst.setInt(2, review.getNote());
            pst.setInt(3, review.getActivityId());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Avis ajouté avec succès ");
            } else {
                System.out.println("Échec de l ajout de ");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l ajout de l'avis : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifier(Review review) {
        String req = "UPDATE review SET commentaire=?, note=?, activityId=? WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, review.getCommentaire());
            pst.setInt(2, review.getNote());
            pst.setInt(3, review.getActivityId());
            pst.setInt(4, review.getId());
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Avis modifié avec succès !");
            } else {
                System.out.println("Aucun avis trouvé avec l'ID " + review.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'avis : " + e.getMessage());
        }
    }

    public void supprimer(Review review) {
        String req = "DELETE FROM review WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, review.getId());
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Avis supprimé avec succès ");
            } else {
                System.out.println("Aucun avis trouvé avec l'ID " + review.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'avis : " + e.getMessage());
        }
    }

    public List<Review> rechercher() {
        List<Review> reviews = new ArrayList<>();
        String req = "SELECT * FROM review";
        try (PreparedStatement pst = connection.prepareStatement(req);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("id"),
                        rs.getString("commentaire"),
                        rs.getInt("note"),
                        rs.getTimestamp("dateReview").toLocalDateTime(),
                        rs.getInt("activityId")
                );
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des avis : " + e.getMessage());
        }
        return reviews;
    }

    /// Vérifie si l activityId existe avant d'ajouter
    public boolean activityExists(int activityId) {
        String req = "SELECT id FROM activity WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, activityId);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de l'activité : " + e.getMessage());
            return false;
        }
    }

    ///verfieé a ce que  id existe ou non id review
    public boolean reviewExists(int id) {
        String req = "SELECT COUNT(*) FROM review WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de l'existence de l'avis : " + e.getMessage());
        }
        return false;
    }
    public List<Integer> getAllActivityIds() {
        List<Integer> activityIds = new ArrayList<>();
        String req = "SELECT id FROM activity";  // Vous pouvez ajuster la requête selon votre besoin
        try (PreparedStatement pst = connection.prepareStatement(req);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                activityIds.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des IDs des activités : " + e.getMessage());
        }
        return activityIds;
    }














}
