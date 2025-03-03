package com.globalTravel.models.activity;

import com.globalTravel.models.user.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Activity {
    private int id;
    private Timestamp dateDebut;
    private Timestamp dateFin;
    private String description;
    private String localisation;
    private int prixTotal;
    private String nomActivity;
    private TypeActivity typeActivity;
    private int joinHotelId;
    private int joinVoitureId;
    private int joinVolsId;
    private int user_id;
    private List<User> invitedUsers = new ArrayList<>();

    public Activity(int id, Timestamp dateDebut, Timestamp dateFin, String description, String localisation,
                    int prixTotal, String nomActivity, TypeActivity typeActivity,
                    int joinHotelId, int joinVoitureId, int joinVolsId, int user_id) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.localisation = localisation;
        this.prixTotal = prixTotal;
        this.nomActivity = nomActivity;
        this.typeActivity = typeActivity;
        this.joinHotelId = joinHotelId;
        this.joinVoitureId = joinVoitureId;
        this.joinVolsId = joinVolsId;
        this.user_id = user_id;
    }

    public Activity(Timestamp dateDebut, Timestamp dateFin, String description, String localisation,
                    int prixTotal, String nomActivity, TypeActivity typeActivity,
                    int joinHotelId, int joinVoitureId, int joinVolsId, int user_id) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.localisation = localisation;
        this.prixTotal = prixTotal;
        this.nomActivity = nomActivity;
        this.typeActivity = typeActivity;
        this.joinHotelId = joinHotelId;
        this.joinVoitureId = joinVoitureId;
        this.joinVolsId = joinVolsId;
        this.user_id = user_id;


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Timestamp dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Timestamp getDateFin() {
        return dateFin;
    }

    public void setDateFin(Timestamp dateFin) {
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

    public int getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(int prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getNomActivity() {
        return nomActivity;
    }

    public void setNomActivity(String nomActivity) {
        this.nomActivity = nomActivity;
    }

    public TypeActivity getTypeActivity() {
        return typeActivity;
    }

    public void setTypeActivity(TypeActivity typeActivity) {
        this.typeActivity = typeActivity;
    }

    public int getJoinHotelId() {
        return joinHotelId;
    }

    public void setJoinHotelId(int joinHotelId) {
        this.joinHotelId = joinHotelId;
    }

    public int getJoinVoitureId() {
        return joinVoitureId;
    }

    public void setJoinVoitureId(int joinVoitureId) {
        this.joinVoitureId = joinVoitureId;
    }

    public int getJoinVolsId() {
        return joinVolsId;
    }

    public void setJoinVolsId(int joinVolsId) {
        this.joinVolsId = joinVolsId;
    }

    public void setInvitedUsers(List<User> users) {
        this.invitedUsers = users;
    }
    public List<User> getInvitedUsers() {
        return invitedUsers;
    }
    public void addInvitedUser(User user) {
        this.invitedUsers.add(user);
    }
    public boolean isUserInvited(User user) {
        return this.invitedUsers.contains(user);
    }
    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }


    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", description='" + description + '\'' +
                ", localisation='" + localisation + '\'' +
                ", prixTotal=" + prixTotal +
                ", nomActivity='" + nomActivity + '\'' +
                ", typeActivity=" + typeActivity +
                ", joinHotelId=" + joinHotelId +
                ", joinVoitureId=" + joinVoitureId +
                ", joinVolsId=" + joinVolsId +
                ", user_id=" + user_id +
                '}';
    }
}
