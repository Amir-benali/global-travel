package com.globalTravel.models;

public class PrivateCar {
    private int id;
    private String brand;
    private String model;
    private int num_place;
    private CarDriver carDriver;

    public PrivateCar(int id, String brand, String model, int num_place, CarDriver carDriver) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.num_place = num_place;
        this.carDriver = carDriver;
    }

    public PrivateCar(  String brand ,String model,int num_place,  CarDriver carDriver) {
        this.carDriver = carDriver;
        this.num_place = num_place;
        this.model = model;
        this.brand = brand;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getNum_place() {
        return num_place;
    }

    public void setNum_place(int num_place) {
        this.num_place = num_place;
    }

    public CarDriver getCarDriver() {
        return carDriver;
    }

    public void setCarDriver(CarDriver carDriver) {
        this.carDriver = carDriver;
    }

    @Override
    public String toString() {
        return "PrivateCar{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", num_place=" + num_place +
                ", carDriver=" + carDriver +
                '}';
    }
}
