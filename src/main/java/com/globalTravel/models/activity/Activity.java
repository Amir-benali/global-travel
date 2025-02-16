package com.globalTravel.models.activity;

import java.util.Date;

public class Activity {
    private int id;
    private Date dateDebut;
    private Date dateFin;
    private String description;
    private String localisation;
    private boolean notification;
    private int prixTotal;
    private boolean hotelInclus;
    private boolean volInclus;
    private boolean voitureIncluse;

    public Activity(int id, Date dateDebut, Date dateFin, String description, String localisation,
                    boolean notification, int prixTotal, boolean hotelInclus, boolean volInclus, boolean voitureIncluse) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.localisation = localisation;
        this.notification = notification;
        this.prixTotal = prixTotal;
        this.hotelInclus = hotelInclus;
        this.volInclus = volInclus;
        this.voitureIncluse = voitureIncluse;
    }

    public Activity(Date dateDebut, Date dateFin, String description, String localisation,
                    boolean notification, int prixTotal, boolean hotelInclus, boolean volInclus, boolean voitureIncluse) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.localisation = localisation;
        this.notification = notification;
        this.prixTotal = prixTotal;
        this.hotelInclus = hotelInclus;
        this.volInclus = volInclus;
        this.voitureIncluse = voitureIncluse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public int getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(int prixTotal) {
        this.prixTotal = prixTotal;
    }

    public boolean isHotelInclus() {
        return hotelInclus;
    }

    public void setHotelInclus(boolean hotelInclus) {
        this.hotelInclus = hotelInclus;
    }

    public boolean isVolInclus() {
        return volInclus;
    }

    public void setVolInclus(boolean volInclus) {
        this.volInclus = volInclus;
    }

    public boolean isVoitureIncluse() {
        return voitureIncluse;
    }

    public void setVoitureIncluse(boolean voitureIncluse) {
        this.voitureIncluse = voitureIncluse;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", description='" + description + '\'' +
                ", localisation='" + localisation + '\'' +
                ", notification=" + notification +
                ", prixTotal=" + prixTotal +
                ", hotelInclus=" + hotelInclus +
                ", volInclus=" + volInclus +
                ", voitureIncluse=" + voitureIncluse +
                '}';
    }
}
