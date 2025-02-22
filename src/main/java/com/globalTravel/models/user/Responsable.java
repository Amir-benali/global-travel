package com.globalTravel.models.user;

import java.sql.Date;

public class Responsable extends User {
    private String departement;

    public Responsable() {}

    public Responsable(int id, String genre, Date dateNaissance, String adresse, String email, String roles,
                       String password, String firstName, String lastName, String phoneNumber, String image, String statut,
                       String departement) {
        super(id, genre, dateNaissance, adresse, email, roles, password, firstName, lastName, phoneNumber, image, statut);
        this.departement = departement;
    }

    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }

    @Override
    public String toString() {
        return super.toString() + ", departement='" + departement + '\'' + '}';
    }
}
