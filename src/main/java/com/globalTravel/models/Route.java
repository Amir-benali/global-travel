package com.globalTravel.models;

import java.util.Date;

public class Route {
    private int id;
    private Date date_start;
    private Date date_destination;
    private String location_start;
    private String location_destination;

    public Route(int id, Date date_start, Date date_destination, String location_start, String location_destination) {
        this.id = id;
        this.date_start = date_start;
        this.date_destination = date_destination;
        this.location_start = location_start;
        this.location_destination = location_destination;
    }

    public Route(Date date_start, Date date_destination, String location_start, String location_destination) {
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

    public Date getDate_start() {
        return date_start;
    }

    public void setDate_start(Date date_start) {
        this.date_start = date_start;
    }

    public Date getDate_destination() {
        return date_destination;
    }

    public void setDate_destination(Date date_destination) {
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
