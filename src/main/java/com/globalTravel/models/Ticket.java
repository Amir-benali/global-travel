package com.globalTravel.models;

public class Ticket {
    private int ticket_id;
    private int flight_id;
    private String seat_number;
    private TicketClass ticketClass;
    private double ticket_price;
    private TicketStatus status;
    private String booking_date;

    public Ticket(int ticket_id, int flight_id, String seat_number, TicketClass ticketClass, double ticket_price, TicketStatus status, String booking_date) {
        this.ticket_id = ticket_id;
        this.flight_id = flight_id;
        this.seat_number = seat_number;
        this.ticketClass = ticketClass;
        this.ticket_price = ticket_price;
        this.status = status;
        this.booking_date = booking_date;
    }

    public Ticket(int flight_id, String seat_number, TicketClass ticketClass, double ticket_price, TicketStatus status, String booking_date) {
        this.flight_id = flight_id;
        this.seat_number = seat_number;
        this.ticketClass = ticketClass;
        this.ticket_price = ticket_price;
        this.status = status;
        this.booking_date = booking_date;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticket_id=" + ticket_id +
                ", flight_id=" + flight_id +
                ", seat_number='" + seat_number + '\'' +
                ", ticketClass=" + ticketClass +
                ", ticket_price=" + ticket_price +
                ", status=" + status +
                ", booking_date='" + booking_date + '\'' +
                '}';
    }

    public int getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(int ticket_id) {
        this.ticket_id = ticket_id;
    }

    public int getFlight_id() {
        return flight_id;
    }

    public void setFlight_id(int flight_id) {
        this.flight_id = flight_id;
    }

    public String getSeat_number() {
        return seat_number;
    }

    public void setSeat_number(String seat_number) {
        this.seat_number = seat_number;
    }

    public TicketClass getTicketClass() {
        return ticketClass;
    }

    public void setTicketClass(TicketClass ticketClass) {
        this.ticketClass = ticketClass;
    }

    public double getTicket_price() {
        return ticket_price;
    }

    public void setTicket_price(double ticket_price) {
        this.ticket_price = ticket_price;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }
}
