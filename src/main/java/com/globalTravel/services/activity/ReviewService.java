package com.globalTravel.services.activity;



import com.globalTravel.models.activity.Review;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewService implements IService<Review> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(Review review) {
        String req = "INSERT INTO review (commentaire, note, dateReview, reservationDecision) VALUES (?, ?, NOW(), ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, review.getCommentaire());
            pst.setInt(2, review.getNote());
            pst.setString(3, review.getReservationDecision());
            pst.executeUpdate();
            System.out.println("Avis ajouté avec succès ");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'avis : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Review review) {
        String req = "UPDATE review SET commentaire=?, note=?, reservationDecision=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, review.getCommentaire());
            pst.setInt(2, review.getNote());
            pst.setString(3, review.getReservationDecision());
            pst.setInt(4, review.getId());
            pst.executeUpdate();
            System.out.println(" modifié avec succes ");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l avis : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Review review) {
        String req = "DELETE FROM review WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, review.getId());
            pst.executeUpdate();
            System.out.println("Avis supprimé avec succès ");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'avis : " + e.getMessage());
        }
    }

    @Override
    public List<Review> rechercher() {
        List<Review> reviews = new ArrayList<>();
        String req = "SELECT * FROM review";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("id"),
                        rs.getString("commentaire"),
                        rs.getInt("note"),
                        rs.getTimestamp("dateReview").toLocalDateTime(),
                        rs.getString("reservationDecision")
                );
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des avis : " + e.getMessage());
        }

        return reviews;
    }
}
