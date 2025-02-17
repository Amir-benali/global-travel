package com.globalTravel.services;

import java.util.List;

public interface IActivityService<T> {
    boolean ajouter(T t);
    boolean modifier(T t);
    void supprimer(T t);
    List<T> rechercher();
}
