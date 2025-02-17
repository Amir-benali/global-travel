package com.globalTravel.models.hotel;

import java.time.LocalDate;

public class Chambre {

    private int id_Chambre_h;
    private String type_chambre_h;
    private int prix_nuit_h;
    private LocalDate dispo_h;
    private String option_h;
    private Hotel id_hotel_j;

    // Constructeur
    public Chambre(int id_Chambre_h, String type_chambre_h, int prix_nuit_h, LocalDate dispo_h, String option_h, Hotel id_hotel_j) {
        this.id_Chambre_h = id_Chambre_h;
        this.type_chambre_h = type_chambre_h;
        this.prix_nuit_h = prix_nuit_h;
        this.dispo_h = dispo_h;
        this.option_h = option_h;
        this.id_hotel_j = id_hotel_j;
    }

    // Constructeur
    public Chambre(String type_chambre_h, int prix_nuit_h, LocalDate dispo_h, String option_h, Hotel id_hotel_j) {
        this.type_chambre_h = type_chambre_h;
        this.prix_nuit_h = prix_nuit_h;
        this.dispo_h = dispo_h;
        this.option_h = option_h;
        this.id_hotel_j = id_hotel_j;
    }

    // Getters et Setters
    public int getId_Chambre_h() {
        return id_Chambre_h;
    }

    public void setId_Chambre_h(int id_Chambre_h) {
        this.id_Chambre_h = id_Chambre_h;
    }

    public Hotel getid_hotel_j() {
        return id_hotel_j;
    }

    public void setid_hotel_j(Hotel id_hotel_j) {
        this.id_hotel_j = id_hotel_j;
    }

    public String getType_chambre_h() {
        return type_chambre_h;
    }

    public void setType_chambre_h(String type_chambre_h) {
        this.type_chambre_h = type_chambre_h;
    }

    public int getPrix_nuit_h() {
        return prix_nuit_h;
    }

    public void setPrix_nuit_h(int prix_nuit_h) {
        this.prix_nuit_h = prix_nuit_h;
    }

    public LocalDate getDispo_h() {
        return dispo_h;
    }

    public void setDispo_h(LocalDate dispo_h) {
        this.dispo_h = dispo_h;
    }

    public String getOption_h() {
        return option_h;
    }

    public void setOption_h(String option_h) {
        this.option_h = option_h;
    }

    @Override
    public String toString() {
        return "Chambre{" +
                "id_Chambre_h=" + id_Chambre_h +
                ", type_chambre_h='" + type_chambre_h + '\'' +
                ", prix_nuit_h=" + prix_nuit_h +
                ", dispo_h='" + dispo_h + '\'' +
                ", option_h='" + option_h + '\'' +
                ", id_hotel_h='" + id_hotel_j + '\'' +
                '}';
    }
}