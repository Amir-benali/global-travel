package com.globalTravel.tests;

import com.globalTravel.models.CarDriver;
import com.globalTravel.models.PrivateCar;
import com.globalTravel.services.PrivateCarService;

public class PrivateCarMain {
    public static void main(String[] args) {
        PrivateCarService service = new PrivateCarService();
        CarDriver driver1 =new CarDriver(3,"ahmed","amin","99885544");
//        service.ajouter(new CarDriver("ahmed","amin","99885544"));
//        service.ajouter(new PrivateCar("brand 1","model 1",3));
//        service.modifier(new PrivateCar(2,"brand 2","model 2",2));

//        service.supprimer(new PrivateCar(2,"brand 2","model 2",2));
//        service.ajouter(new PrivateCar("brand 2","model 2",2));
//        service.ajouter(new PrivateCar("brand 4","model 4",1));
//        service.ajouter(new PrivateCar("brand 5","model 5",4));
//        service.supprimer(new CarDriver(2,"aziz","amin","999800815"));
        service.ajouter(new PrivateCar("brand 5","model 5",4 ,driver1));
        System.out.println(service.rechercher());
    }
}
