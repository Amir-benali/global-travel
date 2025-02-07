package com.globalTravel.services;

import com.globalTravel.utils.DataSource;
import com.globalTravel.models.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService implements IService<Employee> {
    private Connection conn;

    public EmployeeService() {
        conn = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Employee employee) {
        String sql = "INSERT INTO user (genre, date_naissance, adresse, email, roles, password, firstname, lastname, phone_number, image, statut, poste) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employee.getGenre());
            pstmt.setDate(2, new Date(employee.getDateNaissance().getTime()));
            pstmt.setString(3, employee.getAdresse());
            pstmt.setString(4, employee.getEmail());
            pstmt.setString(5, "Employee");
            pstmt.setString(6, employee.getPassword());
            pstmt.setString(7, employee.getFirstName());
            pstmt.setString(8, employee.getLastName());
            pstmt.setString(9, employee.getPhoneNumber());
            pstmt.setString(10, employee.getImage());
            pstmt.setString(11, employee.getStatut());
            pstmt.setString(12, employee.getPoste());

            pstmt.executeUpdate();
            System.out.println("Employee ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'Employee : " + e.getMessage());
        }
    }

    @Override
    public List<Employee> rechercher() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE roles = 'Employee'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Employee employee = new Employee(
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
                        rs.getString("poste")
                );
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des Employees : " + e.getMessage());
        }
        return employees;
    }

    @Override
    public void modifier(Employee employee) {
        String sql = "UPDATE user SET genre = ?, date_naissance = ?, adresse = ?, email = ?, password = ?, firstname = ?, lastname = ?, phone_number = ?, image = ?, statut = ?, poste = ? " +
                "WHERE id = ? AND roles = 'Employee'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employee.getGenre());
            pstmt.setDate(2, new Date(employee.getDateNaissance().getTime()));
            pstmt.setString(3, employee.getAdresse());
            pstmt.setString(4, employee.getEmail());
            pstmt.setString(5, employee.getPassword());
            pstmt.setString(6, employee.getFirstName());
            pstmt.setString(7, employee.getLastName());
            pstmt.setString(8, employee.getPhoneNumber());
            pstmt.setString(9, employee.getImage());
            pstmt.setString(10, employee.getStatut());
            pstmt.setString(11, employee.getPoste());
            pstmt.setInt(12, employee.getId());

            pstmt.executeUpdate();
            System.out.println("Employee modifié avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'Employee : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Employee employee) {
        String sql = "DELETE FROM user WHERE id = ? AND roles = 'Employee'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, employee.getId());
            pstmt.executeUpdate();
            System.out.println("Employee supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'Employee : " + e.getMessage());
        }
    }
}
