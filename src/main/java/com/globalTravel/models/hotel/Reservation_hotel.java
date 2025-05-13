package com.globalTravel.models.hotel;

import java.time.LocalDate;

public class Reservation_hotel {

    private int id_reservation_h;
    private LocalDate date_checkin_h;
    private LocalDate date_checkout_h;
    private int nombre_chambres_h;
    private String statut_h;
    private String moyen_Paiement_h;
    private Chambre id_chambre_j;

    // Constructeur par défaut
    public Reservation_hotel() {
    }

    // Constructeur modifié pour utiliser LocalDate
    public Reservation_hotel(int id_reservation_h, LocalDate date_checkin_h, LocalDate date_checkout_h, int nombre_chambres_h, String statut_h, String moyen_Paiement_h, Chambre id_chambre_j) {
        this.id_reservation_h = id_reservation_h;
        this.date_checkin_h = date_checkin_h;
        this.date_checkout_h = date_checkout_h;
        this.nombre_chambres_h = nombre_chambres_h;
        this.statut_h = statut_h;
        this.moyen_Paiement_h = moyen_Paiement_h;
        this.id_chambre_j = id_chambre_j;
    }

    public Reservation_hotel(LocalDate date_checkin_h, LocalDate date_checkout_h, int nombre_chambres_h, String statut_h, String moyen_Paiement_h, Chambre id_chambre_j) {
        this.date_checkin_h = date_checkin_h;
        this.date_checkout_h = date_checkout_h;
        this.nombre_chambres_h = nombre_chambres_h;
        this.statut_h = statut_h;
        this.moyen_Paiement_h = moyen_Paiement_h;
        this.id_chambre_j = id_chambre_j;
    }

    // Getters et setters
    public Chambre getid_chambre_j() {
        return id_chambre_j;
    }

    public void setid_chambre_j(Chambre id_chambre_j) {
        this.id_chambre_j = id_chambre_j;
    }

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
        return "Réservation #" + id_reservation_h +
                " - Check-in: " + date_checkin_h +
                ", Check-out: " + date_checkout_h +
                ", Statut: " + statut_h +
                ", Moyen de paiement: " + moyen_Paiement_h +
                ", Chambre: " + id_chambre_j;
    }

}