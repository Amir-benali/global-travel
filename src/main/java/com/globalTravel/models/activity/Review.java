package com.globalTravel.models.activity;
import java.time.LocalDateTime;
import java.util.Objects;

public class Review {
    private int id;
    private String commentaire;
    private int note;
    private LocalDateTime dateReview;
    private String reservationDecision;


    public Review(int id, String commentaire, int note, LocalDateTime dateReview, String reservationDecision) {
        this.id = id;
        this.commentaire = commentaire;
        setNote(note);
        this.dateReview = (dateReview != null) ? dateReview : LocalDateTime.now();
        this.reservationDecision = reservationDecision;
    }

    public Review(String commentaire, int note, String reservationDecision) {
        this.commentaire = commentaire;
        setNote(note);
        this.dateReview = LocalDateTime.now();
        this.reservationDecision = reservationDecision;
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
        this.commentaire = commentaire;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note  comprise entre 1 et 5.");
        }
        this.note = note;
    }

    public LocalDateTime getDateReview() {
        return dateReview;
    }

    public void setDateReview(LocalDateTime dateReview) {
        this.dateReview = dateReview;
    }

    public String getReservationDecision() {
        return reservationDecision;
    }

    public void setReservationDecision(String reservationDecision) {
        this.reservationDecision = reservationDecision;
    }


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
                ", reservationDecision='" + reservationDecision + '\'' +
                '}';
    }
}
