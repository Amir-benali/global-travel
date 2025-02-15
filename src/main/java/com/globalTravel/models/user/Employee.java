package com.globalTravel.models.user;

public class Employee extends User {
    private String poste;

    public Employee() {}

    public Employee(int id, String genre, java.util.Date dateNaissance, String adresse, String email, String roles,
                    String password, String firstName, String lastName, String phoneNumber, String image, String statut,
                    String poste) {
        super(id, genre, dateNaissance, adresse, email, roles, password, firstName, lastName, phoneNumber, image, statut);
        this.poste = poste;
    }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    @Override
    public String toString() {
        return super.toString() + ", poste='" + poste + '\'' + '}';
    }
}
