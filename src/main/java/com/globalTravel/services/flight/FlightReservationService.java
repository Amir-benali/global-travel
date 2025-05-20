package com.globalTravel.services.flight;

        import com.globalTravel.models.flight.FlightReservation;
        import com.globalTravel.services.user.UserService;
        import com.globalTravel.utils.DataSource;
        import java.sql.*;
        import java.util.ArrayList;
        import java.util.List;

        public class FlightReservationService {
            private Connection connection = DataSource.getInstance().getConnection();
            private FlightService flightService = new FlightService();
            private UserService userService = new UserService();

            public int ajouter(FlightReservation reservation) {
                String query = "INSERT INTO flight_reservations (booking_date, status, flight_id, user_id,seat) VALUES (?, ?, ?, ?,?)";
                try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    pst.setDate(1, reservation.getBookingDate());
                    pst.setString(2, reservation.getStatus());
                    pst.setInt(3, reservation.getFlight().getId_flight());
                    pst.setInt(4, reservation.getUser());
                    pst.setString(5, reservation.getSeat());
                    pst.executeUpdate();
                    ResultSet rs = pst.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return -1;
            }

            public void modifier(FlightReservation reservation) {
                String query = "UPDATE flight_reservations SET booking_date = ?, status = ?, flight_id = ?, user_id = ?, seat = ? WHERE id = ?";
                try (PreparedStatement pst = connection.prepareStatement(query)) {
                    pst.setDate(1, reservation.getBookingDate());
                    pst.setString(2, reservation.getStatus());
                    pst.setInt(3, reservation.getFlight().getId_flight());
                    pst.setInt(4, reservation.getUser());
                    pst.setInt(5, reservation.getId());
                    pst.setString(6, reservation.getSeat());
                    pst.executeUpdate();
                    System.out.println("Flight reservation has been modified");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            public void supprimer(FlightReservation reservation) {
                String query = "DELETE FROM flight_reservations WHERE id = ?";
                try (PreparedStatement pst = connection.prepareStatement(query)) {
                    pst.setInt(1, reservation.getId());
                    pst.executeUpdate();
                    System.out.println("Flight reservation has been deleted");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            public List<FlightReservation> rechercher() {
                List<FlightReservation> reservations = new ArrayList<>();
                String query = "SELECT * FROM flight_reservations";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        FlightReservation reservation = new FlightReservation(
                            rs.getInt("id"),
                            rs.getDate("booking_date"),
                            rs.getString("status"),
                            flightService.getFlightById(rs.getInt("flight_id")),
                            rs.getString("seat"),
                                rs.getInt("user_id")

                        );
                        reservations.add(reservation);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return reservations;
            }

            public void updateStatus(FlightReservation reservation, String status) {
                String query = "UPDATE flight_reservations SET status = ? WHERE id = ?";
                try (PreparedStatement pst = connection.prepareStatement(query)) {
                    pst.setString(1, status);
                    pst.setInt(2, reservation.getId());
                    pst.executeUpdate();
                    System.out.println("Flight reservation status has been updated");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
