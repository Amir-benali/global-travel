package com.globalTravel.models.hotel;




public class Hotel {

    private int id_hotel_h;
    private String nom_h;
    private String adresse_h;
    private String ville_h;
    private String pays_h;
    private int categorie_h;
    private String services_h;
    private String coordonnees_h;
    private String avis_h;

    // Constructeur avec l'ID
    public Hotel(int id_hotel_h, String nom_h, String adresse_h, String ville_h, String paysH, int categorieH, String servicesH, String coordonneesH, String avisH) {
        this.id_hotel_h = id_hotel_h;
        this.nom_h = nom_h;
        this.adresse_h = adresse_h;
        this.ville_h = ville_h;
        this.pays_h = paysH;
        this.categorie_h = categorieH;
        this.services_h = servicesH;
        this.coordonnees_h = coordonneesH;
        this.avis_h = avisH;
    }

    // Constructeur sans l'ID (pour insertion)
    public Hotel(String nom_h, String adresse_h, String ville_h, String pays_h, int categorie_h, String services_h, String coordonnees_h, String avis_h) {
        this.nom_h = nom_h;
        this.adresse_h = adresse_h;
        this.ville_h = ville_h;
        this.pays_h = pays_h;
        this.categorie_h = categorie_h;
        this.services_h = services_h;
        this.coordonnees_h = coordonnees_h;
        this.avis_h = avis_h;
    }

    public int getId_hotel_h() {
        return id_hotel_h;
    }


    public void setId_hotel_h(int id_hotel_h) {
        this.id_hotel_h = id_hotel_h;
    }

    public String getNom_h() {
        return nom_h;
    }

    public void setNom_h(String nom_h) {
        this.nom_h = nom_h;
    }

    public String getAdresse_h() {
        return adresse_h;
    }

    public void setAdresse_h(String adresse_h) {
        this.adresse_h = adresse_h;
    }

    public String getVille_h() {
        return ville_h;
    }

    public void setVille_h(String ville_h) {
        this.ville_h = ville_h;
    }

    public String getPays_h() {
        return pays_h;
    }

    public void setPays_h(String pays_h) {
        this.pays_h = pays_h;
    }

    public int getCategorie_h() {
        return categorie_h;
    }

    public void setCategorie_h(int categorie_h) {
        this.categorie_h = categorie_h;
    }

    public String getServices_h() {
        return services_h;
    }

    public void setServices_h(String services_h) {
        this.services_h = services_h;
    }

    public String getCoordonnees_h() {
        return coordonnees_h;
    }

    public void setCoordonnees_h(String coordonnees_h) {
        this.coordonnees_h = coordonnees_h;
    }

    public String getAvis_h() {
        return avis_h;
    }

    public void setAvis_h(String avis_h) {
        this.avis_h = avis_h;
    }

    @Override
    public String toString() {
        return "Hôtel #" + id_hotel_h + " - " + nom_h;
    }
}
