package com.globalTravel.models.flight;


                import java.sql.Timestamp;
                import java.util.List;

                public class Flight {
                    private int id_flight;
                    private String flight_number;
                    private int airlineId;
                    private String departure_country;
                    private String arrival_country;
                    private String departure_airport;
                    private String arrival_airport;
                    private Timestamp departure_time;
                    private Timestamp arrival_time;
                    private int seatsNumber;
                    private List<String> availableSeats;
                    private List<String> unavailableSeats;
                    private int duration;
                    private double base_price;
                    private FlightStatus status;

                    public Flight(int id_flight, String flight_number, int airlineId, String departure_country, String arrival_country, String departure_airport, String arrival_airport, Timestamp departure_time, Timestamp arrival_time, int duration, List<String> availableSeats,List<String> unavailableSeats,int seatsNumber , double base_price, FlightStatus status) {
                        this.id_flight = id_flight;
                        this.flight_number = flight_number;
                        this.airlineId = airlineId;
                        this.departure_country = departure_country;
                        this.arrival_country = arrival_country;
                        this.departure_airport = departure_airport;
                        this.arrival_airport = arrival_airport;
                        this.departure_time = departure_time;
                        this.arrival_time = arrival_time;
                        this.duration = duration;
                        this.availableSeats = availableSeats;
                        this.unavailableSeats = unavailableSeats;
                        this.seatsNumber = seatsNumber;
                        this.base_price = base_price;
                        this.status = status;
                    }

                    public Flight(String flight_number, int airlineId, String departure_country, String arrival_country, String departure_airport, String arrival_airport, Timestamp departure_time, Timestamp arrival_time, int duration, List<String> availableSeats,List<String> unavailableSeats,int seatsNumber , double base_price, FlightStatus status) {
                        this.flight_number = flight_number;
                        this.airlineId = airlineId;
                        this.departure_country = departure_country;
                        this.arrival_country = arrival_country;
                        this.departure_airport = departure_airport;
                        this.arrival_airport = arrival_airport;
                        this.departure_time = departure_time;
                        this.arrival_time = arrival_time;
                        this.duration = duration;
                        this.availableSeats = availableSeats;
                        this.unavailableSeats = unavailableSeats;
                        this.seatsNumber = seatsNumber;
                        this.base_price = base_price;
                        this.status = status;
                    }

                    public int getId_flight() {
                        return id_flight;
                    }

                    public void setId_flight(int id_flight) {
                        this.id_flight = id_flight;
                    }

                    public String getFlight_number() {
                        return flight_number;
                    }

                    public void setFlight_number(String flight_number) {
                        this.flight_number = flight_number;
                    }

                    public int getAirlineId() {
                        return airlineId;
                    }

                    public void setAirlineId(int airlineId) {
                        this.airlineId = airlineId;
                    }

                    public String getDeparture_country() {
                        return departure_country;
                    }

                    public void setDeparture_country(String departure_country) {
                        this.departure_country = departure_country;
                    }

                    public String getArrival_country() {
                        return arrival_country;
                    }

                    public void setArrival_country(String arrival_country) {
                        this.arrival_country = arrival_country;
                    }

                    public String getDeparture_airport() {
                        return departure_airport;
                    }

                    public void setDeparture_airport(String departure_airport) {
                        this.departure_airport = departure_airport;
                    }

                    public String getArrival_airport() {
                        return arrival_airport;
                    }

                    public void setArrival_airport(String arrival_airport) {
                        this.arrival_airport = arrival_airport;
                    }

                    public Timestamp getDeparture_time() {
                        return departure_time;
                    }

                    public void setDeparture_time(Timestamp departure_time) {
                        this.departure_time = departure_time;
                    }

                    public Timestamp getArrival_time() {
                        return arrival_time;
                    }

                    public void setArrival_time(Timestamp arrival_time) {
                        this.arrival_time = arrival_time;
                    }

                    public int getDuration() {
                        return duration;
                    }

                    public void setDuration(int duration) {
                        this.duration = duration;
                    }

                    public List<String> getAvailableSeats(){
                        return availableSeats;
                    }

                    public void setAvailableSeats(List<String> availableSeats){
                        this.availableSeats = availableSeats;
                    }

                    public List<String> getUnavailableSeats() {
                        return unavailableSeats;
                    }

                    public void setUnavailableSeats(List<String> unavailableSeats) {
                        this.unavailableSeats = unavailableSeats;
                    }

                    public int getSeatsNumber() {
                        return seatsNumber;
                    }

                    public void setSeatsNumber(int seatsNumber) {
                        this.seatsNumber = seatsNumber;
                    }

                    public double getBase_price() {
                        return base_price;
                    }

                    public void setBase_price(double base_price) {
                        this.base_price = base_price;
                    }

                    public FlightStatus getStatus() {
                        return status;
                    }

                    public void setStatus(FlightStatus status) {
                        this.status = status;
                    }

                    @Override
                    public String toString() {
                        return "Flight{" +
                                "id_flight=" + id_flight +
                                ", flight_number='" + flight_number + '\'' +
                                ", airlineId=" + airlineId +
                                ", departure_country='" + departure_country + '\'' +
                                ", arrival_country='" + arrival_country + '\'' +
                                ", departure_airport='" + departure_airport + '\'' +
                                ", arrival_airport='" + arrival_airport + '\'' +
                                ", departure_time=" + departure_time +
                                ", arrival_time=" + arrival_time +
                                ", duration=" + duration +
                                ", availableSeats=" + availableSeats +
                                ", unavailableSeats=" + unavailableSeats +
                                "seatsNumber=" + seatsNumber +
                                ", base_price=" + base_price +
                                ", status=" + status +
                                '}';
                    }
                }