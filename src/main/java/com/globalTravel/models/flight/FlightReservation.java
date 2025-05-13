package com.globalTravel.models.flight;

import com.globalTravel.models.user.User;
import java.sql.Date;

public class FlightReservation {
    private int id;
    private Date bookingDate;
    private String status;
    private Flight flight;
    private User user;

    public FlightReservation(Date bookingDate, String status, Flight flight, User user) {
        this.bookingDate = bookingDate;
        this.status = status;
        this.flight = flight;
        this.user = user;
    }

    public FlightReservation(int id, Date bookingDate, String status, Flight flight, User user) {
        this.id = id;
        this.bookingDate = bookingDate;
        this.status = status;
        this.flight = flight;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "FlightReservation{" +
                "id=" + id +
                ", bookingDate=" + bookingDate +
                ", status='" + status + '\'' +
                ", flight=" + flight +
                ", user=" + user +
                '}';
    }
}