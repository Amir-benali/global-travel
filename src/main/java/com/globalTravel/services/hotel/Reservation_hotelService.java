package com.globalTravel.services.hotel;

import com.globalTravel.models.hotel.Reservation_hotel;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Reservation_hotelService implements IService<Reservation_hotel> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(Reservation_hotel reservationHotel) {
        String req = "INSERT INTO reservation_hotel (date_checkin_h, date_checkout_h, nombre_chambres_h, statut_h, moyen_Paiement_h, id_chambre_j) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setDate(1, Date.valueOf(reservationHotel.getDate_checkin_h()));
            pst.setDate(2, Date.valueOf(reservationHotel.getDate_checkout_h()));
            pst.setInt(3, reservationHotel.getNombre_chambres_h());
            pst.setString(4, reservationHotel.getStatut_h());
            pst.setString(5, reservationHotel.getMoyen_Paiement_h());
            pst.setInt(6, reservationHotel.getid_chambre_j().getId_Chambre_h());
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Réservation ajoutée avec succès");
            } else {
                System.out.println("Aucune ligne n'a été affectée par l'insertion");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la réservation : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Reservation_hotel reservationHotel) {
        String req = "UPDATE reservation_hotel SET date_checkin_h = ?, date_checkout_h = ?, nombre_chambres_h = ?, statut_h = ?, moyen_Paiement_h = ?, id_chambre_j = ? WHERE id_reservation_h = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setDate(1, Date.valueOf(reservationHotel.getDate_checkin_h()));
            pst.setDate(2, Date.valueOf(reservationHotel.getDate_checkout_h()));
            pst.setInt(3, reservationHotel.getNombre_chambres_h());
            pst.setString(4, reservationHotel.getStatut_h());
            pst.setString(5, reservationHotel.getMoyen_Paiement_h());
            pst.setInt(6, reservationHotel.getid_chambre_j().getId_Chambre_h());
            pst.setInt(7, reservationHotel.getId_reservation_h());
            pst.executeUpdate();
            System.out.println("Réservation modifiée");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de la réservation : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Reservation_hotel reservationHotel) {
        String req = "DELETE FROM reservation_hotel WHERE id_reservation_h = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, reservationHotel.getId_reservation_h());
            pst.executeUpdate();
            System.out.println("Réservation supprimée");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la réservation : " + e.getMessage());
        }
    }

    @Override
    public List<Reservation_hotel> rechercher() {
        List<Reservation_hotel> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation_hotel";
        try (PreparedStatement pst = connection.prepareStatement(req);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                reservations.add(new Reservation_hotel(
                        rs.getInt("id_reservation_h"),
                        rs.getDate("date_checkin_h").toLocalDate(),
                        rs.getDate("date_checkout_h").toLocalDate(),
                        rs.getInt("nombre_chambres_h"),
                        rs.getString("statut_h"),
                        rs.getString("moyen_Paiement_h"),
                        new ChambreService().getChambreById(rs.getInt("id_chambre_j"))
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche des réservations : " + e.getMessage());
        }
        return reservations;
    }
}