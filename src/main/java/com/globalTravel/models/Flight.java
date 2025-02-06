package globaltravel.modules.FlightModule.models;

public class Flight {
    private int id_flight;
    private String flight_number;
    private int airline_id;
    private String departure_airport;
    private String arrival_airport;
    private String departure_time;
    private String arrival_time;
    private int duration;
    private int available_seats;
    private double base_price;
    private FlightStatus status;

    public Flight(int id_flight, String flight_number, int airline_id, String departure_airport, String arrival_airport, String departure_time, String arrival_time, int duration, int available_seats, double base_price, FlightStatus status) {
        this.id_flight = id_flight;
        this.flight_number = flight_number;
        this.airline_id = airline_id;
        this.departure_airport = departure_airport;
        this.arrival_airport = arrival_airport;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
        this.duration = duration;
        this.available_seats = available_seats;
        this.base_price = base_price;
        this.status = status;
    }

    public Flight(String flight_number, int airline_id, String departure_airport, String arrival_airport, String departure_time, String arrival_time, int duration, int available_seats, double base_price, FlightStatus status) {
        this.flight_number = flight_number;
        this.airline_id = airline_id;
        this.departure_airport = departure_airport;
        this.arrival_airport = arrival_airport;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
        this.duration = duration;
        this.available_seats = available_seats;
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

    public int getAirline_id() {
        return airline_id;
    }

    public void setAirline_id(int airline_id) {
        this.airline_id = airline_id;
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

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAvailable_seats() {
        return available_seats;
    }

    public void setAvailable_seats(int available_seats) {
        this.available_seats = available_seats;
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
                ", airline_id=" + airline_id +
                ", departure_airport='" + departure_airport + '\'' +
                ", arrival_airport='" + arrival_airport + '\'' +
                ", departure_time='" + departure_time + '\'' +
                ", arrival_time='" + arrival_time + '\'' +
                ", duration=" + duration +
                ", available_seats=" + available_seats +
                ", base_price=" + base_price +
                ", status=" + status +
                '}';
    }
}
