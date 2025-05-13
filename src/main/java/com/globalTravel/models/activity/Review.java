package com.globalTravel.models.activity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Review {
    private int id;
    private String commentaire;
    private int note;
    private LocalDateTime dateReview;
    private int activityId;
    private int userId; // ID de l'utilisateur qui a écrit la review
    private String userNom; // Nom de l'utilisateur
    private String userPrenom; // Prénom de l'utilisateur

    // Constructeur avec tous les champs
    public Review(int id, String commentaire, int note, LocalDateTime dateReview, int activityId, int userId, String userNom, String userPrenom) {
        this.id = id;
        this.commentaire = commentaire;
        this.note = note;
        this.dateReview = dateReview;
        this.activityId = activityId;
        this.userId = userId;
        this.userNom = userNom;
        this.userPrenom = userPrenom;
    }

    // Constructeur sans ID (pour la création d'une nouvelle review)
    public Review(String commentaire, int note, int activityId, int userId) {
        this.commentaire = commentaire;
        this.note = note;
        this.dateReview = LocalDateTime.now(); // La date de la review est définie à maintenant
        this.activityId = activityId;
        this.userId = userId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        if (commentaire == null || commentaire.trim().isEmpty()) {
            throw new IllegalArgumentException("Le commentaire ne peut pas être vide.");
        }
        this.commentaire = commentaire;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        if (note < 0 || note > 5) {
            throw new IllegalArgumentException("La note doit être comprise entre 0 et 5.");
        }
        this.note = note;
    }

    public LocalDateTime getDateReview() {
        return dateReview;
    }

    public void setDateReview(LocalDateTime dateReview) {
        if (dateReview == null) {
            this.dateReview = LocalDateTime.now(); // Si la date est null, on utilise la date actuelle
        } else {
            this.dateReview = dateReview;
        }
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        if (activityId <= 0) {
            throw new IllegalArgumentException("L'ID de l'activité doit être supérieur à 0.");
        }
        this.activityId = activityId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID de l'utilisateur doit être supérieur à 0.");
        }
        this.userId = userId;
    }

    public String getUserNom() {
        return userNom;
    }

    public void setUserNom(String userNom) {
        this.userNom = userNom;
    }

    public String getUserPrenom() {
        return userPrenom;
    }

    public void setUserPrenom(String userPrenom) {
        this.userPrenom = userPrenom;
    }

    // Méthode equals pour comparer deux reviews
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id == review.id;
    }

    // Méthode hashCode pour générer un hash basé sur l'ID
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Méthode toString pour afficher les informations de la review
    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", commentaire='" + commentaire + '\'' +
                ", note=" + note +
                ", dateReview=" + dateReview +
                ", activityId=" + activityId +
                ", userId=" + userId +
                ", userNom='" + userNom + '\'' +
                ", userPrenom='" + userPrenom + '\'' +
                '}';
    }
}