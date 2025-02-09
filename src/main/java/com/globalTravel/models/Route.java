package com.globalTravel.models;


import java.time.LocalDateTime;

public class Route {
    private int id;
    private LocalDateTime date_start;
    private LocalDateTime  date_destination;
    private String location_start;
    private String location_destination;

    public Route(int id, LocalDateTime  date_start, LocalDateTime  date_destination, String location_start, String location_destination) {
        this.id = id;
        this.date_start = date_start;
        this.date_destination = date_destination;
        this.location_start = location_start;
        this.location_destination = location_destination;
    }

    public Route(LocalDateTime  date_start, LocalDateTime  date_destination, String location_start, String location_destination) {
        this.date_start = date_start;
        this.date_destination = date_destination;
        this.location_start = location_start;
        this.location_destination = location_destination;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime  getDate_start() {
        return date_start;
    }

    public void setDate_start(LocalDateTime  date_start) {
        this.date_start = date_start;
    }

    public LocalDateTime  getDate_destination() {
        return  date_destination;
    }

    public void setDate_destination(LocalDateTime  date_destination) {
        this.date_destination = date_destination;
    }

    public String getLocation_start() {
        return location_start;
    }

    public void setLocation_start(String location_start) {
        this.location_start = location_start;
    }

    public String getLocation_destination() {
        return location_destination;
    }

    public void setLocation_destination(String location_destination) {
        this.location_destination = location_destination;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", date_start=" + date_start +
                ", date_destination=" + date_destination +
                ", location_start='" + location_start + '\'' +
                ", location_destination='" + location_destination + '\'' +
                '}';
    }
}
