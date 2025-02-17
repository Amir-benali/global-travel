package com.globalTravel.services.user;

import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;
import com.globalTravel.models.user.Admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminService implements IService<Admin> {
    private Connection conn;

    public AdminService() {
        conn = DataSource.getInstance().getConnection();
    }

    @Override
    public boolean ajouter(Admin admin) {
        String sql = "INSERT INTO user (genre, date_naissance, adresse, email, roles, password, firstname, lastname, phone_number, image, statut, privileges) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getGenre());
            pstmt.setDate(2, new Date(admin.getDateNaissance().getTime()));
            pstmt.setString(3, admin.getAdresse());
            pstmt.setString(4, admin.getEmail());
            pstmt.setString(5, "Admin");
            pstmt.setString(6, admin.getPassword());
            pstmt.setString(7, admin.getFirstName());
            pstmt.setString(8, admin.getLastName());
            pstmt.setString(9, admin.getPhoneNumber());
            pstmt.setString(10, admin.getImage());
            pstmt.setString(11, admin.getStatut());
            pstmt.setString(12, admin.getPrivileges());

            pstmt.executeUpdate();
            System.out.println("Admin ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'Admin : " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Admin> rechercher() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE roles = 'Admin'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Admin admin = new Admin(
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
                        rs.getString("privileges")
                );
                admins.add(admin);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des Admins : " + e.getMessage());
        }
        return admins;
    }

    @Override
    public boolean modifier(Admin admin) {
        String sql = "UPDATE user SET genre = ?, date_naissance = ?, adresse = ?, email = ?, password = ?, firstname = ?, lastname = ?, phone_number = ?, image = ?, statut = ?, privileges = ? " +
                "WHERE id = ? AND roles = 'Admin'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getGenre());
            pstmt.setDate(2, new Date(admin.getDateNaissance().getTime()));
            pstmt.setString(3, admin.getAdresse());
            pstmt.setString(4, admin.getEmail());
            pstmt.setString(5, admin.getPassword());
            pstmt.setString(6, admin.getFirstName());
            pstmt.setString(7, admin.getLastName());
            pstmt.setString(8, admin.getPhoneNumber());
            pstmt.setString(9, admin.getImage());
            pstmt.setString(10, admin.getStatut());
            pstmt.setString(11, admin.getPrivileges());
            pstmt.setInt(12, admin.getId());

            pstmt.executeUpdate();
            System.out.println("Admin modifié avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'Admin : " + e.getMessage());
        }
        return false;
    }

    @Override
    public void supprimer(Admin admin) {
        String sql = "DELETE FROM user WHERE id = ? AND roles = 'Admin'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, admin.getId());
            pstmt.executeUpdate();
            System.out.println("Admin supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'Admin : " + e.getMessage());
        }
    }
}
