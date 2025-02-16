package com.globalTravel.models.user;

public class Admin extends User {
    private String privileges;

    public Admin() {}

    public Admin(int id, String genre, java.util.Date dateNaissance, String adresse, String email, String roles,
                 String password, String firstName, String lastName, String phoneNumber, String image, String statut,
                 String privileges) {
        super(id, genre, dateNaissance, adresse, email, roles, password, firstName, lastName, phoneNumber, image, statut);
        this.privileges = privileges;
    }

    public String getPrivileges() { return privileges; }
    public void setPrivileges(String privileges) { this.privileges = privileges; }

    @Override
    public String toString() {
        return super.toString() + ", privileges='" + privileges + '\'' + '}';
    }
}


