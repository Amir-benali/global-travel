package com.globalTravel.services.user;

import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;
import com.globalTravel.models.user.Responsable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResponsableService implements IService<Responsable> {
    private Connection conn;

    public ResponsableService() {
        conn = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Responsable responsable) {
        String sql = "INSERT INTO user (genre, date_naissance, adresse, email, roles, password, firstname, lastname, phone_number, image, statut, departement) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, responsable.getGenre());
            pstmt.setDate(2, new Date(responsable.getDateNaissance().getTime()));
            pstmt.setString(3, responsable.getAdresse());
            pstmt.setString(4, responsable.getEmail());
            pstmt.setString(5, "Responsable");
            pstmt.setString(6, responsable.getPassword());
            pstmt.setString(7, responsable.getFirstName());
            pstmt.setString(8, responsable.getLastName());
            pstmt.setString(9, responsable.getPhoneNumber());
            pstmt.setString(10, responsable.getImage());
            pstmt.setString(11, responsable.getStatut());
            pstmt.setString(12, responsable.getDepartement());

            pstmt.executeUpdate();
            System.out.println("Responsable ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du Responsable : " + e.getMessage());
        }
    }

    @Override
    public List<Responsable> rechercher() {
        List<Responsable> responsables = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE roles = 'Responsable'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Responsable responsable = new Responsable(
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
                        rs.getString("departement")
                );
                responsables.add(responsable);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des Responsables : " + e.getMessage());
        }
        return responsables;
    }

    @Override
    public void modifier(Responsable responsable) {
        String sql = "UPDATE user SET genre = ?, date_naissance = ?, adresse = ?, email = ?, password = ?, firstname = ?, lastname = ?, phone_number = ?, image = ?, statut = ?, departement = ? " +
                "WHERE id = ? AND roles = 'Responsable'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, responsable.getGenre());
            pstmt.setDate(2, new Date(responsable.getDateNaissance().getTime()));
            pstmt.setString(3, responsable.getAdresse());
            pstmt.setString(4, responsable.getEmail());
            pstmt.setString(5, responsable.getPassword());
            pstmt.setString(6, responsable.getFirstName());
            pstmt.setString(7, responsable.getLastName());
            pstmt.setString(8, responsable.getPhoneNumber());
            pstmt.setString(9, responsable.getImage());
            pstmt.setString(10, responsable.getStatut());
            pstmt.setString(11, responsable.getDepartement());
            pstmt.setInt(12, responsable.getId());

            pstmt.executeUpdate();
            System.out.println("Responsable modifié avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du Responsable : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Responsable responsable) {
        String sql = "DELETE FROM user WHERE id = ? AND roles = 'Responsable'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, responsable.getId());
            pstmt.executeUpdate();
            System.out.println("Responsable supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du Responsable : " + e.getMessage());
        }
    }
}
