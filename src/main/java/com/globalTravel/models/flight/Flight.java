package com.globalTravel.models.flight;

        import java.sql.Timestamp;

        public class Flight {
            private int id_flight;
            private String flight_number;
            private String airline_name;
            private String departure_country;
            private String arrival_country;
            private String departure_airport;
            private String arrival_airport;
            private Timestamp departure_time;
            private Timestamp arrival_time;
            private int duration;
            private int available_seats;
            private double base_price;
            private FlightStatus status;

            public Flight(int id_flight, String flight_number, String airline_name, String departure_country, String arrival_country, String departure_airport, String arrival_airport, Timestamp departure_time, Timestamp arrival_time, int duration, int available_seats, double base_price, FlightStatus status) {
                this.id_flight = id_flight;
                this.flight_number = flight_number;
                this.airline_name = airline_name;
                this.departure_country = departure_country;
                this.arrival_country = arrival_country;
                this.departure_airport = departure_airport;
                this.arrival_airport = arrival_airport;
                this.departure_time = departure_time;
                this.arrival_time = arrival_time;
                this.duration = duration;
                this.available_seats = available_seats;
                this.base_price = base_price;
                this.status = status;
            }

            public Flight(String flight_number, String airline_name, String departure_country, String arrival_country, String departure_airport, String arrival_airport, Timestamp departure_time, Timestamp arrival_time, int duration, int available_seats, double base_price, FlightStatus status) {
                this.flight_number = flight_number;
                this.airline_name = airline_name;
                this.departure_country = departure_country;
                this.arrival_country = arrival_country;
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

            public String getAirline_name() {
                return airline_name;
            }

            public void setAirline_name(String airline_name) {
                this.airline_name = airline_name;
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
                        ", airline_name='" + airline_name + '\'' +
                        ", departure_country='" + departure_country + '\'' +
                        ", arrival_country='" + arrival_country + '\'' +
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