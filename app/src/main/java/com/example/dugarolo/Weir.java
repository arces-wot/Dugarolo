package com.example.dugarolo;

import org.osmdroid.util.GeoPoint;

public class Weir {
    private String number;
    private String farm;
    private Integer waterLevel;
    private GeoPoint position;

    public Weir(String number, String farm, Integer waterLevel, GeoPoint position) {
        this.farm = farm;
        this.number = number;
        this.waterLevel = waterLevel;
        this.position = position;
    }

    public String getFarm() {
        return farm;
    }

    public String getNumber() {
        return number;
    }

    public Integer getWaterLevel() {
        return waterLevel;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public void setWaterLevel(Integer waterLevel) {
        this.waterLevel = waterLevel;
    }
}
