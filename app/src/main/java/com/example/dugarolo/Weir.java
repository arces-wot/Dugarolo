package com.example.dugarolo;

import org.osmdroid.util.GeoPoint;

public class Weir {
    private Integer number;
    private String farm;
    private Integer waterLevel;
    private GeoPoint position;

    public static Weir[] weirs = {
            new Weir(1, "Martinelli", 70, new GeoPoint(44.774749, 10.720185)),
            new Weir(2, "Luceri", 46, new GeoPoint(44.780640, 10.727920))
    };

    public Weir(Integer number, String farm, Integer waterLevel, GeoPoint position) {
        this.farm = farm;
        this.number = number;
        this.waterLevel = waterLevel;
        this.position = position;
    }

    public String getFarm() {
        return farm;
    }

    public Integer getNumber() {
        return number;
    }

    public Integer getWaterLevel() {
        return waterLevel;
    }

    public GeoPoint getPosition() {
        return position;
    }
}
