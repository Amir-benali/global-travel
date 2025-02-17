package com.globalTravel.services.hotel;



import com.globalTravel.models.hotel.reservation_hotel;
import com.globalTravel.services.IService;
import com.globalTravel.utils.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class reservation_hotelService implements IService<reservation_hotel> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public boolean ajouter(reservation_hotel reservationHotel) {
        String req = "INSERT INTO reservation_hotel (date_checkin_h, date_checkout_h, nombre_chambres_h, statut_h, moyen_Paiement_h) VALUES ('" +
                reservationHotel.getDate_checkin_h() + "','" +
                reservationHotel.getDate_checkout_h() + "'," +
                reservationHotel.getNombre_chambres_h() + ",'" +
                reservationHotel.getStatut_h() + "','" +
                reservationHotel.getMoyen_Paiement_h() + "')";
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Réservation ajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean modifier(reservation_hotel reservationHotel) {
        String req = "UPDATE reservation_hotel SET date_checkin_h='" + reservationHotel.getDate_checkin_h() + "', " +
                "date_checkout_h='" + reservationHotel.getDate_checkout_h() + "', " +
                "nombre_chambres_h=" + reservationHotel.getNombre_chambres_h() + ", " +
                "statut_h='" + reservationHotel.getStatut_h() + "', " +
                "moyen_Paiement_h='" + reservationHotel.getMoyen_Paiement_h() + "' " +
                "WHERE id_reservation_h=" + reservationHotel.getId_reservation_h();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Réservation modifiée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public void supprimer(reservation_hotel reservationHotel) {
        String req = "DELETE FROM reservation_hotel WHERE id_reservation_h=" + reservationHotel.getId_reservation_h();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Réservation supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<reservation_hotel> rechercher() {
        List<reservation_hotel> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation_hotel";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                reservations.add(new reservation_hotel(
                        rs.getInt("id_reservation_h"),
                        rs.getDate("date_checkin_h").toLocalDate(),
                        rs.getDate("date_checkout_h").toLocalDate(),
                        rs.getInt("nombre_chambres_h"),
                        rs.getString("statut_h"),
                        rs.getString("moyen_Paiement_h")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reservations;
    }
}
