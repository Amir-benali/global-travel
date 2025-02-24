package com.globalTravel.models.activity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Review {
    private int id;
    private String commentaire;
    private int note;
    private LocalDateTime dateReview;
    private int activityId;


    public Review(int id, String commentaire, int note, LocalDateTime dateReview, int activityId) {
        this.id = id;
        setCommentaire(commentaire);
        setNote(note);
        setDateReview(dateReview);
        setActivityId(activityId);
    }

    public Review(String commentaire, int note, int activityId) {
        setCommentaire(commentaire);
        setNote(note);
        this.dateReview = LocalDateTime.now();
        setActivityId(activityId);
    }


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
            throw new IllegalArgumentException("Le commentaire ne pas etre vide.");
        }
        this.commentaire = commentaire;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        if (note < 0|| note > 5) {
            throw new IllegalArgumentException("La note  comprise entre 0 et 5.");
        }
        this.note = note;
    }

    public LocalDateTime getDateReview() {
        return dateReview;
    }

    public void setDateReview(LocalDateTime dateReview) {
        if (dateReview == null) {
            this.dateReview = LocalDateTime.now();
        } else {
            this.dateReview = dateReview;
        }
    }


    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        if (activityId <= 0) {
            throw new IllegalArgumentException("ID de l activité  supérieur a 0");
        }
        this.activityId = activityId;
    }

    //conduction pour compareé dans main deux instance
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id == review.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", commentaire='" + commentaire + '\'' +
                ", note=" + note +
                ", dateReview=" + dateReview +
                ", activityId=" + activityId +
                '}';
    }
}
