package com.globalTravel.models;

import java.time.LocalDate;

public class reservation_hotel {

    private int id_reservation_h;
    private LocalDate date_checkin_h;
    private LocalDate date_checkout_h;
    private int nombre_chambres_h;
    private String statut_h;
    private String moyen_Paiement_h;

    // Constructeur modifié pour utiliser LocalDate
    public reservation_hotel(int id_reservation_h, LocalDate date_checkin_h, LocalDate date_checkout_h, int nombre_chambres_h, String statut_h, String moyen_Paiement_h) {
        this.id_reservation_h = id_reservation_h;
        this.date_checkin_h = date_checkin_h;
        this.date_checkout_h = date_checkout_h;
        this.nombre_chambres_h = nombre_chambres_h;
        this.statut_h = statut_h;
        this.moyen_Paiement_h = moyen_Paiement_h;
    }
    public reservation_hotel(LocalDate date_checkin_h, LocalDate date_checkout_h, int nombre_chambres_h, String statut_h, String moyen_Paiement_h) {
        this.id_reservation_h = id_reservation_h;
        this.date_checkin_h = date_checkin_h;
        this.date_checkout_h = date_checkout_h;
        this.nombre_chambres_h = nombre_chambres_h;
        this.statut_h = statut_h;
        this.moyen_Paiement_h = moyen_Paiement_h;
    }

    // Getters et setters
    public int getId_reservation_h() {
        return id_reservation_h;
    }

    public void setId_reservation_h(int id_reservation_h) {
        this.id_reservation_h = id_reservation_h;
    }

    public LocalDate getDate_checkin_h() {
        return date_checkin_h;
    }

    public void setDate_checkin_h(LocalDate date_checkin_h) {
        this.date_checkin_h = date_checkin_h;
    }

    public LocalDate getDate_checkout_h() {
        return date_checkout_h;
    }

    public void setDate_checkout_h(LocalDate date_checkout_h) {
        this.date_checkout_h = date_checkout_h;
    }

    public int getNombre_chambres_h() {
        return nombre_chambres_h;
    }

    public void setNombre_chambres_h(int nombre_chambres_h) {
        this.nombre_chambres_h = nombre_chambres_h;
    }

    public String getStatut_h() {
        return statut_h;
    }

    public void setStatut_h(String statut_h) {
        this.statut_h = statut_h;
    }

    public String getMoyen_Paiement_h() {
        return moyen_Paiement_h;
    }

    public void setMoyen_Paiement_h(String moyen_Paiement_h) {
        this.moyen_Paiement_h = moyen_Paiement_h;
    }

    @Override
    public String toString() {
        return "reservation_hotel{" +
                "id_reservation_h=" + id_reservation_h +
                ", date_checkin_h=" + date_checkin_h +
                ", date_checkout_h=" + date_checkout_h +
                ", nombre_chambres_h=" + nombre_chambres_h +
                ", statut_h='" + statut_h + '\'' +
                ", moyen_Paiement_h='" + moyen_Paiement_h + '\'' +
                '}';
    }
}
