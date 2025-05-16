package com.globalTravel.models.car;

import com.globalTravel.models.user.User;

import java.sql.Date;

public class CarReservation {
    private int id;
    private Date date;
    private TypeCarReservation status;
    private Route route;
    private Offer offer;
    private User user;

    public CarReservation(Date date, TypeCarReservation status, Route route, Offer offer, User user) {
        this.date = date;
        this.status = status;
        this.route = route;
        this.offer = offer;
        this.user = user;
    }

    public CarReservation(int id, Date date, TypeCarReservation status, Route route, Offer offer, User user) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.route = route;
        this.offer = offer;
        this.user = user;
    }
    public CarReservation( Date date, TypeCarReservation status, Route route, Offer offer) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.route = route;
        this.offer = offer;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TypeCarReservation getStatus() {
        return status;
    }

    public void setStatus(TypeCarReservation status) {
        this.status = status;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "CarReservation{" +
                "id=" + id +
                ", date=" + date +
                ", status=" + status +
                ", route=" + route +
                ", offer=" + offer +
                ", user=" + user +
                '}';
    }

}
