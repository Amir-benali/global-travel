package com.globalTravel.services;

import java.util.List;

public interface IServiceActivity<T> {
    boolean ajouter(T t);
    boolean modifier(T t);
    void supprimer(T t);
    List<T> rechercher();
}
