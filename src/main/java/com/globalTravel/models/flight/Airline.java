package com.globalTravel.models.flight;

public class Airline {
    private int airline_id;
    private String airline_name;
    private String airline_code;
    private String country;

    public Airline(int airline_id, String airline_name, String airline_code, String country) {
        this.airline_id = airline_id;
        this.airline_name = airline_name;
        this.airline_code = airline_code;
        this.country = country;
    }

    public Airline(String country, String airline_code, String airline_name) {
        this.country = country;
        this.airline_code = airline_code;
        this.airline_name = airline_name;
    }

    public int getAirline_id() {
        return airline_id;
    }

    public void setAirline_id(int airline_id) {
        this.airline_id = airline_id;
    }

    public String getAirline_name() {
        return airline_name;
    }

    public void setAirline_name(String airline_name) {
        this.airline_name = airline_name;
    }

    public String getAirline_code() {
        return airline_code;
    }

    public void setAirline_code(String airline_code) {
        this.airline_code = airline_code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Airline{" +
                "airline_id=" + airline_id +
                ", airline_name='" + airline_name + '\'' +
                ", airline_code='" + airline_code + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
