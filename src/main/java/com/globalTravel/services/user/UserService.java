package com.globalTravel.services.user;

import com.globalTravel.models.user.User;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {

    private Connection conn;

    public UserService() {
        conn = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(User user) {
        // Implémentez cette méthode si nécessaire
    }

    @Override
    public void modifier(User user) {
        String sql = "UPDATE user SET genre = ?, date_naissance = ?, adresse = ?, email = ?, password = ?, firstname = ?, lastname = ?, phone_number = ?, image = ?, statut = ?, roles = ? " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getGenre());
            pstmt.setDate(2, user.getDateNaissance());
            pstmt.setString(3, user.getAdresse());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getFirstName());
            pstmt.setString(7, user.getLastName());
            pstmt.setString(8, user.getPhoneNumber());
            pstmt.setString(9, user.getImage());
            pstmt.setString(10, user.getStatut());
            pstmt.setString(11, user.getRoles());
            pstmt.setInt(12, user.getId());

            pstmt.executeUpdate();
            System.out.println("Utilisateur modifié avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(User user) {
        String sql = "DELETE FROM user WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.executeUpdate();
            System.out.println("Utilisateur supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public List<User> rechercher() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("genre"),
                        rs.getDate("date_naissance"),
                        rs.getString("adresse"),
                        rs.getString("email"),
                        rs.getString("roles"),
                        rs.getString("password"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("phone_number"),
                        rs.getString("image"),
                        rs.getString("statut"),
                        ""
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
        return users;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("genre"),
                        rs.getDate("date_naissance"),
                        rs.getString("adresse"),
                        rs.getString("email"),
                        rs.getString("roles"),
                        rs.getString("password"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("phone_number"),
                        rs.getString("image"),
                        rs.getString("statut"),
                        ""
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }
        return null;
    }

    // Méthode pour vérifier si un email existe déjà dans la base de données
    public boolean emailExists(String email) {
        String sql = "SELECT id FROM user WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Retourne true si l'email existe déjà
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'email : " + e.getMessage());
            return false;
        }
    }
}