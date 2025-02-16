package com.globalTravel.models.activity;

import java.util.Objects;

public class TypeActivity {
    private int id;
    private String nomEvenement;
    private String nomType;


    public TypeActivity(int id, String nomEvenement, String nomType) {
        this.id = id;
        this.nomEvenement = nomEvenement;
        this.nomType = nomType;
    }


    public TypeActivity(String nomEvenement, String nomType) {
        this.nomEvenement = nomEvenement;
        this.nomType = nomType;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomEvenement() {
        return nomEvenement;
    }

    public void setNomEvenement(String nomEvenement) {
        this.nomEvenement = nomEvenement;
    }

    public String getNomType() {
        return nomType;
    }

    public void setNomType(String nomType) {
        this.nomType = nomType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeActivity that = (TypeActivity) o;
        return Objects.equals(nomType, that.nomType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomType);
    }


    @Override
    public String toString() {
        return "TypeActivity{" +
                "id=" + id +
                ", nomEvenement='" + nomEvenement + '\'' +
                ", nomType='" + nomType + '\'' +
                '}';
    }
}
