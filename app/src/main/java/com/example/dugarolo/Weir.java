package com.example.dugarolo;

import org.osmdroid.util.GeoPoint;

public class Weir {
    private String number;
    private String farm;
    private Integer openLevel;
    private GeoPoint position;

    public Weir(String number, String farm, Integer openLevel, GeoPoint position) {
        this.farm = farm;
        this.number = number;
        this.openLevel = openLevel;
        this.position = position;
    }

    public String getFarm() {
        return farm;
    }

    public String getNumber() {
        return number;
    }

    public Integer getOpenLevel() {
        return openLevel;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public void setOpenLevel(Integer openLevel) {
        this.openLevel = openLevel;
    }
}
