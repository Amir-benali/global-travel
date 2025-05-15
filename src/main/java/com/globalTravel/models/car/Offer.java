package com.globalTravel.models.car;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Offer {
    private int id;
    private String description;
    private LocalDateTime date;
    private float price;
    private Route route;
    private PrivateCar car;
    private ArrayList<String> reservedSeats = new ArrayList<>();

    public Offer(int id, String description, LocalDateTime date, float price, Route route, PrivateCar car) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.price = price;
        this.route = route;
        this.car = car;
    }

    public Offer(String description, LocalDateTime date, float price, Route route, PrivateCar car) {
        this.description = description;
        this.date = date;
        this.price = price;
        this.route = route;
        this.car = car;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public PrivateCar getCar() {
        return car;
    }

    public void setCar(PrivateCar car) {
        this.car = car;
    }

    public ArrayList<String> getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(ArrayList<String> reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", price=" + price +
                ", route=" + route +
                ", car=" + car +
                '}';
    }
}
